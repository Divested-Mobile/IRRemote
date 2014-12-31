<?php
define('AUTH_INFO_MISSING', 1);
define('AUTH_FAILED', 2);
define('MSG_AUTH_ERR', "Authentication error");


require_once(__DIR__.'/db.inc.php');
require_once(__DIR__.'/httpjson.inc.php');



// Checks if the request has valid authentication credentials and dies if not
function auth_request($req) {
	if (!isset($req->userinfo)) hj_return(AUTH_INFO_MISSING, MSG_AUTH_ERR);
	$acc = $req->userinfo;
	if (!isset($acc->username) or !isset($acc->access_token)) hj_return(AUTH_INFO_MISSING, MSG_AUTH_ERR);

	$db = get_db();
	if (!auth_username_access_token($db, $acc->username, $acc->access_token)) hj_return(AUTH_INFO_MISSING, MSG_AUTH_ERR);
}

function get_id($username) {
	$db = get_db();
	$st = $db->prepare("SELECT id FROM users WHERE username=? LIMIT 1");
	$st->bind_param("s", $username);
	$st->execute();
	$st->store_result();
	$st->bind_result($id);
	$st->fetch();
	$nr = $st->num_rows;
	$st->close();
	if ($nr === 0) return 0;
	return $id;
}

function auth_username_access_token($db, $id, $access_token) {
	$st = $db->prepare("SELECT id FROM users WHERE username=? AND access_token=? LIMIT 1");
	$st->bind_param("ss", $id, $access_token);
	$st->execute();
	$st->bind_result($count);
	$st->fetch();
	$st->close();
	return $count > 0;
}

function auth_username_password($db, $username, $password) {
	$st = $db->prepare("SELECT access_token, password FROM users WHERE username=? LIMIT 1");
	$st->bind_param("s", $username);
	$st->execute();
	$st->bind_result($token, $hash);
	$st->fetch();
	$st->close();

		hj_log("auth_token_db", $token);

	require_once(__DIR__.'/crypto.inc.php');
	if (validate_password($password, $hash)) {
		hj_log("auth_token_db", $token);
		return $token;
	}
	return NULL;
}


?>