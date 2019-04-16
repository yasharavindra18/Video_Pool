
<?php


$host = "handson-mysql";
$db_name = "PRF";
$username = "kumar";
$password = "kumar";
// Create connection
$conn = new mysqli($host, $username, $password, $db_name);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

echo "Hello";

?>