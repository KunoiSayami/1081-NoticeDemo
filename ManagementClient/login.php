<?php
	@session_start();
	require_once('.config.inc.php');
	// copied from https://www.tutorialspoint.com/php/php_login_example.htm
?>

<html>

   <head>
	  <title>Login to console</title>
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">

	  <style>
		body {
			padding-top: 40px;
			padding-bottom: 40px;
			background-color: #ADABAB;
		}

		.form-signin {
			max-width: 330px;
			padding: 15px;
			margin: 0 auto;
			color: #017572;
		}

		.form-signin .form-signin-heading,
		.form-signin .checkbox {
			margin-bottom: 10px;
		}

		.form-signin .checkbox {
			font-weight: normal;
		}

		.form-signin .form-control {
			position: relative;
			height: auto;
			-webkit-box-sizing: border-box;
			-moz-box-sizing: border-box;
			box-sizing: border-box;
			padding: 10px;
			font-size: 16px;
		}

		.form-signin .form-control:focus {
			z-index: 2;
		}

		.form-signin input[type="email"] {
			margin-bottom: -1px;
			border-bottom-right-radius: 0;
			border-bottom-left-radius: 0;
			border-color:#017572;
		}

		.form-signin input[type="password"] {
			margin-bottom: 10px;
			border-top-left-radius: 0;
			border-top-right-radius: 0;
			border-color:#017572;
		}

		h2{
			text-align: center;
			color: #017572;
		}
	  </style>

   </head>

   <body>

		<h2>Login required</h2>
		<div class = "container form-signin">
			<?php
				$msg = '';
				if ($_SERVER['REQUEST_METHOD'] === 'POST') {
					$conn = mysqli_connect($DB_HOST, $DB_USER, $DB_PASSWORD, $DB_NAME, $DB_PORT);
					$success_login = false;
					/*if (!$conn) {
						die("Connection failed: " . mysqli_connect_error());
					}*/
					if (isset($_POST['login']) && !empty($_POST['username'])
						&& !empty($_POST['password'])) {
						$hash_passwd = hash('sha512', $_POST['password']);
						$username = mysqli_escape_string($conn, $_POST['username']);
						$sqlObj = mysqli_query($conn,
							"SELECT `id`, `password` FROM `manage_account` WHERE `username` = '$username';");
						if (mysqli_num_rows($sqlObj) > 0) {
							// Only one row will get
							$row = mysqli_fetch_assoc($sqlObj);
							//echo $row['password'];
							if ($row['password'] === $hash_passwd) {
								$success_login = true;
								$_SESSION['timeout'] = time();
								$_SESSION['user_id'] = $row['id'];
							}
						}
					}
					if ($success_login) {
						$_SESSION['valid'] = true;
						header("Location: /", true, 301);
					}
					else {
						$msg = 'Wrong username or password';
					}
					mysqli_close($conn);
				}
			?>
		</div> <!-- /container -->

		<div class = "container">

				<?php
					if ($_SERVER['REQUEST_METHOD'] === 'GET') {
						if (isset($_GET['action']) && $_GET['action'] == 'logout'){
							unset($_SESSION['valid']);
						}
						elseif ($_SESSION['valid']) {
							header("Location: /", true, 301);
						}
					}
				?>

				<form class = "form-signin" role = "form"
				action = "<?php echo htmlspecialchars($_SERVER['PHP_SELF']);
				?>" method = "post">
					<h4 class = "form-signin-heading"><?php echo $msg; ?></h4>
					<input type = "text" class = "form-control"
					name = "username" placeholder = "Username"
					required autofocus></br>
					<input type = "password" class = "form-control"
					name = "password" placeholder = "password" required>
					<br>
					<button class = "btn btn-lg btn-primary btn-block" type = "submit"
					name = "login">Login</button>
				</form>


		</div>

   </body>
</html>