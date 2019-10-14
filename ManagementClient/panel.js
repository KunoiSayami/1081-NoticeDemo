"use strict";
var submit_button, radio_set_to_all_client_button, radio_set_to_part_of_client_button, radio_set_to_part_of_user_button;
var refresh_button;
var div_firebase_device_id;
var last_request_time = 0;
const expire_time = 120 * 1000;
function findElementById() {
	submit_button = document.getElementById('firebase_send');
	radio_set_to_all_client_button = document.getElementById('set_to_all_client');
	radio_set_to_part_of_client_button = document.getElementById('part_of_client');
	radio_set_to_part_of_user_button = document.getElementById('part_of_user');
	refresh_button = document.getElementById('firebase_from_refresh');
	div_firebase_device_id = document.getElementById('firebase_device_id');
}

function init_panel() {
	findElementById();
	submit_button.addEventListener('click', async _ => {
		try {
			const response = await fetch('/request.php', {
				method: 'post',
				body: {

				}
			});
		} catch (err) {
			console.error(`Error: ${err}`);
		}
	});
	radio_set_to_all_client_button.addEventListener('click', function() {

	});
	refresh_button.addEventListener('click', function(event) {
		refresh_firebase_clients(true);
		return false;
	});
}

function process_firebase_token_data(raw_json) {
	div_firebase_device_id.innerHtml = '<p>';
	var i = 0;
	raw_json.data.forEach(x => {
		div_firebase_device_id.innerHtml +=
		'<label><input type="checkbox" name="device_id_group" value="' + i + '" id="deviceIdGroup'+ i + '" checked="checked">'+ x.substr(0, 20) +'</label><br>';
		i++;
	});
	div_firebase_device_id.innerHtml += '</p>';
}

function refresh_firebase_clients(force = false) {
	if (!force && (new Date().getTime() - last_request_time) <= expire_time)
		return;
	var c_type = 'device';
	if (radio_set_to_part_of_user_button.checked)
		c_type = 'user';
	$.getJSON('/request.php?t=firebase_clients&c=' + c_type + "&" + new Date().getTime(), process_firebase_token_data)
		.fail(function(){
			console.error('Error in fetch firebase clients');
		}
	);
}