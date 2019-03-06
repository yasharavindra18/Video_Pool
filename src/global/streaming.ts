export class StreamingService {

  private mediaRecorder: MediaRecorder;

  public startStreaming(localStream: MediaStream) {
    this.mediaRecorder = new MediaRecorder(localStream);
    this.mediaRecorder.ondataavailable = (event: BlobEvent) => {
      // Stream the blob to server
      this.pushToServer(event.data);
    };
    this.mediaRecorder.start(100);
  }

  private pushToServer(blob: Blob) {
    // Create Blob from Data
    const data = new FormData();
    data.append('video', blob, 'sender_id');
    // Generate Headers
    const headers = new Headers();
    // Multipart chunked uploads commented out for now until fixed
    // headers.append('Content-Type', `multipart/form-data; boundary=`);

    // Push Chunk to Server
    fetch('http://localhost:3000/api/video/upload', {
      method: "POST",
      headers: headers,
      body: data
    }).then(
      response => {
        console.log(response);
      },
      error => {
        console.error(error);
      });
  }

  public stopStreaming() {
    this.mediaRecorder.stop();
  }
}