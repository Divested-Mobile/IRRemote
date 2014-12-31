<?php

define('INVALID_REMOTE', 1);

define('INC_DIR', dirname(__DIR__));
require_once(INC_DIR.'/inc/remotes.inc.php');
require_once(INC_DIR.'/inc/db.inc.php');
require_once(INC_DIR.'/inc/httpjson.inc.php');
require_once(INC_DIR.'/inc/auth.inc.php');


// if !auth return
$req = hj_request();
hj_log('req', $req);
$acc = $req->userinfo;
auth_request($req);

$remote = $req->remote;

$det = $req->remote->details;

$user_id = get_id($acc->username);


$db = get_db();
$st = $db->prepare('INSERT INTO remotes'
	.'(user_id, manufacturer, model, device_type) VALUES(?,?,?,?)');
$type_string = get_type_string($req->remote);
$st->bind_param("isss", $user_id, $det->manufacturer, $det->model, $type_string);
$st->execute();
$remote_id = $st->insert_id;
$st->close();

$st = $db->prepare('INSERT INTO buttons'
	.'(remote_id, function, frequency, pattern) VALUES(?,?,?,?)');
$b_func = $b_freq = $b_patt = '';
$st->bind_param("isis", $remote_id, $b_func, $b_freq, $b_patt);
$i = 0;
foreach ($remote->buttons as $ignored => $b_json) {
	hj_log("b".$i, $b_json);
	$i++;
	$b = new Button($b_json);
	$b_func = $b->func_string();
	$b_freq = $b->signal->frequency;
	$b_patt = $b->signal->serialize_pattern();
	$st->execute();
}
$st->close();
$db->close();

hj_resp("remote_id", $remote_id);

hj_return(0);

?>