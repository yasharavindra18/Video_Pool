export class StreamingService {

  startStreaming(localStream: MediaStream) {
    const mediaRecorder = new MediaRecorder(localStream);
    mediaRecorder.ondataavailable = (event: BlobEvent) => {
      console.log(event);
      // Stream the blob to server
    };
    mediaRecorder.start(1000);
  }

  stopStreaming() {
    // this.connection.close();
  }
}