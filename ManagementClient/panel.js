"use strict";
var submit_button, radio_set_to_all_client_button, radio_set_to_part_of_client_button, radio_set_to_part_of_user_button;
var refresh_button;
var div_firebase_device_id;
var last_request_time = 0;
const expire_time = 120 * 1000;
var i, checkbox_disabled, checkbox_font_style_1;
var last_request_type;

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
		refresh_firebase_clients(true);
	});
	radio_set_to_part_of_client_button.addEventListener('click', function() {
		refresh_firebase_clients(true);
	});
	radio_set_to_part_of_user_button.addEventListener('click', function() {
		refresh_firebase_clients();
	});
	refresh_button.addEventListener('click', function(_event) {
		refresh_firebase_clients(true);
	});
	refresh_firebase_clients(true);
}

function process_firebase_token_data(raw_json) {
	i = 0;

	// clear first
	div_firebase_device_id.innerHTML = '';
	if (radio_set_to_all_client_button.checked){
		checkbox_disabled = 'disabled="disabled"';
		checkbox_font_style_1 = 'style="color: gray;"';
	}
	else {
		checkbox_disabled = '';
		checkbox_font_style_1 = ''
	}
	if (raw_json.data.length < 5)
		div_firebase_device_id.classList.remove("scrollable_area");
	else
		div_firebase_device_id.classList.add("scrollable_area");

	raw_json.data.forEach(x => {
		div_firebase_device_id.innerHTML +=
			'<label><input type="checkbox" name="device_id_group" value="' + i +
			'" id="deviceIdGroup'+ i + '" checked="checked" '+ checkbox_disabled +'><font ' +
			checkbox_font_style_1 + '>' + x.substr(0, 20) +'</font></label><br>';
		i++;
	});
	//div_firebase_device_id.innerHTML += '';
}

function get_last_request_type() {
	if (radio_set_to_all_client_button.checked || radio_set_to_part_of_client_button.checked) {
		return 1;
	}
	else if (radio_set_to_part_of_user_button.checked) {
		return 2;
	}
	throw new Error("Request type undefined");
}

function refresh_firebase_clients(force = false) {
	if ((!force && (new Date().getTime() - last_request_time) <= expire_time) || last_request_type == get_last_request_type())
		return;
	var c_type = 'device';
	if (radio_set_to_part_of_user_button.checked)
		c_type = 'user';
	$.getJSON('/request.php?t=firebase_clients&c=' + c_type + "&" + new Date().getTime(), process_firebase_token_data)
		.fail(function(){
			console.error('Error in fetch firebase clients');
		}
	);
	last_request_type = get_last_request_type();
}