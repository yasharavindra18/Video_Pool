import axios, { AxiosInstance } from 'axios';

export class StreamingService {

  private axiosInstance: AxiosInstance = axios.create({
    baseURL: "http://localhost:3000/api",
    timeout: 10000
  });

  public startStreaming(localStream: MediaStream) {
    const mediaRecorder = new MediaRecorder(localStream);
    mediaRecorder.ondataavailable = (event: BlobEvent) => {
      console.log(event.data.size);
      // Stream the blob to server
    };
    mediaRecorder.start(100);
  }

  private pushToServer(blob: Blob){
      const data = new FormData();
      data.append('file', blob);
    
      return this.axiosInstance.post(`/livestream`, data, {
        headers: {
          'Content-Type': `multipart/form-data; boundary=${blob.size}`,
        },
        timeout: 30000,
      });
  }

  public stopStreaming() {
    // this.connection.close();
  }
}