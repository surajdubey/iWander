<?php
error_reporting(E_ALL);
ini_set('display_errors', '1');

include('dbConnect.php');
include('sendNotification.php');

$username = $_POST['username'];
$longitude = $_POST['longitude'];
$latitude = $_POST['latitude'];

$sql="SELECT * FROM users WHERE username = '$username' AND userType = 'Caretaker'";
$query = $pdo->prepare($sql);
$query->execute();
$row = $query->fetch(PDO::FETCH_ASSOC);
$gcm_id = $row["gcm_id"];

$registration_ids = array($gcm_id);
$message = array("message" => "Patient has crossed the radius");
send_push_notification($registration_ids, $message);


?>
