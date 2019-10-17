<?php
	require_once('.config.inc.php');
	if (!$_SESSION['valid']){
		header('Location: /login.php', true, 301);
		die();
	}
	else {
		$_SESSION['timeout'] = time();
	}

	// https://stackoverflow.com/a/13640164
	header("Cache-Control: no-store, no-cache, must-revalidate, max-age=0");
	header("Cache-Control: post-check=0, pre-check=0", false);
	header("Pragma: no-cache");

	if ($_SERVER['REQUEST_METHOD'] === 'POST') {
		if (isset($_POST['t']))
			if ($_POST['t'] === 'firebase_post' && isset($_POST['payload'])) {
				$ch = curl_init($BACKEND_SERVER_ADMIN_PAGE);
				$payload = json_decode($_POST['payload'], true);
				$payload['t'] = $_POST['t'];
				$payload = json_encode($payload);
				curl_setopt_array($ch, array(CURLOPT_POST => 1, CURLOPT_POSTFIELDS => $payload, CURLOPT_HEADER => 0, CURLOPT_RETURNTRANSFER => TRUE,
					CURLOPT_HTTPHEADER => array('Content-Type: application/json', 'Content-Length: ' . strlen($payload))));
				curl_exec($ch);
				if (curl_errno($ch)) {
					http_response_code(502);
					die('Couldn\'t send request: ' . curl_error($ch));
				}
				http_response_code(204);
				die();
			}
		else {
			http_response_code(400);
			die('Bad request');
		}
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
		else {
			http_response_code(400);
			die('Bad request');
		}
	}
?>