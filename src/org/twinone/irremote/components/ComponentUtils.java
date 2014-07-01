package org.twinone.irremote.components;

import java.lang.reflect.Field;
import java.util.Locale;

import org.twinone.irremote.R;

import android.content.Context;
import android.util.Log;
import android.util.SparseIntArray;

public class ComponentUtils {

	private static SparseIntArray mButtonIdArray;

	public SparseIntArray getArray() {
		return mButtonIdArray;
	}

	public ComponentUtils(Context c) {
		if (mButtonIdArray == null) {
			mButtonIdArray = getButtonIdList(c);
		}
	}

	public int getButtonIdCount() {
		return mButtonIdArray.size();
	}

	public int getButtonId(int resId) {
		final int index = mButtonIdArray.indexOfValue(resId);
		return (index < 0) ? 0 : mButtonIdArray.keyAt(index);
	}

	public int getResId(int buttonId) {
		final int index = mButtonIdArray.indexOfKey(buttonId);
		return (index < 0) ? 0 : mButtonIdArray.valueAt(index);
	}

	/**
	 * Load the mappings of button res id's and button id's
	 * 
	 * @param c
	 * @return
	 */
	private static SparseIntArray getButtonIdList(Context c) {
		SparseIntArray result = new SparseIntArray();
		final Class<?> button = org.twinone.irremote.components.Button.class;
		try {
			for (Field f : button.getFields()) {
				final String name = f.getName();
				if (name.startsWith("ID_")) {
					final String resName = "button_"
							+ name.substring(3).toLowerCase(Locale.ENGLISH);
					int bid = f.getInt(button);
					int rid = c.getResources().getIdentifier(resName, "id",
							c.getPackageName());
					result.put(bid, rid);
				}
			}
		} catch (Exception e) {
			Log.d("", "Error: ", e);
		}
		return result;
	}

	public static String getCommonButtonDisplyaName(int id, Context c) {
		switch (id) {
		case Button.ID_POWER:
			return c.getString(R.string.button_text_power);
		case Button.ID_POWER_ON:
			return c.getString(R.string.button_text_power_on);
		case Button.ID_POWER_OFF:
			return c.getString(R.string.button_text_power_off);
		case Button.ID_VOL_UP:
			return c.getString(R.string.button_text_vol_up);
		case Button.ID_VOL_DOWN:
			return c.getString(R.string.button_text_vol_down);
		case Button.ID_CH_UP:
			return c.getString(R.string.button_text_ch_up);
		case Button.ID_CH_DOWN:
			return c.getString(R.string.button_text_ch_down);
		case Button.ID_NAV_UP:
			return c.getString(R.string.button_text_nav_up);
		case Button.ID_NAV_DOWN:
			return c.getString(R.string.button_text_nav_down);
		case Button.ID_NAV_LEFT:
			return c.getString(R.string.button_text_nav_left);
		case Button.ID_NAV_RIGHT:
			return c.getString(R.string.button_text_nav_right);
		case Button.ID_NAV_OK:
			return c.getString(R.string.button_text_nav_ok);
		case Button.ID_BACK:
			return c.getString(R.string.button_text_back);
		case Button.ID_MUTE:
			return c.getString(R.string.button_text_mute);
		case Button.ID_MENU:
			return c.getString(R.string.button_text_menu);
		case Button.ID_DIGIT_0:
			return c.getString(R.string.button_text_digit_0);
		case Button.ID_DIGIT_1:
			return c.getString(R.string.button_text_digit_1);
		case Button.ID_DIGIT_2:
			return c.getString(R.string.button_text_digit_2);
		case Button.ID_DIGIT_3:
			return c.getString(R.string.button_text_digit_3);
		case Button.ID_DIGIT_4:
			return c.getString(R.string.button_text_digit_4);
		case Button.ID_DIGIT_5:
			return c.getString(R.string.button_text_digit_5);
		case Button.ID_DIGIT_6:
			return c.getString(R.string.button_text_digit_6);
		case Button.ID_DIGIT_7:
			return c.getString(R.string.button_text_digit_7);
		case Button.ID_DIGIT_8:
			return c.getString(R.string.button_text_digit_8);
		case Button.ID_DIGIT_9:
			return c.getString(R.string.button_text_digit_9);
		case Button.ID_INPUT:
			return c.getString(R.string.button_text_input);
		case Button.ID_GUIDE:
			return c.getString(R.string.button_text_guide);
		case Button.ID_SMART:
			return c.getString(R.string.button_text_smart);
		case Button.ID_LAST:
			return c.getString(R.string.button_text_last);
		case Button.ID_CLEAR:
			return c.getString(R.string.button_text_clear);
		case Button.ID_EXIT:
			return c.getString(R.string.button_text_exit);
		case Button.ID_CC:
			return c.getString(R.string.button_text_cc);
		case Button.ID_INFO:
			return c.getString(R.string.button_text_info);
		case Button.ID_SLEEP:
			return c.getString(R.string.button_text_sleep);
		case Button.ID_PLAY:
			return c.getString(R.string.button_text_play);
		case Button.ID_PAUSE:
			return c.getString(R.string.button_text_pause);
		case Button.ID_STOP:
			return c.getString(R.string.button_text_stop);
		case Button.ID_FAST_FORWARD:
			return c.getString(R.string.button_text_fast_forward);
		case Button.ID_REWIND:
			return c.getString(R.string.button_text_rewind);
		case Button.ID_NEXT:
			return c.getString(R.string.button_text_next);
		case Button.ID_PREV:
			return c.getString(R.string.button_text_prev);
		case Button.ID_REC:
			return c.getString(R.string.button_text_rec);
		case Button.ID_DISP:
			return c.getString(R.string.button_text_disp);
		case Button.ID_SRC_CD:
			return c.getString(R.string.button_text_src_cd);
		case Button.ID_SRC_AUX:
			return c.getString(R.string.button_text_src_aux);
		case Button.ID_SRC_TAPE:
			return c.getString(R.string.button_text_src_tape);
		case Button.ID_SRC_TUNER:
			return c.getString(R.string.button_text_src_tuner);
		case Button.ID_RED:
			return c.getString(R.string.button_text_red);
		case Button.ID_GREEN:
			return c.getString(R.string.button_text_green);
		case Button.ID_BLUE:
			return c.getString(R.string.button_text_blue);
		case Button.ID_YELLOW:
			return c.getString(R.string.button_text_yellow);
		case Button.ID_INPUT_1:
			return c.getString(R.string.button_text_input_1);
		case Button.ID_INPUT_2:
			return c.getString(R.string.button_text_input_2);
		case Button.ID_INPUT_3:
			return c.getString(R.string.button_text_input_3);
		case Button.ID_INPUT_4:
			return c.getString(R.string.button_text_input_4);
		case Button.ID_INPUT_5:
			return c.getString(R.string.button_text_input_5);
		case Button.ID_FAN_UP:
			return c.getString(R.string.button_text_fan_up);
		case Button.ID_FAN_DOWN:
			return c.getString(R.string.button_text_fan_down);
		case Button.ID_TEMP_UP:
			return c.getString(R.string.button_text_temp_up);
		case Button.ID_TEMP_DOWN:
			return c.getString(R.string.button_text_temp_down);

		default:
			return "?";
		}
	}

	public static final int[] BUTTONS_TV = { Button.ID_POWER, Button.ID_MUTE,
			Button.ID_VOL_UP, Button.ID_VOL_DOWN, Button.ID_CH_UP,
			Button.ID_CH_DOWN, Button.ID_DIGIT_0, Button.ID_DIGIT_1,
			Button.ID_DIGIT_2, Button.ID_DIGIT_3, Button.ID_DIGIT_4,
			Button.ID_DIGIT_5, Button.ID_DIGIT_6, Button.ID_DIGIT_7,
			Button.ID_DIGIT_8, Button.ID_DIGIT_9, Button.ID_MENU,
			Button.ID_NAV_OK, Button.ID_NAV_LEFT, Button.ID_NAV_RIGHT,
			Button.ID_NAV_UP, Button.ID_NAV_DOWN, Button.ID_EXIT };
	public static final int[] BUTTONS_CABLE = { Button.ID_POWER,
			Button.ID_MUTE, Button.ID_VOL_UP, Button.ID_VOL_DOWN,
			Button.ID_CH_UP, Button.ID_CH_DOWN, Button.ID_DIGIT_0,
			Button.ID_DIGIT_1, Button.ID_DIGIT_2, Button.ID_DIGIT_3,
			Button.ID_DIGIT_4, Button.ID_DIGIT_5, Button.ID_DIGIT_6,
			Button.ID_DIGIT_7, Button.ID_DIGIT_8, Button.ID_DIGIT_9,
			Button.ID_MENU, Button.ID_NAV_OK, Button.ID_NAV_LEFT,
			Button.ID_NAV_RIGHT, Button.ID_NAV_UP, Button.ID_NAV_DOWN,
			Button.ID_EXIT, Button.ID_PLAY, Button.ID_PAUSE, Button.ID_STOP,
			Button.ID_PREV, Button.ID_NEXT, Button.ID_FAST_FORWARD,
			Button.ID_REWIND, Button.ID_REC, };

	public static final int[] BUTTONS_AUDIO_AMPLIFIER = { Button.ID_POWER,
			Button.ID_MUTE, Button.ID_VOL_UP, Button.ID_VOL_DOWN,
			Button.ID_INPUT_1, Button.ID_INPUT_2, Button.ID_INPUT_3,
			Button.ID_INPUT_4, Button.ID_INPUT_5,

	};
	public static final int[] BUTTONS_AIR_CONDITIONING = { Button.ID_POWER,
			Button.ID_FAN_UP, Button.ID_FAN_DOWN, Button.ID_TEMP_UP,
			Button.ID_TEMP_DOWN };

	public static int[] getButtonsForType(int type) {
		switch (type) {
		case Remote.TYPE_TV:
			return BUTTONS_TV;

		case Remote.TYPE_CABLE:
		case Remote.TYPE_BLURAY:
			return BUTTONS_CABLE;

		case Remote.TYPE_AUDIO_AMPLIFIER:
			return BUTTONS_AUDIO_AMPLIFIER;

		case Remote.TYPE_AIR_CONDITIONER:
			return BUTTONS_AIR_CONDITIONING;
		default:
			return null;
		}
	}

	public static Remote createEmptyRemote(Context c, int type) {
		Remote r = new Remote();
		r.options.type = type;

		int[] bb = getButtonsForType(type);
		for (int i = 0; i < bb.length; i++) {
			String name = ComponentUtils.getCommonButtonDisplyaName(bb[i], c);
			Button b = new Button(bb[i], name);
			r.addButton(b);
		}

		return r;
	}

	public static int getLayout(int type) {
		switch (type) {
		case Remote.TYPE_CABLE:
		case Remote.TYPE_BLURAY:
			return R.layout.fragment_remote_cable;

		case Remote.TYPE_AIR_CONDITIONER:
			return R.layout.fragment_remote_air_conditioner;

		case Remote.TYPE_AUDIO_AMPLIFIER:
			return R.layout.fragment_remote_audio_amplifier;

		default:
			return R.layout.fragment_remote_tv;
		}
	}

}
