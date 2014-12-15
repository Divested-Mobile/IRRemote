<?php
require_once(__DIR__.'/db.inc.php');

function auth_id_access_token($db, $id, $access_token) {
	$st = $db->prepare("SELECT id FROM users WHERE id=? AND access_token=? LIMIT 1");
	$st->bind_param("ss", $id, $access_token);
	$st->execute();
	$st->bind_result($count);
	$st->fetch();
	$st->close();
	return $count > 0;
}


?>