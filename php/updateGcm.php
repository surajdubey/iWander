<?php
error_reporting(E_ALL);
ini_set('display_errors', '1');

include('dbConnect.php');

$username = $_POST['username'];
$userType = $_POST['userType'];
$gcm_id = $_POST["gcm_id"];

try{
$sql = "SELECT * FROM users WHERE username = '$username' AND userType = '$userType'";
$query = $pdo->prepare($sql);
$query->execute();
$row = $query->fetch(PDO::FETCH_ASSOC);
$user_id = $row["user_id"];//	echo "reached here";

$sql = "UPDATE users SET gcm_id = '$gcm_id' WHERE user_id = '$user_id'";
$query = $pdo->prepare($sql);
if($query->execute())
{
	$response["message"] =  "Success";
}
else
{
	$response["message"] =  "Failed";

}
echo json_encode($response);

}
catch(PDOException $e)
{
	$response["message"] =  "Error ocurred";
	
	echo json_encode($response);

}
?>
