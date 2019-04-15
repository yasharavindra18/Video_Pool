package com.example.EventUtilities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.Utilities.JSONParser;
import com.example.hci.MainActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.Utilities.config.url_create_event;
import static com.example.Utilities.config.url_upload_video;

public class videoUploadTask extends AppCompatActivity {


    //Create JSON Parser
    JSONParser jsonParser = new JSONParser();

    // Progress Dialog
    private ProgressDialog pDialog;

    //Video parameters
    private static String filePath = "";
    private static String fileName = "";
    private static String EventId = "";

    //video parameters setters and getters
    public void setFilePath(String filePath_L){
        filePath = filePath_L;
    }

    public void setFileName(String fileName_L){
        fileName = fileName_L;
    }

    public void setEventId(String EventId_L){
        EventId = EventId_L;
    }

    public String getFilePath(){
        return filePath;
    }

    public String getFileName(){
        return fileName;
    }

    public String getEventId(){
        return EventId;
    }

    public void executevideoUpload(){
        //execute video upload
        new VideoUpload().execute();
    }

    /**
     * Background Async Task to upload recorded video
     * */
    class VideoUpload extends AsyncTask<Void, Integer, String> {
        /* This Class is an AsyncTask to upload a video to a server on a background thread
         *
         */
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            //super.onPreExecute();
            super.onPreExecute();
            pDialog = new ProgressDialog(videoUploadTask.this);
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


            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = "";
            // Building Parameters
            Map<String, String> params = new HashMap<String, String>();

            /*
            params need filename,filepath on local,
            filepath = params.get("filepath");
            fileName = params.get("fileName");
            eventId = params.get("event_id");
             */
            params.put("filepath",filePath);
            params.put("fileName",fileName);
            params.put("event_id",EventId);

            // getting JSON Object
            // Note that upload url accepts POST method
            // url contains multipart form data so pass parameters along with the request
            //
            JSONObject json = jsonParser.makeHttpRequest(url_upload_video,
                    "VIDEO", params);
            // check log cat fro response
            Log.d("Video Upload Response", json.toString());
            responseString = json.toString();
            return responseString;
        }

        //@Override
        protected void onPostExecute(Integer result) {
            //Check the return code and update the listener
            Log.d("VideoUploadTask onPostExecute", "updating listener after execution");
        }
    }
}
