"use strict";

var firebase_send_button, radio_set_to_all_client_button, radio_set_to_part_of_user_button,
	txt_firebase_notice_title, txt_firebase_notice_body,
	radio_select_firebase_message, radio_select_manage_notification,
	txt_search_user_field;

var div_notifications, button_notification_submit, button_notification_check,
	button_notification_uncheck, button_notifaction_delete;

var refresh_button, reset_button, div_firebase_device_id;

var div_console_status;

var last_request_time = 0;
const expire_time = 120 * 1000, label_offset = 2;
var i, checkbox_disabled, checkbox_font_style_1;
var last_request_type;

function findElementById() {
	firebase_send_button = document.getElementById('firebase_send');
	radio_set_to_all_client_button = document.getElementById('set_to_all_client');
	radio_set_to_part_of_user_button = document.getElementById('part_of_user');
	refresh_button = document.getElementById('firebase_from_refresh');
	div_firebase_device_id = document.getElementById('firebase_device_id');
	txt_firebase_notice_title = document.getElementById('firebase_send_title');
	txt_firebase_notice_body = document.getElementById('firebase_send_body');
	reset_button = document.getElementById('firebase_form_reset');
	radio_select_firebase_message = document.getElementById('radio_firebase_messaging_panel');
	radio_select_manage_notification = document.getElementById('radio_past_notification_panel');
	div_notifications = document.getElementById('list_notifications');
	button_notification_submit = document.getElementById('notifications_save');
	button_notification_check = document.getElementById('notifications_check_all');
	button_notification_uncheck = document.getElementById('notifications_uncheck_all');
	div_console_status = document.getElementById('console_status');
	txt_search_user_field = document.getElementById('search_user');
}

function init_panel() {
	findElementById();
	radio_set_to_all_client_button.addEventListener('click', function() {
		document.getElementById('search_user_label').style.display = "none";
		txt_search_user_field.value = '';
		refresh_firebase_clients(true);
	});
	radio_set_to_part_of_user_button.addEventListener('click', function() {
		document.getElementById('search_user_label').style.display = "";
		refresh_firebase_clients(true);
	});
	firebase_send_button.addEventListener('click', function() {
		// length limit here
		if (txt_firebase_notice_title.value.length > 25) {
			return alert('Title length should shorter than 25');
		}
		if (txt_firebase_notice_body.value.length > 300) {
			return alert('Body length should shorter than 300');
		}

		var select_users = [];
		if (!radio_set_to_all_client_button.checked)
			document.getElementsByName('device_id_group').forEach(element => {
				if (element.checked)
					select_users.push(element.value);
			});
		do_POST('firebase_post', {
			title: txt_firebase_notice_title.value,
			body: txt_firebase_notice_body.value,
			select_user: (radio_set_to_all_client_button.checked ? 'all' : select_users)
		}).done(() => {show_console_status('Posted!');});
	});

	txt_search_user_field.addEventListener('input', function(){
		document.getElementsByName('device_label_groups').forEach(element => {
			if (element.innerText.indexOf(this.value) == -1){
				element.children[0].checked = false;
				element.style.display = "none";
			}
			else {
				element.children[0].checked = true;
				element.style.display = "";
			}
		});
	});


	reset_button.addEventListener('click', function() {
		txt_firebase_notice_title.value = '';
		txt_firebase_notice_body.value = '';
	});
	refresh_button.addEventListener('click', function(_event) {
		refresh_firebase_clients(true);
	});
	radio_select_manage_notification.addEventListener('click', function() {
		document.getElementById('firebase_messaging_panel').style.display = "none";
		document.getElementById('past_notification_panel').style.display = "block";
		refresh_past_notifications();
	});
	radio_select_firebase_message.addEventListener('click', function(){
		document.getElementById('firebase_messaging_panel').style.display = "block";
		document.getElementById('past_notification_panel').style.display = "none";
		refresh_firebase_clients();
	});

	button_notification_submit.addEventListener('click', function() {
		var unchecked_group = [], checked_group = [];
		document.getElementsByName('notifications_group').forEach(element => {
			if (element.checked)
				checked_group.push(element.value);
			else
				unchecked_group.push(element.value);
		});
		do_POST('notification_manage', {
			checked: checked_group,
			unchecked: unchecked_group
		}).done(() => {show_console_status('Saved!');});
	});

	button_notification_check.addEventListener('click', function (){
		document.getElementsByName('notifications_group').forEach(element => {
			element.checked = true;
		});
	});

	button_notification_uncheck.addEventListener('click', function (){
		document.getElementsByName('notifications_group').forEach(element => {
			element.checked = false;
		});
	});

	refresh_firebase_clients(true);
}

function show_console_status(str , timeout = 5) {
	div_console_status.innerHTML = '<h3>'+ str +'</h3>';
	setTimeout(() => {
		div_console_status.innerHTML = '';
	}, timeout * 1000);
}

function do_POST(t, payload, callback = () => {}) {
	return $.post('/request.php', {
		t: t,
		payload: JSON.stringify(payload)
	}, callback);
}

function process_firebase_token_data(raw_json) {
	i = 0;

	// clear first
	div_firebase_device_id.innerHTML = '';
	if (radio_set_to_all_client_button.checked){
		checkbox_disabled = 'disabled';
		checkbox_font_style_1 = 'style="color: gray;"';
	}
	else {
		checkbox_disabled = '';
		checkbox_font_style_1 = ''
	}
	// https://stackoverflow.com/a/14626707
	if (Object.keys(raw_json.data).length < 5)
		div_firebase_device_id.classList.remove("scrollable_area");
	else
		div_firebase_device_id.classList.add("scrollable_area");

	// https://stackoverflow.com/a/18804596
	Object.keys(raw_json.data).forEach(function(key, _value){
		div_firebase_device_id.innerHTML +=
			'<label name="device_label_groups"><input type="checkbox" name="device_id_group" value="' + key +
			'" id="deviceIdGroup'+ i + '" checked="checked" '+ checkbox_disabled +'><font ' +
			checkbox_font_style_1 + '>' + raw_json.data[key] +'</font><br /></label>';
		i++;
	});
	//div_firebase_device_id.innerHTML += '';
}

function get_last_request_type() {
	if (radio_set_to_all_client_button.checked) {
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
	$.getJSON('/request.php?t=firebase_clients' + "&" + new Date().getTime(), process_firebase_token_data)
		.fail(function(){
			console.error('Error occurd while fetch firebase clients');
		}
	);
	last_request_type = get_last_request_type();
}

function process_past_notifications(jsonObject) {
	div_notifications.innerHTML = '';
	if (jsonObject.data.length > 10)
		div_notifications.classList.add("scrollable_area");
	else
		div_notifications.classList.remove("scrollable_area");
	jsonObject.data.forEach(x => {
		div_notifications.innerHTML += '<label>' +
		'<input type="checkbox" name="notifications_group" value="' + x.id + '" id="notificationsIdGroup_' +
		x.id + '" ' + (x.available == 'Y'? 'checked' : '') + ' >' + x.title + '(' + getStringWithLimit(x.body) + ')</label><br>';
	});
}

function getStringWithLimit(str, limit = 20) {
	if (str.length < limit)
		return str;
	else
		return str.substr(0, limit) + '...';
}

function refresh_past_notifications() {
	$.getJSON('/request.php?t=past_notifications&' + new Date().getTime(), process_past_notifications)
		.fail(function() {
			console.error('Error occurd while fetch past notifications');
		});
}