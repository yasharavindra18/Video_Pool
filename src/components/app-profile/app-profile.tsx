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

  startVideoStream(): any {
    navigator.mediaDevices.getUserMedia({ audio: true, video: true })
      .then((stream) => {
        this.mediaStream = stream;
        var player = document.querySelector('video');
        player.srcObject = stream;

        // const peerConnection = new RTCPeerConnection();
      })
      .catch((err) => {
        console.error(err);
      })
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
        <ion-button onClick={() => this.startVideoStream()}>Start Stream</ion-button>
        <video width="320" height="240" autoPlay controls></video>
      </ion-content>
    ];
  }
}
