<?php
require_once(__DIR__.'/db-config.php');

function open_db() {
	return new mysqli(DB_SERVER, DB_USER, DB_PASS, DB_NAME);
}

?>