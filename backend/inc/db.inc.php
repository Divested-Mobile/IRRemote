<?php
require_once(__DIR__.'/db-config.php');

$_db;

function get_db() {
	global $_db;
	if (!isset($_db))
		$_db = new mysqli(DB_SERVER, DB_USER, DB_PASS, DB_NAME);
	return $_db;
}

?>