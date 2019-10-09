<?php
    @session_start();
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
	$conn = mysqli_connect($DB_HOST, $DB_USER, $DB_PASSWORD, $DB_NAME, $DB_PORT);
?>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<script src="panel.js"></script>
<script>
	$(document).ready(function(){
		init_panel();
	});
</script>
<html>
	<h1>Console</h1>
	<div id="panel">
		<form action="<?php echo htmlspecialchars($_SERVER['PHP_SELF']);?>" method="post">
			<div id="firebase_control_left">
				<input type="text" name="label" size="40"></p>
				<textarea name="message" cols="45" rows="4"></textarea></p>
			</div>
			<div id="firebase_control_right">
				<input type="radio" id="set_to_all_client" name="reciever" value="1">
				<label for="set_to_all_client">All</label>
				<input type="radio" id="part_of_client" name="reciever" value="2">
				<label for="part_of_client">Checked</label>
				<div id="firebase_device_id">
				</div>
			</div>

			<div id="firebase_control_button">
				<button type="submit" id="firebase_send">send</button>
				<button id="firebase_form_reset">reset</button>
				<button id="firebase_from_refresh">refresh</button>
			</div>
		</form>
	</div>
	Click <a href = "/login.php?action=logout">here</a> to logout Session.
</html>

<?php
	mysqli_close($conn);
?>