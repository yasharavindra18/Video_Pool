
<?php

 //echo "Success! This message is from PHP";

$host = "handson-mysql";
$db_name = "HCI";
$username = "kumar";
$password = "kumar";
// Create connection
$conn = new mysqli($host, $username, $password, $db_name);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

if(isset($_POST['latitude']) && isset($_POST['longitude'])){
	$lat = $_POST['latitude'];
	$long = $_POST['longitude'];
	$sql = "Select * from EventsData";
	$result = $conn->query($sql);
	if($result->num_rows>0){
		while($row = $result->fetch_assoc()){

			$R = 6371; // Radius of the earth in km
  			$dLat = (($lat-$row['Event_lat'])*pi())/180;  // deg2rad below
  			$dLon = (($long-$row['Event_long'])*pi())/180;
			//$data['data'][] = $row;
			$a = sin($dLat/2) * sin($dLat/2) + cos(deg2rad($lat)) * cos(deg2rad($row['Event_lat'])) * sin($dLon/2) * sin($dLon/2);
			$c = 2 * atan2(sqrt($a), sqrt(1-$a));
  			$d = $R * $c; // Distance in km
  			if($d<0.5){
  			$data['data'][] = $row;
  			}

		}
	}


//echo "Hello";
//$result['data'] = "Hello";
echo json_encode($data);
}

/*function deg2rad($deg) {
  return $deg * (pi()/180)
}*/


?>
