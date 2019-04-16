package com.example.hci;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.Utilities.config;

import java.io.IOException;
import java.util.concurrent.Executors;

import static android.hardware.Camera.open;

public class MainActivity extends AppCompatActivity {

    boolean isRecording = false;
    private boolean isFrontCamera = false;

    private String eventName;
    private String eventID;
    private double eventLat;
    private double eventLng;
    private android.hardware.Camera mCamera;
    private CameraPreview mPreview;
    private final View.OnClickListener switchCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // get the number of cameras
            if (!isRecording) {
                int camerasNumber = android.hardware.Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    // release the old camera instance
                    // switch camera, from the front and the back and vice versa
                    releaseCamera();
                    chooseCamera();
                } else {
                    Toast toast = Toast.makeText(MainActivity.this, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    };
    private MediaRecorder mediaRecorder;
    private final Runnable recordAndUpload = new Runnable() {
        @Override
        public void run() {
            try {
                MediaUploader mediaUploader = new MediaUploader(config.api_host, 8082);
                mediaUploader.run();
                mediaUploader.handshake(eventID, "1", "MP4");
                mediaRecorder = new MediaRecorder();
                mCamera.unlock();
                mediaRecorder.setCamera(mCamera);
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
                mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
                mediaRecorder.setOutputFile(mediaUploader.getFileDescriptor());
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Failed to Set Up Media Recorder", Toast.LENGTH_LONG).show();
                MainActivity.this.finish();
                return;
            }

            try {
                mediaRecorder.prepare();
            } catch (IllegalStateException e) {
                releaseMediaRecorder();
                Toast.makeText(MainActivity.this, "IllegalStateException when Preparing Media Recorder", Toast.LENGTH_LONG).show();
                MainActivity.this.finish();
            } catch (IOException e) {
                releaseMediaRecorder();
                Toast.makeText(MainActivity.this, "Failed to Prepare Media Recorder", Toast.LENGTH_LONG).show();
                MainActivity.this.finish();
            }
        }
    };
    private Button capture, switchCamera;
    private Chronometer mChronometer;
    //Displaying timer using chronometer
    private final View.OnClickListener captureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isRecording) {
                //Stop Chronometer
                mChronometer.stop();
                // stop isRecording and release camera
                mediaRecorder.stop(); // stop the isRecording
                releaseMediaRecorder(); // release the MediaRecorder object
                Toast.makeText(MainActivity.this, "Video captured!", Toast.LENGTH_LONG).show();
                isRecording = false;
                capture.setBackgroundResource(R.drawable.play);
            } else {
                prepareMediaRecorder();
                // work on UiThread for better performance
                runOnUiThread(new Runnable() {
                    public void run() {
                        // If there are stories, add them to the table
                        try {
                            //Start Recording
                            Toast.makeText(MainActivity.this, "Started Recording", Toast.LENGTH_LONG).show();
                            mediaRecorder.start();
                            // Start Chronometer
                            mChronometer.setBase(SystemClock.elapsedRealtime());
                            mChronometer.setVisibility(View.VISIBLE);
                            mChronometer.start();

                        } catch (final Exception ex) {
                            // Log.i("---","Exception in thread");
                        }
                    }
                });

                isRecording = true;
                capture.setBackgroundResource(R.drawable.pause);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Get Event Details From State
        if (getIntent().getExtras() != null) {
            eventID = getIntent().getExtras().getString("e_id");
            eventName = getIntent().getExtras().getString("e_name");
            eventLat = getIntent().getExtras().getDouble("e_lat");
            eventLng = getIntent().getExtras().getDouble("e_lng");
        }

        // Initialize Camera
        LinearLayout cameraPreview = findViewById(R.id.camera_preview);

        //Setting chronometer object on layout
        mChronometer = findViewById(R.id.linear_chronometer);

        mPreview = new CameraPreview(MainActivity.this, mCamera);
        cameraPreview.addView(mPreview);

        capture = findViewById(R.id.button_capture);
        capture.setOnClickListener(captureListener);

        switchCamera = findViewById(R.id.button_ChangeCamera);
        switchCamera.setOnClickListener(switchCameraListener);
    }

    public void onResume() {
        super.onResume();
        if (!hasCamera(MainActivity.this)) {
            Toast toast = Toast.makeText(MainActivity.this, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }

        if (mCamera == null) {
            //if front camera doesnt exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                switchCamera.setVisibility(View.GONE);
            }
            mCamera = open(findBackFacingCamera());
            mPreview.refreshCamera(mCamera);
        }
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = android.hardware.Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(i, info);
            if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                isFrontCamera = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the back facing camera
        // get the number of cameras
        int numberOfCameras = android.hardware.Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(i, info);
            if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                isFrontCamera = false;
                break;
            }
        }
        return cameraId;
    }

    private boolean hasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public void chooseCamera() {
        // if the camera preview is the front
        if (isFrontCamera) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = android.hardware.Camera.open(cameraId);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = android.hardware.Camera.open(cameraId);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other applications
        releaseCamera();
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private boolean prepareMediaRecorder() {
        Executors.newSingleThreadExecutor().submit(recordAndUpload);
        return true;
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }


}
