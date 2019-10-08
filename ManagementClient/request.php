<?php
	@session_start();
	require_once(".config.inc.php");
	if (!$_SESSION["valid"]){
		header('Location: /login.php', true, 301);
		die();
	}
	if ($_SERVER['REQUEST_METHOD'] === 'POST') {

	}
	elseif ($_SERVER['REQUEST_METHOD'] === 'GET') {
		
	}
?>