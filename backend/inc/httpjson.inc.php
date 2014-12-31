<?php

function hj_request() {
	return json_decode(file_get_contents('php://input'));
}

$_hj_log = array();
function hj_log($key, $val = NULL) {
	if ($val !== NULL) {
		global $_hj_log;
		$_hj_log[$key] = $val;
	}
	$f="/var/www/apps/irremote/log.html";
	file_put_contents($f, date("M d Y H:i:s")."\n<pre>".json_encode($key, JSON_PRETTY_PRINT)."</pre>");
}

$_hj_response = array();
function hj_return($status, $message = null) {
	global $_hj_response;
	$_hj_response['status'] = $status;
	if (!empty($message)) $_hj_response['message'] = $message;
	// Log
	global $_hj_log;
	hj_log('resp', $_hj_response);
	hj_log($_hj_log);
	die(json_encode($_hj_response));
}

function hj_resp($key, $val) {
	global $_hj_response;
	$_hj_response[$key] = $val;
}

?>