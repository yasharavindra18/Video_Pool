package com.example.hci;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.Utilities.JSONParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.hardware.Camera.open;
import static com.example.Utilities.config.filePath;

public class MainActivity extends AppCompatActivity {

    // JSON Node names
    protected static final String TAG_SUCCESS = "success";
    // adding logger to the code
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    //public Event Details Strings
    public static String eventName = "";
    public static String eventID = "";
    public static String eventLat = "";
    public static String eventLong = "";
    //video file
    public static String fileName = "";
    // Progress Dialog
    protected ProgressDialog pDialog;
    //Create JSON Parser
    JSONParser jsonParser = new JSONParser();
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
            Locale.getDefault()).format(new Date());
    boolean recording = false;
    private android.hardware.Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mediaRecorder;
    private Button capture, switchCamera;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    View.OnClickListener switchCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // get the number of cameras
            if (!recording) {
                int camerasNumber = android.hardware.Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    // release the old camera instance
                    // switch camera, from the front and the back and vice versa

                    releaseCamera();
                    chooseCamera();
                } else {
                    Toast toast = Toast.makeText(myContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    };
    //Displaying timer using chronometer
    private Chronometer mchronometer;
    final View.OnClickListener captrureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (recording) {
                //Stop Chronometer
                mchronometer.stop();
                // stop recording and release camera
                mediaRecorder.stop(); // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                Toast.makeText(MainActivity.this, "Video captured!", Toast.LENGTH_LONG).show();


                //Upload the video to the server from here
                new UploadVideoToAPI(MainActivity.this).execute();

                /*
                videoUploadTask vUT = new videoUploadTask();
                vUT.setEventId(eventID);
                vUT.setFileName(fileName);
                vUT.setFilePath(filePath);

                vUT.executevideoUpload();
                */

                recording = false;

                capture.setBackgroundResource(R.drawable.play);
            } else {
                if (!prepareMediaRecorder()) {
                    Toast.makeText(MainActivity.this, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                    finish();
                }
                // work on UiThread for better performance
                runOnUiThread(new Runnable() {
                    public void run() {
                        // If there are stories, add them to the table

                        try {
                            //Start Recording
                            Toast.makeText(MainActivity.this, "Started Recording", Toast.LENGTH_LONG).show();
                            mediaRecorder.start();
                            // Start Chronometer
                            mchronometer.setBase(SystemClock.elapsedRealtime());
                            mchronometer.setVisibility(View.VISIBLE);
                            mchronometer.start();

                        } catch (final Exception ex) {
                            // Log.i("---","Exception in thread");
                        }
                    }
                });

                recording = true;
                capture.setBackgroundResource(R.drawable.pause);
            }
        }
    };
    //ProgressBar
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // to Keep the screen always on
        myContext = this;
        initialize();
    }

    public void initialize() {
        cameraPreview = (LinearLayout) findViewById(R.id.camera_preview);

        //Setting chronometer object on layout
        mchronometer = (Chronometer) findViewById(R.id.linear_chronometer);

        mPreview = new CameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);

        capture = (Button) findViewById(R.id.button_capture);
        capture.setOnClickListener(captrureListener);

        switchCamera = (Button) findViewById(R.id.button_ChangeCamera);
        switchCamera.setOnClickListener(switchCameraListener);

        //getting data from other intent
        Intent in = getIntent();
        eventName = in.getStringExtra("EventName");
        eventID = in.getStringExtra("EventID");
        eventLat = in.getStringExtra("Event_Lat");
        eventLong = in.getStringExtra("Event_Long");
        Log.d("Event Details (ID, NAME) : ", eventID + ", " + eventName);

        //Setting file name with event name
        fileName = eventName + "_" + eventID + "_" + timeStamp + ".mp4";
    }

    public void onResume() {
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
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
                cameraFront = true;
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
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    private boolean hasCamera(Context context) {
        // check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
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
        // when on Pause, release camera in order to be used from other
        // applications
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

        mediaRecorder = new MediaRecorder();

        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));

        mediaRecorder.setOutputFile(filePath + fileName);
        mediaRecorder.setMaxDuration(600000); //set maximum duration 60 sec.
        mediaRecorder.setMaxFileSize(50000000); //set maximum file size 50M

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
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
