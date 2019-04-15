<?php
/*
 * Following code will create Events from post request
 */

// array for JSON response
$response = array();
// check for required fields


if (isset($_POST['event_name']) && isset($_POST['event_description']) && isset($_POST['event_place'])) {
 
    $name = $_POST['event_name'];
    $price = $_POST['event_place'];
    $description = $_POST['event_description'];
    $lat = $_POST['event_lat'];
    $long = $_POST['event_long'];
    // include db connect class
    require_once __DIR__ . '/db_config.php'; 
    // connecting to db
    $con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
    // 
    if(!$con){
        die("Connection failed: " . mysqli_connect_error());
    }
    //Mysql Inserting a Row
    $db_query = "INSERT INTO events_list(event_name, latitude, longitude, description) VALUES ('$name', '$lat', '$long', '$description')";
     
    $result = mysqli_query($con, $db_query) or die(mysqli_error($con));

    // Update query to generate eventid as combination of id and m 
    $db_update_query = "UPDATE `events_list` SET `event_id`= Concat('m', cast(id AS CHAR));";

    $result = mysqli_query($con, $db_update_query) or die(mysqli_error($con));

    // Sample Insert Query
    //INSERT INTO events_list(event_name, latitude, longitude, description ) VALUES('Event1', '43.5432', '87.5371','Event 1 happening');
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Product successfully created.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred wile inserting into database.";
 
        // echoing JSON response
        echo json_encode($response);
    }
}else{
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}

?>