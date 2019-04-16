package com.example.hci;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.Utilities.config.filePath;
import static com.example.Utilities.config.url_upload_video;

/**
 * Background Async Task to upload recorded video
 */
class UploadVideoToAPI extends AsyncTask<Void, Integer, String> {
    private MainActivity mainActivity;

    public UploadVideoToAPI(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    /* This Class is an AsyncTask to upload a video to a server on a background thread
     *
     */
    @Override
    protected void onPreExecute() {
        // setting progress bar to zero
        //super.onPreExecute();
        super.onPreExecute();
        mainActivity.pDialog = new ProgressDialog(mainActivity);
        mainActivity.pDialog.setMessage("Uploading Video..");
        mainActivity.pDialog.setIndeterminate(false);
        mainActivity.pDialog.setCancelable(true);
        mainActivity.pDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {

    }

    @Override
    protected String doInBackground(Void... params) {

        JSONObject json = uploadFile();
        // check for success tag
        try {
            int success = json.getInt(MainActivity.TAG_SUCCESS);

            if (success == 1) {
                // successfully created Event, Redirect to Maps Activity
                //Toast tst = Toast.makeText(this, R.string.tst_EventCreationFailed, Toast.LENGTH_SHORT);
                Intent i = new Intent(mainActivity.getApplicationContext(), MapsActivity.class);
                mainActivity.startActivity(i);

                // closing this screen
                mainActivity.finish();
            } else {
                // failed to create event
                Log.i("Upload Video Failed", "Upload video failed!!!!!");
                //Toast tst = (Toast) Toast.makeText("Video Upload Failed", Toast.LENGTH_SHORT);
                //tst.show();
                // closing this screen
                mainActivity.finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    private JSONObject uploadFile() {
        JSONObject responseString;
        // Building Parameters
        Map<String, String> params = new HashMap<String, String>();

        /*
        params need filename,filepath on local,
        filepath = params.get("filepath");
        fileName = params.get("fileName");
        eventId = params.get("event_id");
         */
        params.put("filepath", filePath);
        params.put("fileName", MainActivity.fileName);
        params.put("event_id", MainActivity.eventID);

        // getting JSON Object
        // Note that upload url accepts POST method
        // url contains multipart form data so pass parameters along with the request
        //
        JSONObject json = mainActivity.jsonParser.makeHttpRequest(url_upload_video,
                "VIDEO", params);
        // check log cat fro response
        Log.d("Video Upload Response", json.toString());
        //responseString = json.toString();
        responseString = json;
        return responseString;
    }

    //@Override
    protected void onPostExecute(Integer result) {
        //Check the return code and update the listener
        Log.d("VideoUploadTask onPostExecute", "updating listener after execution");
    }
}
