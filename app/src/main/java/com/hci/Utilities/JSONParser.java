package com.hci.Utilities;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static com.hci.Utilities.Constants.CONNECTION_TIMEOUT;
import static com.hci.Utilities.Constants.READ_TIMEOUT;
import static com.hci.Utilities.Constants.UNSUCCESSFUL_MESSAGE;

public class JSONParser {
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    static HttpURLConnection conn;
    static URL url = null;
    static String boundary = "*****";
    static String filepath = "";
    static String fileName = "";
    static String eventId = "";
    static String lineEnd = "\r\n";
    static String twoHyphens = "--";
    // Video related variables
    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    // setting maximum size of video
    int maxBufferSize = 50 * 1024 * 1024;

    // constructor
    public JSONParser() {

    }

    // function get json from url
    // by making HTTP POST or GET method

    /**
     * public JSONObject makeHttpRequest(String url_link, String method,
     * List<NameValuePair> params) {
     */
    public JSONObject makeHttpRequest(String url_link, String method,
                                      Map<String, String> params) {
        // Making HTTP request
        try {
            // Enter URL address where your php file resides
            url = new URL(url_link);
            // Setup HttpURLConnection class to send and receive data from php
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);

            // check for request method
            if (method == "POST") {
                // request method is POST
                // defaultHttpClient
                /**
                 DefaultHttpClient httpClient = new DefaultHttpClient();
                 HttpPost httpPost = new HttpPost(url);
                 httpPost.setEntity(new UrlEncodedFormEntity(params));

                 HttpResponse httpResponse = httpClient.execute(httpPost);
                 HttpEntity httpEntity = httpResponse.getEntity();
                 is = httpEntity.getContent();
                 */
                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setUseCaches(false);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder();
                if (!params.isEmpty()) {
                    //int i = params.size();
                    // Iterate over map to set the values
                    for (String i : params.keySet()) {
                        builder.appendQueryParameter(i, params.get(i));
                    }
                    String query = builder.build().getEncodedQuery();
                    // Open connection for sending data
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    writer.close();
                    os.close();
                    conn.connect();
                } else {
                    // Code if the params are empty ---- to be written
                }
            } else if (method == "GET") {
                // request method is GET
                /**
                 DefaultHttpClient httpClient = new DefaultHttpClient();
                 String paramString = URLEncodedUtils.format(params, "utf-8");
                 url += "?" + paramString;
                 HttpGet httpGet = new HttpGet(url);

                 HttpResponse httpResponse = httpClient.execute(httpGet);
                 HttpEntity httpEntity = httpResponse.getEntity();
                 is = httpEntity.getContent();
                 */
                /** New implementation of the same class as some of the libraries are missing.
                 **/
                conn.setRequestMethod("GET");
                // setDoOutput to true as we recieve data from json file
                conn.setDoOutput(true);


            } else if (method == "VIDEO") {

                // All video uploads are handled here

                //Read input file
                if (!params.isEmpty()) {
                    filepath = params.get("filepath");
                    fileName = params.get("fileName");
                    eventId = params.get("event_id");
                }
                File sourceFile = new File(filepath + fileName);
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                //set connection properties
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                //OutputStream os = new
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                //dos.writeBytes("Content-Disposition: form-data; name=\"event_id\"" +lineEnd + lineEnd + eventId + lineEnd + "; name=\"filename\"; filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"event_id\"" + lineEnd + lineEnd + eventId + lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"filename\"; filename=\"" + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                bytesAvailable = fileInputStream.available();
                Log.i("Video Upload", "Initial .available : " + bytesAvailable);

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

//                serverResponseCode = conn.getResponseCode();

                fileInputStream.close();
                dos.flush();
                dos.close();

            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            int response_code = conn.getResponseCode();
            // Check if successful connection made
            if (response_code == HttpURLConnection.HTTP_OK || response_code == HttpURLConnection.HTTP_CREATED) {
                // Read data sent from server
                is = conn.getInputStream();
                //InputStream is
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                json = sb.toString();
            } else {
                json = UNSUCCESSFUL_MESSAGE + response_code;
                Log.e("RESPONSE CODE ERROR", response_code + "please check the url");
            }

        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        } finally {
            //Disconnect the stream after conversion is complete
            conn.disconnect();
        }

        // return JSON String
        return jObj;

    }
}
