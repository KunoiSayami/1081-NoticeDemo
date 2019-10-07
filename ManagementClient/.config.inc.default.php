<?php
	@session_start();
	ini_set("display_errors","Off");  //set this to "On" for debugging  ,especially when no reason blank shows up.
	ini_set("session.cookie_httponly", 1);
	header('X-Frame-Options:SAMEORIGIN');

	static $DB_HOST = '';
	static $DB_PORT = 3306;
	static $DB_USER = '';
	static $DB_PASSWORD = '';
	static $DB_NAME = '';

	static $BACKEND_SERVER_ADDR = ''; // Should in localhost will more secure
?>