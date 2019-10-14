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

	$conn = mysqli_connect($DB_HOST, $DB_USER, $DB_PASSWORD, $DB_NAME, $DB_PORT);


	if ($_SERVER['REQUEST_METHOD'] === 'POST') {

	}
	elseif ($_SERVER['REQUEST_METHOD'] === 'GET') {
		if (isset($_GET['t']) && $_GET['t'] == 'firebase_clients') {
			if (isset($_GET['c'])) {
				$j = array(
					"result" => null,
					"data" => array()
				);
				if ($_GET['c'] == 'device') {
					$r = mysqli_query($conn, "SELECT `token` FROM `firebasetoken` WHERE `register_date` > DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 MONTH)");
					while ($result = mysqli_fetch_assoc($r))
						array_push($j["data"], $result["token"]);
				}
				elseif ($_GET['c'] == 'user') {
					$r = mysqli_query($conn, "SELECT `user_id` FROM `firebasetoken` WHERE `register_date` > DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 MONTH)");
					while ($result = mysqli_fetch_assoc($r))
						array_push($j["data"], $result["user_id"]);
					array_unique($j["data"]);
				}

				$j['result'] = !empty($j["data"])? "success": "error";
				echo json_encode($j, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
			}
		}
	}


	mysqli_close($conn);
?>