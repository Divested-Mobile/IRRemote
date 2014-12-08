<?php

function validate_email($email) {
	return filter_var($email, FILTER_VALIDATE_EMAIL);
}

function validate_user($user) {
	// starts and ends with alphanumeric, may contain -_
	return preg_match('/^[A-Za-z][A-Za-z0-9]*(?:[_-][A-Za-z0-9]+)*$/', $user);
}

?>