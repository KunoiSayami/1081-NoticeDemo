"use strict";

var is_enabled_keep_session = true; // default is true
const keep_session_interval = 120000;
function verify_keep_session() {
	if (document.getElementById('ck_keep_session').checked) {
		if (is_enabled_keep_session)
			return;
		else
			getAndSetTimeout();
	}
	else {
		if (is_enabled_keep_session)
			setTimeout(() => {
				verify_keep_session();
			}, keep_session_interval);
		// do nothing else
	}
}

function __init__keepalive() {
	document.getElementById('ck_keep_session').addEventListener('click', verify_keep_session);
}

function getAndSetTimeout() {
	$.get('request.php?t=204').done(
		function () {
			if (document.getElementById('ck_keep_session').checked){
				is_enabled_keep_session = true;
				setTimeout(() => {
					getAndSetTimeout();
				}, keep_session_interval);
			}
			else {
				is_enabled_keep_session = false;
			}
		}
	);
}