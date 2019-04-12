package com.example.EventUtilities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.Utilities.JSONParser;
import com.example.hci.MapsActivity;
import com.example.hci.R;
import static com.example.Utilities.config.url_create_event;
import static com.example.hci.R.string.tst_EventCreationSuccess;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class NewEventActivity extends AppCompatActivity {

    //Create JSON Parser
    JSONParser jsonParser = new JSONParser();

    // Progress Dialog
    private ProgressDialog pDialog;

    //Edit Text
    EditText In_eventName;
    EditText In_eventDescription;
    EditText In_eventPlace;

    //URL
    //String url =

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
//    private static final String TAG_EventName = "event_name";
//    private static final String TAG_EventDescription = "event_description";
//    private static final String TAG_EventPlace = "event_place";

    //Latitude and longitude Strings
    String Current_Lat = "";
    String Current_Long = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);

        //Getting data from another Intent
        Intent intent = getIntent();
        Current_Lat = intent.getStringExtra("current_lat");
        Current_Long = intent.getStringExtra("current_long");


        //Edit Text
        In_eventName = (EditText) findViewById(R.id.text_event_name);
        In_eventDescription = (EditText) findViewById(R.id.editText3);
        In_eventPlace = (EditText) findViewById(R.id.text_event_name);

        Button btnAddEvent = (Button) findViewById(R.id.addEventButton);
        // button click event
        btnAddEvent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new Event in background thread
                new addNewEvent().execute();
            }
        });
    }

    /**
     * Background Async Task to Create new Event
     * */
    class addNewEvent extends AsyncTask<String, String, String>{
        /**
         * Before starting background thread Show Progress Dialog
         * */
        public String tstmsg = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewEventActivity.this);
            pDialog.setMessage("Creating Event..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        /**
         * Creating product
         * */
        @SuppressLint("WrongThread")
        protected String doInBackground(String... args) {
            String EventName = In_eventName.getText().toString();;
            String EventDescription = In_eventDescription.getText().toString();
            String EventPlace = In_eventPlace.getText().toString();

            //Get Latitude and longitudes from intents
            //Intent intnt = new Intent();
            //Current_Lat = intnt.getStringExtra("current_lat");
            //Current_Long = intnt.getStringExtra("current_long");


            //Store the values as Strings and then to Json Attributes.

            // Building Parameters
            Map<String,String> params = new HashMap<String, String>();

            //add values according to key
            params.put("event_name",EventName);
            params.put("event_description",EventDescription);
            params.put("event_place",EventPlace);
            params.put("event_lat",Current_Lat);
            params.put("event_long",Current_Long);

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_event,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());
            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully created Event, Redirect to Maps Activity
                    //Toast tst = Toast.makeText(this, R.string.tst_EventCreationFailed, Toast.LENGTH_SHORT);
                    tstmsg = String.valueOf(R.string.tst_EventCreationSuccess);
                    Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                } else {
                    // failed to create event
                    Log.i("Empty Response,","Event Creation failed");
                    tstmsg = String.valueOf(R.string.tst_EventCreationFailed);
                    //Toast tst = Toast.makeText(this, R.string.tst_EventCreationFailed, Toast.LENGTH_SHORT);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            //displays a message to user about the event addition
            Toast tst = Toast.makeText(getApplicationContext(), tstmsg, Toast.LENGTH_LONG);
            tst.show();
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }

}
