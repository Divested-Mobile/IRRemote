<?php

function hj_request() {
	return json_decode(file_get_contents('php://input'));
}

$_hj_response = array();
function hj_return($status) {
	global $_hj_response;
	$_hj_response['status'] = $status;
	// Log
	file_put_contents("/var/www/apps/irremote/log.txt", json_encode($_hj_response, JSON_PRETTY_PRINT));
	die(json_encode($_hj_response));
}

function hj_resp($key, $val) {
	global $_hj_response;
	$_hj_response[$key] = $val;
}

?>