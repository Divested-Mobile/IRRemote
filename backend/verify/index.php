<?php

define('OK',      0);
define('INV_REG_TOKEN', 1);
define('DB_FAILED', 2);
define('VERIF_FAILED', 3);
define('ALREADY_EXISTS', 4);

require_once(dirname(__DIR__).'/inc/db.inc.php');
require_once(dirname(__DIR__).'/inc/httpjson.inc.php');

$req = hj_request();

$username = $req->username;
$tok = $req->token;
if (!isset($tok)) hj_return(INV_REG_TOKEN);


$db = open_db();
if ($db->connect_error) hj_return(DB_FAILED);

$st = $db->prepare('SELECT username FROM users WHERE username=? LIMIT 1');
$st->bind_param("s", $username); $st->execute();
$st->store_result();
$ar = $st->num_rows;
$st->close();
if ($ar === 1) hj_return(ALREADY_EXISTS);

$st = $db->prepare('INSERT INTO users (username, email, password) '
	.'SELECT username, email, password FROM registrations WHERE username=? AND token=? LIMIT 1');
$st->bind_param("ss", $username, $tok);
$st->execute();
$ar = $st->affected_rows;
$id = $st->insert_id;
$st->close();
if ($ar !== 1) hj_return(VERIF_FAILED);

$st = $db->prepare('DELETE FROM registrations WHERE username=?');
$st->bind_param('s', $username); $st->execute(); $st->close();

$st = $db->prepare('UPDATE users SET access_token=? WHERE username=?');
$access_tok = md5(mcrypt_create_iv(32, MCRYPT_DEV_URANDOM));
$st->bind_param('ss', $access_tok, $username);
$st->execute();
$st->close();
$db->close();

hj_resp('id', $id);
hj_resp('access_token', $access_tok);
hj_return(OK);
?>