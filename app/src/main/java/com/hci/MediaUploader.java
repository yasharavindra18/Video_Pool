package com.hci;

import android.os.HandlerThread;
import android.os.ParcelFileDescriptor;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public final class MediaUploader extends HandlerThread {

    private final String localhost;
    private final int port;
    private Socket socket;

    public MediaUploader(String localhost, int port) {
        super(MediaUploader.class.getName());
        this.localhost = localhost;
        this.port = port;
        this.run();
    }

    @Override
    public void run() {
        try {
            this.socket = new Socket(this.localhost, this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handshake(String eventId, String userId, String fileFormat) throws IOException {
        synchronized (this) {
            OutputStream outputStream = socket.getOutputStream();
            String metaString = "META" + "{\"event_id\":\"" + eventId + "\",\"user_id\":\"" + userId + "\",\"file_format\":\"" + fileFormat + "\"}\n";
            outputStream.write(metaString.getBytes());
            outputStream.flush();
        }
    }

    public FileDescriptor getFileDescriptor() {
        return ParcelFileDescriptor.fromSocket(socket).getFileDescriptor();
    }


}
