<?php
require_once(__DIR__.'/db.inc.php');

// Set to true to activate web create/drop
$allow_db_creation = false;

$db;

function query_check($q) {
	echo "Executing query: ".$q.'<br>';
	global $db;
	if ($db->query($q) !== TRUE) {
		die("Error:<br>".$db->error);
	}
}

function drop_database() {
	global $db;
	$db = get_db();
	if ($db->connect_error) die("Connection failed");
	query_check('DROP DATABASE irremote;');
}

function create_database() {
	global $db;
	$db = new mysqli(DB_SERVER, DB_USER, DB_PASS);
	if ($db->connect_error) {
		die("Connect failed");
	}

	query_check('CREATE DATABASE IF NOT EXISTS '.DB_NAME);
	query_check('USE '.DB_NAME);

	query_check('CREATE TABLE IF NOT EXISTS users ('
		.'id int NOT NULL AUTO_INCREMENT,'
		.'email VARCHAR(255) NOT NULL,'
		.'username VARCHAR(32) NOT NULL,'
		.'password VARCHAR(128) NOT NULL,'
		.'reg_date TIMESTAMP NOT NULL DEFAULT NOW(),'
		.'access_token VARCHAR(32),'
		.'PRIMARY KEY (id)'
		.');');
	query_check('ALTER TABLE users AUTO_INCREMENT=10000;');

	query_check('CREATE TABLE IF NOT EXISTS registrations ('
		.'username VARCHAR(32) NOT NULL,'
		.'email VARCHAR(255) NOT NULL,'
		.'password VARCHAR(128) NOT NULL,'
		.'reg_date TIMESTAMP NOT NULL DEFAULT NOW(),'
		.'token CHAR(32) NOT NULL' // sent in email
		.');');

	query_check('CREATE TABLE IF NOT EXISTS remotes ('
		.'id int NOT NULL AUTO_INCREMENT,'
		.'parent_id int,' // Parent remote id if remote was forked
		.'user_id int,' // User that uploaded the remote
		.'manufacturer VARCHAR(64),'
		.'model VARCHAR(64),'
		.'device_type VARCHAR(64),' // (see Remote.java)
		.'PRIMARY KEY (id),'
		.'FOREIGN KEY (parent_id) REFERENCES remotes(id),'
		.'FOREIGN KEY (user_id) REFERENCES users(id)'
		.');');
	query_check('ALTER TABLE remotes AUTO_INCREMENT=10000;');

	query_check('CREATE TABLE IF NOT EXISTS buttons ('
		.'remote_id int,'
		.'function VARCHAR(64),'
		.'frequency int,'
		.'pattern VARCHAR(32768),'
		.'FOREIGN KEY (remote_id) REFERENCES remotes(id)'
		.');');

	query_check('CREATE TABLE IF NOT EXISTS ratings ('
		.'remote_id int NOT NULL,'
		.'user_id int NOT NULL,'
		.'rating tinyint,'
		.'FOREIGN KEY (remote_id) REFERENCES remotes(id),'
		.'FOREIGN KEY (user_id) REFERENCES users(id)'
		.');');

	echo '<br><br>All OK';
}


if ($allow_db_creation) {
	if (isset($_GET['drop_db'])) {
		drop_database();
	}
	if (isset($_GET['create_db'])) {
		create_database();
	}
	echo '<!DOCTYPE html>'
	.'<html>'
	.'<body>'
	.'	<form mehtod="get" action="create-db.php">'
	.'		<input hidden name="create_db" value="true">'
	.'		<button type="submit">Create db</button>'
	.'	</form><br>'
	.'	<form mehtod="get" action="create-db.php">'
	.'		<input hidden name="drop_db" value="true">'
	.'		<button type="submit">Drop db</button>'
	.'	</form>'
	.'</body>'
	.'</html>';
}
?>
