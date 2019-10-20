<?php
	require_once('.config.inc.php');
	if (!$_SESSION['valid']){
		echo "<html>
<head><title>403 Forbidden</title></head>
<body bgcolor=\"white\">
<center><h1>403 Forbidden</h1></center>
<hr><center>nginx</center>
</body>
</html>";
		#http_response_code(403);
		// https://www.rapidtables.com/web/dev/php-redirect.html
		header("Location: login.php", true, 301);
		die();
	}
	if (time() - $_SESSION["timeout"] > 600) {
		unset($_SESSION['valid']);
		header("Location: login.php", true, 301);
	}
	// Update time after refresh this page
	$_SESSION["timeout"] = time();
?>
<!doctype html>
<html>
<head>
	<title>Console</title>
	<link rel="stylesheet" type="text/css" href="panel.css">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
	<script src="panel.js?<?php echo time();?>"></script>
	<script src="keepalive.js"></script>
	<script>
		$(document).ready(function(){
			init_panel();
			__init__keepalive();
			setTimeout(() => {
				getAndSetTimeout();
			}, keep_session_interval);
		});
</script>
</head>
<body>
	<h1>Console</h1>
	<div id="panel">
		<div id="choose_panel">
			<label>
				<input type="radio" name="top_panel_select_group" id="radio_firebase_messaging_panel" checked="checked">
				Firebase Messaging Panel
			</label>
			<label>
				<input type="radio" name="top_panel_select_group" id="radio_past_notification_panel">
				Past Notification Panel
			</label>
		</div>

		<!-- Firebase messaging panel start -->
		<div id="firebase_messaging_panel">
			<form action="<?php echo htmlspecialchars($_SERVER['PHP_SELF']);?>" method="post">
				<div id="firebase_control_left">
					<h3>Title</h3>
					<input type="text" id='firebase_send_title' name="label" size="40">
					<h3>Message</h3>
					<textarea name="message" id='firebase_send_body' cols="45" rows="4" required></textarea>
				</div>
				<div id="firebase_control_right">
					<input name="firebase_control" type="radio" id="set_to_all_client" value="1" checked="checked">
					<label for="set_to_all_client">All</label>
					<input type="radio" id="part_of_user" name="firebase_control" value="2">
					<label for="part_of_user">Only Checked User</label>
					<div id="firebase_device_id">
					</div>
				</div>
			</form>

			<div id="firebase_control_button">
				<button type="submit" id="firebase_send">send</button>
				<button id="firebase_form_reset">reset</button>
				<button id="firebase_from_refresh">refresh</button>
			</div>
		</div>
		<!-- Firebase messaging panel end -->

		<!-- Manage notification panel start-->
		<div id="past_notification_panel" style="display: none;">
			<h3>Uncheck notification to hide notification in client</h3>
			<div id="list_notifications">
			</div>
			<div id="notifications_control_button">
				<button id="notifications_check_all">Check all</button>
				<button id="notifications_uncheck_all">Unheck all</button>
				<button id="notifications_save">Save</button>
			</div>
		</div>
		<!-- Manage notification panel end-->
		<div id="console_status"></div>
	</div>

	<hr>
	<label><input type="checkbox" id="ck_keep_session" checked="checked">Keep session available</label>
	<br />
	Click <a href="/login.php?action=logout">here</a> to logout Session.
</body>
</html>