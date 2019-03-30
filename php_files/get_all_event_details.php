<?php
/*
 * Following code will list all the products
 */
 
// array for JSON response
$response = array();

// include db connect class
//require_once __DIR__ . '/db_connect.php';
require_once __DIR__ . '/db_config.php'; 
// connecting to db
//$db = new DB_CONNECT();
$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
// 
if(!$con){
    die("Connection failed: " . mysqli_connect_error());
}
// get all events from events_list table
$db_query = "SELECT ID, EVENT_NAME FROM events_list";
//$result = mysql_query($db_query) or die(mysql_error());
//echo "Executing Query";
$result = mysqli_query($con, $db_query) or die(mysqli_error($con));
// check for empty result
//echo "Feting details";
//if (mysql_num_rows($result) > 0) {
if (mysqli_num_rows($result) > 0) {
    // looping through all results
    // eventss node
    //echo "in Loop";
    $response["Events"] = array();
    //while ($row = mysql_fetch_array($result)) {
    while ($row = mysqli_fetch_array($result)) {
        // temp user array
        $event = array();
        $event["id"] = $row["ID"];
        $event["Event_Name"] = $row["EVENT_NAME"];

        // push single product into final response array
        array_push($response["Events"], $event);
    }
    // success
    $response["success"] = 1;
 
    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No events found in database";
 
    // echo no users JSON
    echo json_encode($response);
}
//close connection
mysqli_close($con);
?>