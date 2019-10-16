"use strict";

function getAndSetTimeout() {
	$.get('request.php?t=204').always(setTimeout(() => {
		getAndSetTimeout();
	}, 120000));
}