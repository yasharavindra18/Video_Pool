package com.example.camerahci;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class CameraControl extends AppCompatActivity {
    private static final String LOG_TAG =
            MainActivity.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EVENTS = "Events";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "Event_Name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_control);

        //Getting data from another Intent
        Intent intent = getIntent();
        String id = intent.getStringExtra(TAG_ID);
        String name = intent.getStringExtra(TAG_NAME);

        String DisplayMessage = R.string.toast_Event_Record + " : "+name;

        //Setting the title of the screen suing activity
        final Activity activity = this;
        activity.setTitle(name);
        Log.d(LOG_TAG,"--> User in Event Cluster : "+id+", "+name+"   !!!");
        Toast tst = Toast.makeText(this, DisplayMessage, Toast.LENGTH_SHORT);
        tst.show();

    }

    public void switchAppCamera(View view) {
        Log.d(LOG_TAG,"--> Switching Camera !!!");
        Toast tst = Toast.makeText(this, R.string.toast_Switch_camera, Toast.LENGTH_SHORT);
        tst.show();
    }

    public void startRecording(View view) {
        Log.d(LOG_TAG,"--> Started Recording !!!");
        Toast tst = Toast.makeText(this, R.string.toast_Record_Start, Toast.LENGTH_SHORT);
        tst.show();

    }
}
