package com.example.Utilities;

public class config {


    // public static String url_create_event = "http://10.254.238.171/HCI_Android/addEvent.php";
    // public static String url_get_all_events = "http://qav2.cs.odu.edu/swaroop/HCI/getEvents.php";
    // public static String url_upload_video = "http://10.254.238.171/HCI_Android/video_upload.php";
    public static String url_create_event = "http://172.17.0.1:8081/api/event/create";
    public static String url_get_all_events = "http://172.17.0.1:8081/api/event/nearby";
    public static String url_upload_video = "http://172.17.0.1:8081/api/video/upload";


    //Path to save video on android : /sdcard/myvideo.mp4
    public static String filePath = "/sdcard/";

}
