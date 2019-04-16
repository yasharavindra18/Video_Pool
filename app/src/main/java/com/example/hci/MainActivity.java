package com.example.hci;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
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

import java.io.IOException;

import static android.hardware.Camera.open;

public class MainActivity extends AppCompatActivity {

    boolean recording = false;
    // Variables
    String eventName, eventId, userId;
    double eventLat, eventLng;
    String fileName;
    // Progress Dialog
    private ProgressDialog pDialog;
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
//                new phpVideoUpload().execute();

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
                try {
                    // Prepare Media Recorder
                    prepareMediaRecorder();
                    //Start Recording
                    mediaRecorder.start();
                    // Start Chronometer
                    mchronometer.setBase(SystemClock.elapsedRealtime());
                    mchronometer.setVisibility(View.VISIBLE);
                    mchronometer.start();
                    // If all goes well, update the UI
                    Toast.makeText(MainActivity.this, "Started Recording", Toast.LENGTH_LONG).show();
                    recording = true;
                    capture.setBackgroundResource(R.drawable.pause);
                } catch (Exception e) {
                    recording = false;
                    mchronometer.stop();
                    e.printStackTrace();
                    releaseMediaRecorder();
                    // If something is broken, first update the UI
                    Toast.makeText(MainActivity.this, "Failed to Start Recording", Toast.LENGTH_LONG).show();
//                    finish();
                }
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

        mPreview = new CameraPreview(myContext);
        mPreview.initialize(mCamera);
        cameraPreview.addView(mPreview);

        capture = (Button) findViewById(R.id.button_capture);
        capture.setOnClickListener(captrureListener);

        switchCamera = (Button) findViewById(R.id.button_ChangeCamera);
        switchCamera.setOnClickListener(switchCameraListener);

        //getting data from other intent
        Intent in = getIntent();
        Bundle e = in.getExtras();
        if (e == null) {
            return;
        }
        eventId = e.getString("e_id");
        eventName = e.getString("e_name");
        eventLat = e.getDouble("e_lat");
        eventLng = e.getDouble("e_lng");
        userId = "!"; // TODO
        Log.d("Event Details (ID, USER) : ", eventId + ", " + userId);

        //Setting file name with event name
        fileName = eventId + "_" + userId + ".mp4";
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
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
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
//            mCamera.lock(); // lock camera for later use
        }
    }

    private void prepareMediaRecorder() throws IOException {
        mediaRecorder = new MediaRecorder();

//        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));

        mediaRecorder.setOutputFile("/sdcard/" + fileName);
        mediaRecorder.setMaxDuration(600000); //set maximum duration 60 sec.
        mediaRecorder.setMaxFileSize(50000000); //set maximum file size 50M
        mediaRecorder.prepare();
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }


    /**
     * Background Async Task to upload recorded video
     */
    private class phpVideoUpload extends AsyncTask<Void, Integer, String> {
        /* This Class is an AsyncTask to upload a video to a server on a background thread
         *
         */
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            //super.onPreExecute();
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Uploading Video..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected String doInBackground(Void... params) {

//            JSONObject json = uploadFile();
//            // check for success tag
//            try {
//                int success = json.getInt(TAG_SUCCESS);
//
//                if (success == 1) {
//                    // successfully created Event, Redirect to Maps Activity
//                    //Toast tst = Toast.makeText(this, R.string.tst_EventCreationFailed, Toast.LENGTH_SHORT);
//                    Intent i = new Intent(getApplicationContext(), MapsActivity.class);
//                    startActivity(i);
//
//                    // closing this screen
//                    finish();
//                } else {
//                    // failed to create event
//                    Log.i("Upload Video Failed", "Upload video failed!!!!!");
//                    //Toast tst = (Toast) Toast.makeText("Video Upload Failed", Toast.LENGTH_SHORT);
//                    //tst.show();
//                    // closing this screen
//                    finish();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            return null;
        }

//        @SuppressWarnings("deprecation")
//        private JSONObject uploadFile() {
//            JSONObject responseString;
//            // Building Parameters
//            Map<String, String> params = new HashMap<String, String>();
//
//            /*
//            params need filename,filepath on local,
//            filepath = params.get("filepath");
//            fileName = params.get("fileName");
//            eventId = params.get("event_id");
//             */
//            params.put("filepath", filePath);
//            params.put("fileName", fileName);
//            params.put("event_id", eventID);
//
//            // getting JSON Object
//            // Note that upload url accepts POST method
//            // url contains multipart form data so pass parameters along with the request
//            //
//            JSONObject json = jsonParser.makeHttpRequest(url_upload_video,
//                    "VIDEO", params);
//            // check log cat fro response
//            Log.d("Video Upload Response", json.toString());
//            //responseString = json.toString();
//            responseString = json;
//            return responseString;
//        }

        //@Override
        protected void onPostExecute(Integer result) {
            //Check the return code and update the listener
            Log.d("VideoUploadTask onPostExecute", "updating listener after execution");
        }
    }

}
