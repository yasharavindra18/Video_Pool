import io from 'socket.io-client';

interface Event {
  id: Number
}

interface Stream {
  event: Event,
  start?: boolean,
  end?: boolean
  data?: Blob,
  begin?: number,
}

export class StreamingService {

  private mediaRecorder: MediaRecorder;
  private socket: SocketIOClient.Socket;
  private event: Event = {id: 1};
  private startTimestamp: number;

  init() {
    this.socket = io('http://192.168.43.144:3000');

    // When connection established
    this.socket.on('connect', () => {
      console.log('Connected');
    });

    // When event info received
    this.socket.on('event_info', (event: Event) => {
      this.event = event;
      console.log('Assigned Event: ' + event);
    });
  }

  public probeForEvents() {
    if ("geolocation" in navigator) {
      navigator.geolocation.getCurrentPosition(
        (position: Position) => {
          this.socket.emit('location_info', position);
        },
        (error: PositionError) => {
          console.log(error.message);
        })
    } else {
      alert('Location Services are Unavailable')
    }
  }

  private send(stream: Stream) {
    this.socket.emit('stream', stream);
  }

  public startStream(localStream: MediaStream) {
    this.mediaRecorder = new MediaRecorder(localStream);
    
    // On Start
    this.mediaRecorder.onstart = () => {
      console.log('Stream Started');
      this.startTimestamp = Date.now();
      this.send({event: this.event, start: true, end: false, begin: this.startTimestamp});
    }

    // On Chunk Available
    this.mediaRecorder.ondataavailable = (event: BlobEvent) => {
      console.log('Stream Data Available');
      this.send({event: this.event, start: false, end: false, data: event.data});
    };

    // On Stop
    this.mediaRecorder.onstop = () => {
      console.log('Stream Ended');
      this.send({event: this.event, start: false, end: true});
    }

    // Start Recording
    this.mediaRecorder.start(1000);
  }

  public endStream() {
    this.mediaRecorder.stop();
  }
}