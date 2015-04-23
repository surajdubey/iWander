<?php
error_reporting(E_ALL);
ini_set('display_errors', '1');

include('dbConnect.php');

$username = $_POST['username'];
$password = $_POST['password'];
$userType = $_POST['userType'];
$age = $_POST['age'];
$dementiaLevel = $_POST['dementiaLevel'];
$phone = $_POST['phone'];
$gcm_id = $_POST["gcm_id"];

try{
$sql = "SELECT * FROM users WHERE username = '$username' AND userType = '$userType'";
$query = $pdo->prepare($sql);
$query->execute();
$row = $query->fetch(PDO::FETCH_ASSOC);
//	echo "reached here";
if($row == null){
	$sql="INSERT INTO users (username, password, userType, age, dementiaLevel, phone, gcm_id) VALUES ('$username', '$password', 	'$userType', '$age', '$dementiaLevel', '$phone', '$gcm_id')";
	$query = $pdo->prepare($sql);
	if($query->execute())
	{
		$response["message"] =  "Success";
	}
	
}

else{
	$response["message"] =  "User already exists.";
}

		
	echo json_encode($response);

}
catch(PDOException $e)
{
	$response["message"] =  "Error ocurred";
	
	echo json_encode($response);

}
?>
