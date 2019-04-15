<?php
/*
 * Following code will create Events from post request
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['event_id'])){

    if(isset($_FILES['filename']['name'])) {
        //setting max filesize
        $maxsize = 52428800; // 50MB
        //data from request
        $event_id = $_POST['event_id'];
        $name = $_FILES['filename']['name'];
        $targetdir = "videos/";
        $target_file = $targetdir . $_FILES['filename']['name'];
        // Select file type
        $videoFileType = strtolower(pathinfo($target_file,PATHINFO_EXTENSION));
        //echo $videoFileType;     
        // Valid file extensions
        $extensions_arr = array("mp4","avi","3gp","mov","mpeg");
        // include db connect class
        require_once __DIR__ . '/db_config.php'; 
        // connecting to db
        $con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
        // 
        if(!$con){
            die("Connection failed: " . mysqli_connect_error());
        }
        //Mysql Inserting a Row
        // Check extension
        if( in_array($videoFileType,$extensions_arr) ){
              
            // Check file size
            if(($_FILES['filename']['size'] >= $maxsize) || ($_FILES["filename"]["size"] == 0)) {
    
                // failed Size constraint
                $response["success"] = 0;
                $response["message"] = "Oops! File too large. File must be less than 50MB.";
        
                // echoing JSON response
                echo json_encode($response);
            }else{
              // Upload onto server
              if(move_uploaded_file($_FILES['filename']['tmp_name'],$target_file)){
                // Insert record
                $query = "INSERT INTO video_file_upload(event_id, file_name, file_location) VALUES('".$event_id."','".$name."','".$target_file."')";
    
                $result = mysqli_query($con, $query) or die(mysqli_error($con));
                // Sample Insert Query
                //INSERT INTO video_file_upload(event_id, file_name, file_location) VALUES('1','Some Event','"FIlepath on server"')";
                
                // Update query to generate eventid as combination of id and m 
                //$db_update_query = "UPDATE `video_file_upload` SET `event_id`= Concat('m', cast(id AS CHAR));";
    
                //$result = mysqli_query($con, $db_update_query) or die(mysqli_error($con));
                
                // check if row inserted or not
                if ($result) {
                    // successfully inserted into database
                    $response["success"] = 1;
                    $response["message"] = "Video Upload successfull";
            
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
                  //Failed File Upload onto Server
                  $response["success"] = 0;
                  $response["message"] = "Oops! File Upload Failed !!!!!!";
                  // echoing JSON response
                  echo json_encode($response);
              }
            }
    
         }else{
            //Failed File extension Check
            $response["success"] = 0;
            $response["message"] = "Oops! Invalid file extension.";
    
            // echoing JSON response
            echo json_encode($response);
         }  
    }else{
        // required field is missing : filename
        $response["success"] = 0;
        $response["message"] = "Required field(s) is missing : filename ['filename']['name']";
    
        // echoing JSON response
        echo json_encode($response);

    }
}else{
    // required field is missing : eventId
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing : event_id";
 
    // echoing JSON response
    echo json_encode($response);
   }
?>