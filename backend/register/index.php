<?php

define('MIN_PASSWD_LEN', 8);
define('MIN_USER_LEN', 5);
define('MAX_USER_LEN', 32);
/*
Communication is done in json:
Input:
{ 
	user: string,
	email: string,
	password: string
}

Output:
{
	status: int,
}
Status:
*/
define('OK',            0);
define('USER_SHORT',    1);
define('USER_TAKEN',    2);
define('PASS_SHORT',    4);
define('EMAIL_INVALID', 8);
define('EMAIL_TAKEN',   16);
define('USER_LONG',     32);
define('USER_INVALID',  64);
define('UNKNOWN_ERR',  128);

// $email = $_GET['email'];
// $pass = $_GET['password'];


require_once(dirname(__DIR__)."/inc/validation.inc.php");
require_once(dirname(__DIR__).'/inc/db.inc.php');
require_once(dirname(__DIR__).'/inc/crypto.inc.php');

// LOGS
$req = json_decode(file_get_contents('php://input'));
file_put_contents(__DIR__."/register.txt","last submission:\n". json_encode($req, JSON_PRETTY_PRINT));


function rand_salt() {
	return substr(hash('sha256', mt_rand()), 0, 16);
}
function return_status($status, $msg=null) {
	die("{status:$status}");
}

if (!isset($req->username)) return_status(USER_SHORT);
$user_len = strlen($req->username);
if ($user_len < MIN_USER_LEN) return_status(USER_SHORT);
if ($user_len > MAX_USER_LEN) return_status(USER_LONG);
if (!validate_user($req->username)) return_status(USER_INVALID);
if (!isset($req->email) or !validate_email($req->email)) return_status(EMAIL_INVALID);
if (!isset($req->password) or strlen($req->password) < MIN_PASSWD_LEN) return_status(PASS_SHORT);

$db = open_db();
$err = 0;

$st = $db->prepare('SELECT username FROM users WHERE username=? LIMIT 1');
$st->bind_param("s", $req->username); $st->execute(); $st->bind_result($u); $st->fetch(); $st->close(); if (isset($u)) $err |= USER_TAKEN;
$st = $db->prepare('SELECT username FROM users WHERE email=? LIMIT 1');
$st->bind_param("s", $req->email); $st->execute(); $st->bind_result($e); $st->fetch(); $st->close(); if (isset($e)) $err |= EMAIL_TAKEN;
// Invalid user or email
if ($err != 0) return_status($err);

// Also check the registrations table
$st = $db->prepare('SELECT username FROM registrations WHERE username=? LIMIT 1');
$st->bind_param("s", $req->username); $st->execute(); $st->bind_result($u); $st->fetch(); $st->close();
$err = 0; if (isset($u)) $err |= USER_TAKEN;
$st = $db->prepare('SELECT username FROM registrations WHERE email=? LIMIT 1');
$st->bind_param("s", $req->email); $st->execute(); $st->bind_result($e); $st->fetch(); $st->close(); if (isset($e)) $err |= EMAIL_TAKEN;
if ($err != 0) return_status($err);


$st = $db->prepare('INSERT INTO registrations(username, email, password, token) VALUES (?,?,?,?)');
$token = rand_salt();
$pass = create_hash($req->password);
$token = md5(mcrypt_create_iv(32, MCRYPT_DEV_URANDOM)); // md5 for easy url sharing
$st->bind_param("ssss", $req->username, $req->email, $pass, $token);
$ok = $st->execute(); if ($ok === false) return_status(UNKNOWN_ERR);
$st->close();

// file_put_contents(__DIR__."/dblog.txt", json_encode($u, JSON_PRETTY_PRINT));


// Send the email

// TODO get out of gmail spam folder. See SpamAssassins
$sub="Confirm IR Remote registration";
$msg="Dear IR Remote user, you have successfully registered. please confirm your email by clicking on the following link:\r\n"
."https://www.twinone.org/apps/irremote/confirm/?email=$req->email&tok=$token";

$headers='From: irremote-noreply@twinone.org (IR Remote)';
$msg = wordwrap($msg, 70);
mail($req->email, $sub, $msg, $headers);

return_status(0);
?>