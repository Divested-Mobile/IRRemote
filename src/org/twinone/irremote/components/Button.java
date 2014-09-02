package org.twinone.irremote.components;

import java.io.Serializable;

import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.SignalFactory;

/**
 * No two buttons on a remote can have the same id
 * 
 * @author Twinone
 */
public class Button implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4924961807483469449L;

	/** Pseudo id that indicates this button has no id */
	public static final int ID_NONE = 0;

	public static final int ID_POWER = 1;
	// TODO decide if "off" is more important than "on"
	public static final int ID_POWER_ON = 2;
	public static final int ID_POWER_OFF = 3;

	public static final int ID_VOL_UP = 4;
	public static final int ID_VOL_DOWN = 5;
	public static final int ID_CH_UP = 6;
	public static final int ID_CH_DOWN = 7;

	public static final int ID_NAV_UP = 8;
	public static final int ID_NAV_DOWN = 9;
	public static final int ID_NAV_LEFT = 10;
	public static final int ID_NAV_RIGHT = 11;
	public static final int ID_NAV_OK = 12;

	public static final int ID_BACK = 13;
	public static final int ID_MUTE = 14;
	public static final int ID_MENU = 15;

	public static final int ID_DIGIT_0 = 16;
	public static final int ID_DIGIT_1 = 17;
	public static final int ID_DIGIT_2 = 18;
	public static final int ID_DIGIT_3 = 19;
	public static final int ID_DIGIT_4 = 20;
	public static final int ID_DIGIT_5 = 21;
	public static final int ID_DIGIT_6 = 22;
	public static final int ID_DIGIT_7 = 23;
	public static final int ID_DIGIT_8 = 24;
	public static final int ID_DIGIT_9 = 25;

	// Since Remote v2
	public static final int ID_INPUT = 26;
	public static final int ID_GUIDE = 27;
	public static final int ID_SMART = 28;
	public static final int ID_LAST = 29;
	public static final int ID_CLEAR = 30;
	public static final int ID_EXIT = 31;
	public static final int ID_CC = 32;
	public static final int ID_INFO = 33;
	public static final int ID_SLEEP = 34;

	// For cable
	public static final int ID_PLAY = 35;
	public static final int ID_PAUSE = 36;
	public static final int ID_STOP = 37;
	public static final int ID_FAST_FORWARD = 38;
	public static final int ID_REWIND = 39;
	public static final int ID_NEXT = 40;
	public static final int ID_PREV = 41;
	public static final int ID_REC = 42;
	public static final int ID_DISP = 43;

	// For audio amplifiers
	public static final int ID_SRC_CD = 44;
	public static final int ID_SRC_AUX = 45;
	public static final int ID_SRC_TAPE = 46;
	public static final int ID_SRC_TUNER = 47;

	// TV
	public static final int ID_RED = 48;
	public static final int ID_GREEN = 49;
	public static final int ID_BLUE = 50;
	public static final int ID_YELLOW = 51;

	// Home theater
	public static final int ID_INPUT_1 = 52;
	public static final int ID_INPUT_2 = 53;
	public static final int ID_INPUT_3 = 54;
	public static final int ID_INPUT_4 = 55;
	public static final int ID_INPUT_5 = 56;

	// Air conditioning
	public static final int ID_FAN_UP = 57;
	public static final int ID_FAN_DOWN = 58;
	public static final int ID_TEMP_UP = 59;
	public static final int ID_TEMP_DOWN = 60;

	/** Custom or unknown buttons are assigned a random id bigger as this */
	public static final int MIN_CUSTOM_ID = 10000;

	/** Ids are to identify common buttons */
	public int id;

	/** URI of the icon that will be displayed with this button */
	public String ic;

	/**
	 * Text that will be shown on the button<br>
	 * The text will also be used to distinguish buttons, which implies that no
	 * two buttons can have the same text.
	 * 
	 * 
	 */
	public String text;

	/**
	 * One of Signal.FORMAT_*
	 */
	public String code;

	// x, y, width, height in px
	public float x;
	public float y;
	public float w;
	public float h;
	// corner radius in px

	public int rtl;
	public int rbl;
	public int rtr;
	public int rbr;

	/** Set the radius for all 4 corners */
	public void setCornerRadius(int radius) {
		rtl = rbl = rtr = rbr = radius;
	}

	public void setCornerRadii(int[] radii) {
		rtl = radii[0];
		rtr = radii[1];
		rbr = radii[2];
		rbl = radii[3];
	}

	public float[] getCornerRadii() {
		return new float[] { rtl, rtr, rbr, rbl };
	}

	public Button(String text) {
		this(0, text);
	}

	public Button(int id) {
		this(id, null);
	}

	public Button(int id, String text) {
		this.id = id;
		this.text = text;
	}

	public String getText() {
		return text;
	}

	/**
	 * Returns true if this button is a common button
	 */
	public boolean isCommon() {
		return id != ID_NONE && id < MIN_CUSTOM_ID;
	}

	public Signal getSignal() {
		// Since v1008 we store everything in pronto
		// BUT USERS CAN STILL HAVE AN OLD FORMAT STORED FROM PRE-v1008!!!
		Signal s = SignalFactory.parse(Signal.FORMAT_AUTO, code);
		return s;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Button))
			// Not a button
			return false;
		final Button b = (Button) o;
		return b.id == this.id;
		// if (id != b.id)
		// // Two different buttons
		// return false;
		// if (id != ID_NONE)
		// // Two the same common buttons
		// return true;
		// // Both undefined buttons
		// if (b.text == null && b.text == null)
		// return true;
		// return text != null && text.equals(b.text);
	}

	@Override
	public int hashCode() {
		return ("button" + id).hashCode();
	}

}
