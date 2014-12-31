<?php

define('INVALID_CREDENTIALS', 1);
define('DB_FAILED', 2);

require_once(__DIR__.'/inc/db.inc.php');
require_once(__DIR__.'/inc/auth.inc.php');
require_once(__DIR__.'/inc/httpjson.inc.php');

// LOGS
// file_put_contents(__DIR__."/register.txt","last submission:\n". json_encode($req, JSON_PRETTY_PRINT));

$req = hj_request();

if (!isset($req->username) or !isset($req->password)) hj_return(INVALID_CREDENTIALS);

$db = get_db();
if ($db->connect_error) hj_return(DB_FAILED);
$access_token = auth_username_password($db, $req->username, $req->password);
hj_resp('access_token', $access_token);
if ($access_token === NULL) hj_return(INVALID_CREDENTIALS);


hj_return(0);

?>