package org.twinone.irremote;

import java.io.Serializable;

import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.SignalFactory;

import android.content.Context;

/**
 * For the comments to be readable by others:<br>
 * <b>Common buttons</b> mean:<br>
 * The buttons that have an ID because they're so common that a lot of remotes
 * will have them<br>
 * This does NOT mean that every remote will have them. <br>
 * A common button will have a dedicated place in the remote <br>
 * Using common buttons makes the remote look much nicer
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
	public static final int ID_TIMER = 34;
	public static final int ID_SLEEP = 35;

	// For cable
	public static final int ID_PLAY = 36;
	public static final int ID_PAUSE = 37;
	public static final int ID_STOP = 38;
	public static final int ID_FAST_FORWARD = 39;
	public static final int ID_REWIND = 40;
	public static final int ID_SKIP_NEXT = 41;
	public static final int ID_SKIP_PREV = 42;
	public static final int ID_RECORD = 43;
	public static final int ID_DISP = 44;

	public boolean common;

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
	public int format;
	public String code;

	public String getDisplayName(Context c) {
		if (!isCommonButton())
			return text;
		else
			return getCommonButtonDisplayName(c);
	}

	private String getCommonButtonDisplayName(Context c) {
		switch (id) {
		case ID_POWER:
			return c.getString(R.string.button_text_power);
		case ID_POWER_ON:
			return c.getString(R.string.button_text_power_on);
		case ID_POWER_OFF:
			return c.getString(R.string.button_text_power_off);
		case ID_VOL_UP:
			return c.getString(R.string.button_text_vol_up);
		case ID_VOL_DOWN:
			return c.getString(R.string.button_text_vol_down);
		case ID_CH_UP:
			return c.getString(R.string.button_text_ch_up);
		case ID_CH_DOWN:
			return c.getString(R.string.button_text_ch_down);
		case ID_NAV_UP:
			return c.getString(R.string.button_text_nav_up);
		case ID_NAV_DOWN:
			return c.getString(R.string.button_text_nav_down);
		case ID_NAV_LEFT:
			return c.getString(R.string.button_text_nav_left);
		case ID_NAV_RIGHT:
			return c.getString(R.string.button_text_nav_right);
		case ID_NAV_OK:
			return c.getString(R.string.button_text_nav_ok);
		case ID_BACK:
			return c.getString(R.string.button_text_back);
		case ID_MUTE:
			return c.getString(R.string.button_text_mute);
		case ID_MENU:
			return c.getString(R.string.button_text_menu);
		case ID_DIGIT_0:
			return c.getString(R.string.button_text_digit_0);
		case ID_DIGIT_1:
			return c.getString(R.string.button_text_digit_1);
		case ID_DIGIT_2:
			return c.getString(R.string.button_text_digit_2);
		case ID_DIGIT_3:
			return c.getString(R.string.button_text_digit_3);
		case ID_DIGIT_4:
			return c.getString(R.string.button_text_digit_4);
		case ID_DIGIT_5:
			return c.getString(R.string.button_text_digit_5);
		case ID_DIGIT_6:
			return c.getString(R.string.button_text_digit_6);
		case ID_DIGIT_7:
			return c.getString(R.string.button_text_digit_7);
		case ID_DIGIT_8:
			return c.getString(R.string.button_text_digit_8);
		case ID_DIGIT_9:
			return c.getString(R.string.button_text_digit_9);

		default:
			return "?";
		}
	}

	/**
	 * Returns true if this button is a common button
	 */
	public boolean isCommonButton() {
		return id != ID_NONE;
	}

	public Signal getSignal() {
		Signal s = SignalFactory.parse(format, code);
		return s;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Button))
			// Not a button
			return false;
		final Button b = (Button) o;
		if (common != b.common || id != b.id)
			// Two different buttons
			return false;
		if (id != ID_NONE)
			// Two the same common buttons
			return true;
		// Both undefined buttons
		if (b.text == null && b.text == null)
			return true;
		return text != null && text.equals(b.text);
	}

	@Override
	public int hashCode() {
		return (id + text).hashCode();
	}
}
