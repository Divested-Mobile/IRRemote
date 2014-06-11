package org.twinone.irremote;

import java.lang.reflect.Field;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.util.SparseIntArray;

public class ButtonUtils {

	private static SparseIntArray mButtonIdArray;

	public SparseIntArray getArray() {
		return mButtonIdArray;
	}

	public ButtonUtils(Context c) {
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
		SparseIntArray result = new SparseIntArray(
				org.twinone.irremote.Button.BUTTON_ID_COUNT);
		final Class<?> button = org.twinone.irremote.Button.class;
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

		default:
			return "?";
		}
	}

}
