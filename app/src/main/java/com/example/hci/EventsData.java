package com.example.hci;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.Utilities.config.url_get_all_events;

public class EventsData extends AsyncTask<String, String, String> {
    public final static ArrayList<String> rec = new ArrayList<String>();
    public final static ArrayList<String> lats = new ArrayList<String>();
    public final static ArrayList<String> longs = new ArrayList<String>();
    public final static ArrayList<String> id = new ArrayList<String>();
    //ProgressDialog pdLoading = new ProgressDialog(GetHomes.this);
    HttpURLConnection conn;
    URL url = null;
    private TaskCompleted mCallback;

    //this method will interact with UI, here display loading message
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //pdLoading.setMessage("\tLoading...");
        //pdLoading.setCancelable(false);
        //pdLoading.show();

    }

    // This method does not interact with UI, You need to pass result to onPostExecute to display
    @Override
    protected String doInBackground(String... params) {
        try {
            // Enter URL address where your php file resides
            // url = new URL("http://qav2.cs.odu.edu/swaroop/HCI/getEvents.php");
            // Append parameters to URL
            Uri.Builder builder = new Uri.Builder()
                    // .appendQueryParameter("latitude", params[0])
                    // .appendQueryParameter("longitude", params[1]);
                    .appendQueryParameter("lat", params[0])
                    .appendQueryParameter("lng", params[1]);
            String query = builder.build().getEncodedQuery();
            url = new URL(url_get_all_events + "?" + query);


        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return e.toString();
        }
        try {

            // Setup HttpURLConnection class to send and receive data from php
            conn = (HttpURLConnection) url.openConnection();
            // conn.setRequestMethod("POST");
            conn.setRequestMethod("GET");

            // setDoInput and setDoOutput method depict handling of both send and receive
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.connect();

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return e1.toString();
        }

        try {

            int response_code = conn.getResponseCode();

            // Check if successful connection made
            if (response_code == HttpURLConnection.HTTP_OK) {

                // Read data sent from server
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                // Pass data to onPostExecute method
                return (result.toString());

            } else {

                return ("unsuccessful");
            }

        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        } finally {
            conn.disconnect();
        }


    }

    // this method will interact with UI, display result sent from doInBackground method
    @Override
    protected void onPostExecute(String result) {

        //pdLoading.dismiss();
        Log.i("result", String.valueOf(result));
        try {
            JSONArray events = new JSONArray(result);
            lats.clear();
            longs.clear();
            for (int i = 0; i < events.length(); i++) {
                JSONObject c = events.getJSONObject(i);
                // lats.add(c.getString("Event_lat"));
                // longs.add(c.getString("Event_long"));
                // rec.add(c.getString("Event_Name"));
                // id.add(c.getString("id"));
                lats.add(c.getString("location").split(",")[0]);
                longs.add(c.getString("location").split(",")[1]);
                rec.add(c.getString("eventName"));
                id.add(c.getString("eventId"));
            }
//            mCallback.onTaskComplete(lats);
            Log.i("result", lats.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
