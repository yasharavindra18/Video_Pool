package com.hci.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hci.Utilities.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.hci.Utilities.config.url_create_event;


public class NewEventActivity extends AppCompatActivity {

    // JSON Node names
    private static final String TAG_SUCCESS = "status";
    //Create JSON Parser
    private JSONParser jsonParser = new JSONParser();
    private String event_lat = "";
    private String event_lng = "";
    //Edit Text
    private EditText textEventName;
    private EditText textEventDescription;
    private TextView textEventPlace;
    private Button btnAddEvent;
    // Progress Dialog
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);

        //Getting data from another Intent
        Intent intent = getIntent();
        event_lat = intent.getStringExtra("current_lat");
        event_lng = intent.getStringExtra("current_long");


        //Edit Text
        textEventName = findViewById(R.id.text_event_name);
        textEventDescription = findViewById(R.id.text_event_description);
        textEventPlace = findViewById(R.id.label_select_place);
        btnAddEvent = findViewById(R.id.button_add_event);
        // button click event
        btnAddEvent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // creating new Event in background thread
                new AddNewEvent().execute();
            }
        });
    }

    /**
     * Background Async Task to Create new Event
     */
    class AddNewEvent extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         */
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
         */
        @SuppressLint("WrongThread")
        protected String doInBackground(String... args) {
            String eventName = textEventName.getText().toString();
            ;
            String EventDescription = textEventDescription.getText().toString();
            String EventPlace = textEventPlace.getText().toString();
            Map<String, String> params = new HashMap<String, String>();
            params.put("eventName", eventName);
            params.put("location", event_lat + ',' + event_lng);
            params.put("event_description", EventDescription);

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_event,"POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());
            // check for success tag
            try {
//                int success = json.getInt(TAG_SUCCESS);
                String success = json.getString(TAG_SUCCESS);
                String eventId = json.getString("eventId");

//                if (success == 1) {
                if (success.contains("New Event Created!")) {
                    // successfully created Event, Redirect to Maps Activity
                    //Toast tst = Toast.makeText(this, R.string.tst_EventCreationFailed, Toast.LENGTH_SHORT);
                    tstmsg = String.valueOf(R.string.tst_EventCreationSuccess);
                    Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(i);

                    // closing this screen
                    finish();
                } else {
                    // failed to create event
                    Log.i("Empty Response,", "Event Creation failed");
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
         **/
        protected void onPostExecute(String file_url) {
            //displays a message to user about the event addition
            Toast tst = Toast.makeText(getApplicationContext(), tstmsg, Toast.LENGTH_LONG);
            tst.show();
            // dismiss the dialog once done
            pDialog.dismiss();
        }

    }

}
