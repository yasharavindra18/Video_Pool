import { Component, Prop, State } from '@stencil/core';
import { sayHello } from '../../helpers/utils';

@Component({
  tag: 'app-profile',
  styleUrl: 'app-profile.css'
})
export class AppProfile {

  @State() state = false;
  @Prop() name: string;
  @State() mediaStream = null;

  formattedName(): string {
    if (this.name) {
      return this.name.substr(0, 1).toUpperCase() + this.name.substr(1).toLowerCase();
    }
    return '';
  }

  getUserMedia(options, successCallback, failureCallback) {
    var api = navigator.getUserMedia || navigator['webkitGetUserMedia'] ||
      navigator['mozGetUserMedia'] || navigator['msGetUserMedia'];
    if (api) {
      return api.bind(navigator)(options, successCallback, failureCallback);
    }
  }
  
  @State() pc1: any;
  @State() pc2: any;
  @State() theStreamB: any;
  
  getStream() {
    if (!navigator.getUserMedia && !navigator['webkitGetUserMedia'] &&
      !navigator['mozGetUserMedia'] && !navigator['msGetUserMedia']) {
      alert('User Media API not supported.');
      return;
    }
    
    const constraints = {
      video: true
    };

    this.getUserMedia(constraints, (stream: any) => {
      this.addStreamToVideoTag(stream, 'localVideo');
  
      // RTCPeerConnection is prefixed in Blink-based browsers.
      window['RTCPeerConnection'] = window['RTCPeerConnection'] || window['webkitRTCPeerConnection'];
      this.pc1 = new RTCPeerConnection(null);
      this.pc1.addStream(stream);
      this.pc1.onicecandidate = event => {
        if (event.candidate == null) return;
        this.pc2.addIceCandidate(new RTCIceCandidate(event.candidate));
      };
  
      this.pc2 = new RTCPeerConnection(null);
      this.pc2.onaddstream = event => {
        this.theStreamB = event.stream;
        this.addStreamToVideoTag(event.stream, 'remoteVideo');
      };
      this.pc2.onicecandidate = event => {
        if (event.candidate == null) return;
        this.pc1.addIceCandidate(new RTCIceCandidate(event.candidate));
      };
  
      this.pc1.createOffer({offerToReceiveVideo: 1})
        .then(desc => {
          this.pc1.setLocalDescription(desc);
          this.pc2.setRemoteDescription(desc);
          return this.pc2.createAnswer({offerToReceiveVideo: 1});
        })
        .then(desc => {
          this.pc1.setRemoteDescription(desc);
          this.pc2.setLocalDescription(desc);
        })
        .catch(err => {
          console.error('createOffer()/createAnswer() failed ' + err);
        });
    }, function (err) {
      alert('Error: ' + err);
    });
  }
  
  addStreamToVideoTag(stream, tag) {
    var mediaControl: any = document.getElementById(tag);
    if ('srcObject' in mediaControl) {
      mediaControl.srcObject = stream;
      mediaControl.src = (window.URL || window['webkitURL']).createObjectURL(stream);
    } else if (navigator['mozGetUserMedia']) {
      mediaControl.mozSrcObject = stream;
    }
  }

  render() {
    return [
      <ion-header>
        <ion-toolbar color="primary">
          <ion-buttons slot="start">
            <ion-back-button defaultHref="/" />
          </ion-buttons>
        </ion-toolbar>
      </ion-header>,

      <ion-content padding>
        <p>
          {sayHello()}! My name is {this.formattedName()}. My name was passed in through a
          route param!
        </p>

        <ion-item>
          <ion-label>Setting ({this.state.toString()})</ion-label>
          <ion-toggle
            checked={this.state}
            onIonChange={ev => (this.state = ev.detail.checked)}
          />
        </ion-item>
        <ion-button onClick={() => this.getStream()}>Start Stream</ion-button>
        <video autoplay id="remoteVideo" style={{"height":"180px", "width": "240px"}}></video>
        <video autoplay id="localVideo" style={{"height":"180px", "width": "240px"}}></video>
      </ion-content>
    ];
  }
}
