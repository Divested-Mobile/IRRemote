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

class Button {
   public $id;
   public $text;
   public $signal;

   function __construct($json) {
      $this->id = $json->id;
      $this->text = $json->text;
      $this->signal = Signal::from_pronto($json->code);
   }
   public  function func_string() {
     global $_id_text_map;
     return $_id_text_map[$this->id];
  }
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
	public function serialize() {
		return $this->frequency.','.$this->serialize_pattern();
	}
	public function serialize_pattern() {
		$patt = $this->pattern[0];
		for ($i=1; $i < sizeof($this->pattern); $i++) $patt.=','.$this->pattern[$i];
			return $patt;
	}
}

function get_type_string($remote) {
   if ($remote->details->type == -1) return $remote->details->type_string;
   global $_type_map;
   return $_type_map[$remote->details->type];
}

$_type_map = array(
   // Other -1
   "TV", // 0
   "CABLE", // 1
   "CD", // 2
   "DVD", // 3
   "BLU_RAY", // 4
   "AUDIO", // 5
   "CAMERA", // 6
   "AIR_CON", // 7
);
$_id_text_map = array(
   "UNKNOWN", // 0
   "POWER", // 1
   "POWER_ON", // 2
   "POWER_OFF", // 3
   "VOL_UP", // 4
   "VOL_DOWN", // 5
   "CH_UP", // 6
   "CH_DOWN", // 7
   "NAV_UP", // 8
   "NAV_DOWN", // 9
   "NAV_LEFT", // 10
   "NAV_RIGHT", // 11
   "NAV_OK", // 12
   "BACK", // 13
   "MUTE", // 14
   "MENU", // 15
   "DIGIT_0", // 16
   "DIGIT_1", // 17
   "DIGIT_2", // 18
   "DIGIT_3", // 19
   "DIGIT_4", // 20
   "DIGIT_5", // 21
   "DIGIT_6", // 22
   "DIGIT_7", // 23
   "DIGIT_8", // 24
   "DIGIT_9", // 25
   "SRC", // 26
   "GUIDE", // 27
   "SMART", // 28
   "LAST", // 29
   "CLEAR", // 30
   "EXIT", // 31
   "CC", // 32
   "INFO", // 33
   "SLEEP", // 34
   "PLAY", // 35
   "PAUSE", // 36
   "STOP", // 37
   "FFWD", // 38
   "RWD", // 39
   "NEXT", // 40
   "PREV", // 41
   "REC", // 42
   "DISP", // 43
   "SRC_CD", // 44
   "SRC_AUX", // 45
   "SRC_TAPE", // 46
   "SRC_TUNER", // 47
   "RED", // 48
   "GREEN", // 49
   "BLUE", // 50
   "YELLOW", // 51
   "INPUT_1", // 52
   "INPUT_2", // 53
   "INPUT_3", // 54
   "INPUT_4", // 55
   "INPUT_5", // 56
   "FAN_UP", // 57
   "FAN_DOWN", // 58
   "TEMP_UP", // 59
   "TEMP_DOWN" // 60
   );

?>