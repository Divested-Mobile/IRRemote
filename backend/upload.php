<?php

require_once('./remotes.inc.php');


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

?>