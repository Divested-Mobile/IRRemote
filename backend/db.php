<?php

// function prettify($jsonString) {
// 	return json_encode(json_decode($jsonString),JSON_PRETTY_PRINT);
// }
function dolog($string) {
	file_put_contents("/var/www/log.txt", $string);
}

$headers = getallheaders();
$KEY = 'af017ebbd416b3c22b82d1ef49b54270';
$reqKey = $headers['X-twinone-key'];

if ($KEY != $reqKey) {
	http_response_code(400);
}


$input = json_decode(file_get_contents('php://input'));



file_put_contents('/var/www/remote.txt', $input);

?>