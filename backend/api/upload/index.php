<?php

define('INVALID_REQUEST', 1);
define('AUTH_FAILED', 2);
define('INVALID_REMOTE', 4);
define('INVALID_REMOTE', 8);

require_once(dirname(dirname(__DIR__)).'/inc/remotes.inc.php');
require_once(dirname(dirname(__DIR__)).'/inc/db.inc.php');
require_once(dirname(dirname(__DIR__)).'/inc/httpjson.inc.php');
require_once(dirname(dirname(__DIR__)).'/inc/auth.inc.php');


$req = hj_request();
if (!isset($req->userinfo)) hj_return(INVALID_REQUEST);
$acc = $req->userinfo;
if (!isset($acc->username) or !isset($acc->access_token)) hj_return(INVALID_REQUEST);

$db = open_db();
if (!auth_id_access_token($db, $acc->id, $acc->access_token)) hj_return(AUTH_FAILED);

$remote = $req->remote;
$det = $req->remote->details;

$st = $db->prepare('INSERT INTO remotes'
	.'(user_id, manufacturer, model, country, device_type)'
	.' VALUES(?,?,?,?,?);');
$st->bind_params("issss", $acc->id, $det->manufacturer, $det->model, $det->country, $det->device_type);


hj_resp('remote_details', $remote->details);
hj_resp('buttons_size', sizeof($remote->buttons));
hj_resp('userinfo_dbg', $acc);
hj_resp('deviceinfo_dbg', $req->deviceinfo);



unset($remote);
unset($req->remote);

hj_return(0);


/*
$details_obj = json_decode(file_get_contents('php://input'), true);
$remote_obj = $details_obj['remote'];
unset($details_obj['remote']);

clean_remote($remote_obj);

do {
	$id = uniqid();
	$remote_file = 'uploads/'.$id.".remote";
	$details_file = 'uploads/'.$id.".details";
} while (file_exists($remote_file) or file_exists($details_file));


file_put_contents($remote_file, json_encode($remote_obj, JSON_PRETTY_PRINT));
file_put_contents($details_file, json_encode($details_obj, JSON_PRETTY_PRINT));



*/
?>