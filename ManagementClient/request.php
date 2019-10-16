<?php
	@session_start();
	require_once(".config.inc.php");
	if (!$_SESSION["valid"]){
		header('Location: /login.php', true, 301);
		die();
	}
	else {
		$_SESSION["timeout"] = time();
	}

	// https://stackoverflow.com/a/13640164
	header("Cache-Control: no-store, no-cache, must-revalidate, max-age=0");
	header("Cache-Control: post-check=0, pre-check=0", false);
	header("Pragma: no-cache");



	if ($_SERVER['REQUEST_METHOD'] === 'POST') {
		
	}
	elseif ($_SERVER['REQUEST_METHOD'] === 'GET') {
		if (isset($_GET['t'])) {
			if ($_GET['t'] == 'firebase_clients') {
				$conn = mysqli_connect($DB_HOST, $DB_USER, $DB_PASSWORD, $DB_NAME, $DB_PORT);
				$j = array(
					"result" => null,
					"data" => array()
				);
				/*if ($_GET['c'] == 'device') { // Deprecated
					$r = mysqli_query($conn, "SELECT `token` FROM `firebasetoken` WHERE `register_date` > DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 MONTH)");
					while ($result = mysqli_fetch_assoc($r))
						array_push($j["data"], $result["token"]);
				}*/
				$r = mysqli_query($conn, "SELECT `user_id` FROM `firebasetoken` WHERE `register_date` > DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 MONTH)");
				$users = array();
				while ($result = mysqli_fetch_assoc($r))
					//array_push($j["data"], $result["user_id"]);
					array_push($users, $result["user_id"]);
				array_unique($users);

				foreach ($users as $value) {
					$r = mysqli_query($conn, "SELECT `username` FROM `accounts` WHERE `id` = $value");
					$result = mysqli_fetch_assoc($r);
					$j["data"][$value] = $result["username"];
					//array_push($j["data"], $result["username"]);
				}
				//$j['result'] = !empty($j["data"])? "success": "error";
				echo json_encode($j, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
				mysqli_close($conn);
			}
			elseif ($_GET['t'] === '204') {
				http_response_code(204);
				die();
			}
		}
	}
?>