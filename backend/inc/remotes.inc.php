<?php

function clean_remote(array &$remote) {
	foreach ($remote['buttons'] as $key => $value) {
		unset($remote['buttons'][$key]);
		$new_val = array();
		$new_val['uid'] = $value['uid'];
		$new_val['id'] = $value['id'];
		$new_val['code'] = $value['code'];
		$remote['buttons'][$key] = $new_val;
	}
}


?>