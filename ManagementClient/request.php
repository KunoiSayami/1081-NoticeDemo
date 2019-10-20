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
		if (isset($_POST['t'])){
			if (!(($_POST['t'] === 'firebase_post' || $_POST['t'] === 'notification_manage')
				&& isset($_POST['payload']))) {
					http_response_code(400);
					die('Bad request');
				}
			$payload = json_decode($_POST['payload'], true);
			if ($_POST['t'] === 'firebase_post')
				if (strlen($payload['title']) > 25 || strlen($payload['body']) > 300) {
					http_response_code(413);
					die('Payload too large');
				}
			$payload['t'] = $_POST['t'];
			$payload = json_encode($payload);
			$ch = curl_init($BACKEND_SERVER_ADMIN_PAGE);
			curl_setopt_array($ch, array(CURLOPT_POST => 1, CURLOPT_POSTFIELDS => $payload, CURLOPT_HEADER => 0, CURLOPT_RETURNTRANSFER => TRUE,
				CURLOPT_HTTPHEADER => array('Content-Type: application/json', 'Content-Length: ' . strlen($payload)),
				CURLOPT_TCP_FASTOPEN => TRUE));
			$response = curl_exec($ch);
			if (curl_errno($ch)) {
				http_response_code(502);
				die('Couldn\'t send request: ' . curl_error($ch));
			}
			$response = json_decode($response, true);
			if ($response['status'] == 200)
				http_response_code(204);
			else {
				http_response_code(400);
				die('Server return error: ' . $response['error']['info']);
			}
			die();
		}
	}
	elseif ($_SERVER['REQUEST_METHOD'] === 'GET') {
		if (isset($_GET['t'])) {
			if ($_GET['t'] === '204') {
				http_response_code(204);
				die();
			}
			$j = array(
				"result" => null, // reversed
				"data" => array()
			);
			$conn = mysqli_connect($DB_HOST, $DB_USER, $DB_PASSWORD, $DB_NAME, $DB_PORT);
			if ($_GET['t'] === 'firebase_clients') {
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
			}
			elseif ($_GET['t'] === 'past_notifications') {
				$r = mysqli_query($conn, "SELECT `id`, `title`, `body`, `timestamp`, `available` FROM `notifications` ORDER BY `id` DESC");
				while ($result = mysqli_fetch_assoc($r))
					array_push($j["data"], $result);
			}
			echo json_encode($j, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
			mysqli_close($conn);
		}
		else {
			http_response_code(400);
			die('Bad request');
		}
	}
?>