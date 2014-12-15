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

class Pronto {
	public $pronto;

}

class Signal {
	public $frequency;
	public $pattern;

	/** PHP version of the Java method in SignalFactory */
	public static function from_pronto($in) {
		$res = new self();
		$in = trim($in);
		$pronto = explode(" ", $in);
		foreach ($pronto as $idx => $val) {
			$pronto[$idx] = hexdec($val);
		}
		if ($pronto[0] != 0) {
			return false;
		}
		$freq = (int) (1000000 / ($pronto[1] * 0.241246));

		$bps1 = $pronto[2] * 2;
		$bps2 = $pronto[3] * 2;
		$offset = 4;

		$pattern = array();
		$length = $bps1 + $bps2;
		for ($i = 0; $i < $length; $i++) {
			$pattern[$i] = $pronto[$offset + $i];
			if ($pattern[$i] <= 0) $pattern[$i] = 1;
		}

		$res->frequency = $freq;
		$res->pattern = $pattern;
		return $res;
	}

	public function to_globalcache() {
		return $this->frequency.','.$this->serialize_pattern();
	}
	public function serialize_pattern() {
		$patt = $this->pattern[0];
		for ($i=1; $i < sizeof($this->pattern); $i++) $patt.=','.$this->pattern[$i];
			return $patt;
	}
}


?>