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
?>