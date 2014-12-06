<?php

$DEBUG = false;
require_once(dirname(__DIR__).'/inc/remotes.inc.php');

if (isset($_GET['id'])) {
	$id = $_GET['id'];
} else {
	die("Error 0");
}

$remote_file = 'uploads/'.$id.".remote";
$details_file = 'uploads/'.$id.".details";

if (file_exists($remote_file) and file_exists($details_file)) {
	$remote_obj = json_decode(file_get_contents($remote_file), true);
	$details_obj = json_decode(file_get_contents($details_file), true);
	$details_obj['remote'] = $remote_obj;
} else {
	die("Error 1");
}


if ($DEBUG) { echo "<pre>"; }
echo json_encode($details_obj, JSON_PRETTY_PRINT);
if ($DEBUG) { echo "</pre>"; }



?>