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
define('DB_FAILED',    256);




// $email = $_GET['email'];
// $pass = $_GET['password'];


require_once(dirname(__DIR__)."/inc/validation.inc.php");
require_once(dirname(__DIR__).'/inc/db.inc.php');
require_once(dirname(__DIR__).'/inc/crypto.inc.php');
require_once(dirname(__DIR__).'/inc/httpjson.inc.php');

// LOGS
// file_put_contents(__DIR__."/register.txt","last submission:\n". json_encode($req, JSON_PRETTY_PRINT));

$req = hj_request();

if (!isset($req->username)) hj_return(USER_SHORT);
$user_len = strlen($req->username);
if ($user_len < MIN_USER_LEN) hj_return(USER_SHORT);
if ($user_len > MAX_USER_LEN) hj_return(USER_LONG);
if (!validate_user($req->username)) hj_return(USER_INVALID);
if (!isset($req->email) or !validate_email($req->email)) hj_return(EMAIL_INVALID);
if (!isset($req->password) or strlen($req->password) < MIN_PASSWD_LEN) hj_return(PASS_SHORT);

$db = open_db();
if ($db->connect_error) hj_return(DB_FAILED);

$err = 0;

$st = $db->prepare('SELECT username FROM users WHERE username=? LIMIT 1');
$st->bind_param("s", $req->username); $st->execute(); $st->bind_result($u); $st->fetch(); $st->close(); if (isset($u)) $err |= USER_TAKEN;
$st = $db->prepare('SELECT username FROM users WHERE email=? LIMIT 1');
$st->bind_param("s", $req->email); $st->execute(); $st->bind_result($e); $st->fetch(); $st->close(); if (isset($e)) $err |= EMAIL_TAKEN;
// Invalid user or email
if ($err != 0) hj_return($err);

// Also check the registrations table
$st = $db->prepare('SELECT username FROM registrations WHERE username=? LIMIT 1');
$st->bind_param("s", $req->username); $st->execute(); $st->bind_result($u); $st->fetch(); $st->close();
$err = 0; if (isset($u)) $err |= USER_TAKEN;
$st = $db->prepare('SELECT username FROM registrations WHERE email=? LIMIT 1');
$st->bind_param("s", $req->email); $st->execute(); $st->bind_result($e); $st->fetch(); $st->close(); if (isset($e)) $err |= EMAIL_TAKEN;
if ($err != 0) hj_return($err);


$st = $db->prepare('INSERT INTO registrations(username, email, password, token) VALUES (?,?,?,?)');
$pass = create_hash($req->password);
$token = md5(mcrypt_create_iv(32, MCRYPT_DEV_URANDOM)); // md5 for easy url sharing
$st->bind_param("ssss", $req->username, $req->email, $pass, $token);
$ok = $st->execute(); if ($ok === false) hj_return(UNKNOWN_ERR);
$st->close();

$db->close();
// file_put_contents(__DIR__."/dblog.txt", json_encode($u, JSON_PRETTY_PRINT));

// Send the email
// TODO get out of gmail spam folder. See SpamAssassins
$sub="Confirm your IR Remote account";
$url = "https://www.twinone.org/apps/irremote/launch/?a=verify&token=$token";
//&d=".urlencode(base64_encode($req->email.':'.$token));

$msg="Dear $req->username.\r\n\r\n"
."Thank you for registering an account for Twinone IR Remote!\r\n"
."To complete your registration you must first verify that the email you provided is a valid email address.\r\n"
."Please click the link below from your phone:\r\n\r\n"
.$url."\r\n\r\n"
."If you have any question please contact twinonedevs@gmail.com\r\n"
."Best Regards,\r\n"
."Twinone\r\n";

$return = "apps@twinone.org";
$from = "apps@twinone.org";
$headers='From: "Twinone IR Remote" <'.$from.'>';
// $msg = wordwrap($msg, 80);
mail($req->email, $sub, $msg, $headers, "-f $from -r $return");

hj_return(0);

?>