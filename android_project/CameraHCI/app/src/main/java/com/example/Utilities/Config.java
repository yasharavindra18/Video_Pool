package com.example.Utilities;

public class Config {
    // url to get all events list
    private static String url_all_events = "http://192.168.0.6/HCI_Android/get_all_event_details.php";

    public String getAllEventsURL(){
        return url_all_events;
    }
}
