import { Component, Prop, State } from '@stencil/core';
import { StreamingService } from '../../global/streaming';

@Component({
  tag: 'app-profile',
  styleUrl: 'app-profile.css'
})
export class AppProfile {
  @State() streaming = false;
  @State() streamingService: StreamingService = new StreamingService();
  @State() videoElement: HTMLVideoElement;
  @Prop() name: string;
  @State() mediaStream: MediaStream = null;

  componentDidLoad() {
    this.videoElement = document.querySelector("video");
  }

  startLiveStream() {
    navigator.getUserMedia(
      { audio: true, video: true },
      (stream: MediaStream) => {
        this.mediaStream = stream;
        this.videoElement.srcObject = this.mediaStream;
        // Begin Streaming To Server
        this.streamingService.startStreaming(this.mediaStream);
        this.streaming = true;
      },
      (error: MediaStreamError) => {
        console.log(error.message);
        this.streaming = false;
      }
    );
  }

  componentDidUnload() {
    this.stopLiveStream();
  }

  stopLiveStream() {
    if(this.mediaStream) {
      this.streamingService.stopStreaming();
      this.mediaStream.getTracks().forEach(track => track.stop());
      this.streaming = false;
    }
  }

  onStreamButtonClick() {
    if(this.streaming == false) {
      this.startLiveStream();
    } else {
      this.stopLiveStream();
    }
  }

  render() {
    return [
      <ion-header>
        <ion-toolbar color="primary">
          <ion-buttons slot="start">
            <ion-back-button defaultHref="/" />
            <ion-title>Stream</ion-title>
          </ion-buttons>
        </ion-toolbar>
      </ion-header>,

      <ion-content padding>
        <ion-button onClick={() => this.onStreamButtonClick()}>{this.streaming ? "Stop" : "Start"} Live Stream</ion-button>
        <video autoplay style={{ height: '180px', width: '240px' }} />
      </ion-content>
    ];
  }
}
