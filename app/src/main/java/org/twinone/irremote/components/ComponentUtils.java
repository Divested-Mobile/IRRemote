package org.twinone.irremote.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.Log;

import org.twinone.irremote.R;

public class ComponentUtils {

    public static final int[] ICON_IDS = new int[]{Button.ID_POWER,
            Button.ID_MUTE, Button.ID_VOL_DOWN, Button.ID_VOL_UP,
            Button.ID_MENU, Button.ID_GUIDE, Button.ID_NAV_DOWN,
            Button.ID_NAV_UP, Button.ID_NAV_LEFT, Button.ID_NAV_RIGHT,
            Button.ID_BACK,
            Button.ID_SRC, Button.ID_INFO, Button.ID_PLAY, Button.ID_PAUSE,
            Button.ID_RWD, Button.ID_FFWD, Button.ID_PREV, Button.ID_NEXT,
            Button.ID_REC, Button.ID_STOP, Button.ID_FAN_DOWN,
            Button.ID_FAN_UP, Button.ID_TEMP_DOWN, Button.ID_TEMP_UP,
            Button.ID_RED,
            Button.ID_HOME, Button.ID_FAV, Button.ID_GAME, Button.ID_HELP,
            Button.ID_LANG, Button.ID_MSG, Button.ID_SETTING};


    private static final int[] BUTTONS_TV = {Button.ID_POWER, Button.ID_MUTE,
            Button.ID_VOL_UP, Button.ID_VOL_DOWN, Button.ID_CH_UP,
            Button.ID_CH_DOWN, Button.ID_DIGIT_0, Button.ID_DIGIT_1,
            Button.ID_DIGIT_2, Button.ID_DIGIT_3, Button.ID_DIGIT_4,
            Button.ID_DIGIT_5, Button.ID_DIGIT_6, Button.ID_DIGIT_7,
            Button.ID_DIGIT_8, Button.ID_DIGIT_9, Button.ID_MENU,
            Button.ID_NAV_OK, Button.ID_NAV_LEFT, Button.ID_NAV_RIGHT,
            Button.ID_NAV_UP, Button.ID_NAV_DOWN, Button.ID_EXIT};
    private static final int[] BUTTONS_CABLE = {Button.ID_POWER,
            Button.ID_MUTE, Button.ID_VOL_UP, Button.ID_VOL_DOWN,
            Button.ID_CH_UP, Button.ID_CH_DOWN, Button.ID_DIGIT_0,
            Button.ID_DIGIT_1, Button.ID_DIGIT_2, Button.ID_DIGIT_3,
            Button.ID_DIGIT_4, Button.ID_DIGIT_5, Button.ID_DIGIT_6,
            Button.ID_DIGIT_7, Button.ID_DIGIT_8, Button.ID_DIGIT_9,
            Button.ID_MENU, Button.ID_NAV_OK, Button.ID_NAV_LEFT,
            Button.ID_NAV_RIGHT, Button.ID_NAV_UP, Button.ID_NAV_DOWN,
            Button.ID_EXIT, Button.ID_PLAY, Button.ID_PAUSE, Button.ID_STOP,
            Button.ID_PREV, Button.ID_NEXT, Button.ID_FFWD, Button.ID_RWD,
            Button.ID_REC,};
    private static final int[] BUTTONS_AUDIO_AMPLIFIER = {Button.ID_POWER,
            Button.ID_MUTE, Button.ID_VOL_UP, Button.ID_VOL_DOWN,
            Button.ID_INPUT_1, Button.ID_INPUT_2, Button.ID_INPUT_3,
            Button.ID_INPUT_4, Button.ID_INPUT_5,

    };
    private static final int[] BUTTONS_AIR_CONDITIONING = {Button.ID_POWER,
            Button.ID_FAN_UP, Button.ID_FAN_DOWN, Button.ID_TEMP_UP,
            Button.ID_TEMP_DOWN};

    public static Drawable getIconDrawable(Context c, int iconId) {
        return c.getResources().getDrawable(getIconResId(iconId));
    }

    /**
     * Get the drawable res id for a given button id
     */
    public static int getIconResId(int iconId) {
        switch (iconId) {
            case Button.ID_NAV_LEFT:
                return R.drawable.b_nav_left;
            case Button.ID_NAV_RIGHT:
                return R.drawable.b_nav_right;
            case Button.ID_NAV_UP:
                return R.drawable.b_nav_up;
            case Button.ID_NAV_DOWN:
                return R.drawable.b_nav_down;

            case Button.ID_INFO:
                return R.drawable.b_info;

            case Button.ID_PLAY:
                return R.drawable.b_play;
            case Button.ID_POWER:
                return R.drawable.b_power;
            case Button.ID_PAUSE:
                return R.drawable.b_pause;
            case Button.ID_STOP:
                return R.drawable.b_stop;
            case Button.ID_RWD:
                return R.drawable.b_rwd;
            case Button.ID_FFWD:
                return R.drawable.b_ffwd;
            case Button.ID_REC:
                return R.drawable.b_rec;

            case Button.ID_MENU:
                return R.drawable.b_menu;
            case Button.ID_PREV:
                return R.drawable.b_prev;
            case Button.ID_NEXT:
                return R.drawable.b_next;

            case Button.ID_VOL_DOWN:
                return R.drawable.b_vol_down;
            case Button.ID_VOL_UP:
                return R.drawable.b_vol_up;
            case Button.ID_MUTE:
                return R.drawable.b_mute;

            case Button.ID_FAN_UP:
                return R.drawable.b_fan_up;
            case Button.ID_FAN_DOWN:
                return R.drawable.b_fan_down;
            case Button.ID_TEMP_UP:
                return R.drawable.b_temp_up;
            case Button.ID_TEMP_DOWN:
                return R.drawable.b_temp_down;

            case Button.ID_BACK:
                return R.drawable.b_back;
            case Button.ID_SRC:
                return R.drawable.b_src;
            case Button.ID_GUIDE:
                return R.drawable.b_guide;

            case Button.ID_RED:
                return R.drawable.b_circle;

            case Button.ID_HOME:
                return R.drawable.b_home;
            case Button.ID_FAV:
                return R.drawable.b_fav;
            case Button.ID_GAME:
                return R.drawable.b_game;
            case Button.ID_HELP:
                return R.drawable.b_help;
            case Button.ID_LANG:
                return R.drawable.b_lang;
            case Button.ID_MSG:
                return R.drawable.b_msg;
            case Button.ID_SETTING:
                return R.drawable.b_setting;

            case 0:
                return 0;
        }
        return R.drawable.ic_launcher;
    }

    // recycle button ids...
    public static int getIconIdForCommonButton(int id) {
        if (id == Button.ID_CH_UP)
            return Button.ID_NAV_UP;
        else if (id == Button.ID_CH_DOWN)
            return Button.ID_NAV_DOWN;
        else if (id == Button.ID_GREEN || id == Button.ID_BLUE || id == Button.ID_YELLOW)
            return Button.ID_RED;

        for (int i : ICON_IDS)
            if (id == i)
                return i;
        return Button.ID_UNKNOWN;
    }

    public static int getForegroundColor(Context c, int color) {
        int resId = -1;
        switch (color) {
            case Button.BG_TRANSPARENT:
                resId = android.R.color.transparent;
                break;
            case Button.BG_RED:
                resId = R.color.material_red_500;
                break;
            case Button.BG_PINK:
                resId = R.color.material_pink_500;
                break;
            case Button.BG_PURPLE:
                resId = R.color.material_purple_500;
                break;
            case Button.BG_DEEP_PURPLE:
                resId = R.color.material_deep_purple_500;
                break;
            case Button.BG_INDIGO:
                resId = R.color.material_indigo_500;
                break;
            case Button.BG_BLUE:
                resId = R.color.material_blue_500;
                break;
            case Button.BG_LIGHT_BLUE:
                resId = R.color.material_light_blue_500;
                break;
            case Button.BG_CYAN:
                resId = R.color.material_cyan_500;
                break;
            case Button.BG_TEAL:
                resId = R.color.material_teal_500;
                break;
            case Button.BG_GREEN:
                resId = R.color.material_green_500;
                break;
            case Button.BG_LIGHT_GREEN:
                resId = R.color.material_light_green_500;
                break;
            case Button.BG_LIME:
                resId = R.color.material_lime_500;
                break;
            case Button.BG_YELLOW:
                resId = R.color.material_yellow_500;
                break;
            case Button.BG_AMBER:
                resId = R.color.material_amber_500;
                break;
            case Button.BG_ORANGE:
                resId = R.color.material_orange_500;
                break;
            case Button.BG_DEEP_ORANGE:
                resId = R.color.material_deep_orange_500;
                break;
            case Button.BG_BROWN:
                resId = R.color.material_brown_500;
                break;
            case Button.BG_GREY:
                resId = R.color.material_grey_500;
                break;
            case Button.BG_BLUE_GREY:
                resId = R.color.material_blue_grey_500;
                break;
            case Button.BG_WHITE:
                resId = android.R.color.white;
                break;
            case Button.BG_BLACK:
                resId = android.R.color.black;
                break;
        }
        if (resId != -1) {
            return c.getResources().getColor(resId);
        }
        return -1;
    }

    /**
     * Returns the array resource id for the specified Button.BG* color
     *
     * @param color
     * @return
     */

    // "red", "pink", "purple",
    // "deep_purple", "indigo", "blue", "light_blue", "cyan", "teal",
    // "green", "light_green", "lime", "yellow", "amber", "orange",
    // "deep_orange", "brown", "grey", "blue_grey"
    //
    private static int getGradientResIdPressed(int color) {
        switch (color) {
            case Button.BG_TRANSPARENT:
                return R.array.gradient_transparent_pressed;
            case Button.BG_RED:
                return R.array.gradient_red_pressed;
            case Button.BG_PINK:
                return R.array.gradient_pink_pressed;
            case Button.BG_PURPLE:
                return R.array.gradient_purple_pressed;
            case Button.BG_DEEP_PURPLE:
                return R.array.gradient_deep_purple_pressed;
            case Button.BG_INDIGO:
                return R.array.gradient_indigo_pressed;
            case Button.BG_BLUE:
                return R.array.gradient_blue_pressed;
            case Button.BG_LIGHT_BLUE:
                return R.array.gradient_light_blue_pressed;
            case Button.BG_CYAN:
                return R.array.gradient_cyan_pressed;
            case Button.BG_TEAL:
                return R.array.gradient_teal_pressed;
            case Button.BG_GREEN:
                return R.array.gradient_green_pressed;
            case Button.BG_LIGHT_GREEN:
                return R.array.gradient_light_green_pressed;
            case Button.BG_LIME:
                return R.array.gradient_lime_pressed;
            case Button.BG_YELLOW:
                return R.array.gradient_yellow_pressed;
            case Button.BG_AMBER:
                return R.array.gradient_amber_pressed;
            case Button.BG_ORANGE:
                return R.array.gradient_orange_pressed;
            case Button.BG_DEEP_ORANGE:
                return R.array.gradient_deep_orange_pressed;
            case Button.BG_BROWN:
                return R.array.gradient_brown_pressed;
            case Button.BG_GREY:
                return R.array.gradient_grey_pressed;
            case Button.BG_BLUE_GREY:
                return R.array.gradient_blue_grey_pressed;
            case Button.BG_WHITE:
                return R.array.gradient_white_pressed;
            case Button.BG_BLACK:
                return R.array.gradient_black_pressed;
            default:
                return R.array.gradient_solid_pressed;
        }
    }

    private static int getGradientResId(int color) {
        switch (color) {
            case Button.BG_TRANSPARENT:
                return R.array.gradient_transparent;
            case Button.BG_RED:
                return R.array.gradient_red;
            case Button.BG_PINK:
                return R.array.gradient_pink;
            case Button.BG_PURPLE:
                return R.array.gradient_purple;
            case Button.BG_DEEP_PURPLE:
                return R.array.gradient_deep_purple;
            case Button.BG_INDIGO:
                return R.array.gradient_indigo;
            case Button.BG_BLUE:
                return R.array.gradient_blue;
            case Button.BG_LIGHT_BLUE:
                return R.array.gradient_light_blue;
            case Button.BG_CYAN:
                return R.array.gradient_cyan;
            case Button.BG_TEAL:
                return R.array.gradient_teal;
            case Button.BG_GREEN:
                return R.array.gradient_green;
            case Button.BG_LIGHT_GREEN:
                return R.array.gradient_light_green;
            case Button.BG_LIME:
                return R.array.gradient_lime;
            case Button.BG_YELLOW:
                return R.array.gradient_yellow;
            case Button.BG_AMBER:
                return R.array.gradient_amber;
            case Button.BG_ORANGE:
                return R.array.gradient_orange;
            case Button.BG_DEEP_ORANGE:
                return R.array.gradient_deep_orange;
            case Button.BG_BROWN:
                return R.array.gradient_brown;
            case Button.BG_GREY:
                return R.array.gradient_grey;
            case Button.BG_BLUE_GREY:
                return R.array.gradient_blue_grey;
            case Button.BG_WHITE:
                return R.array.gradient_white;
            case Button.BG_BLACK:
                return R.array.gradient_black;
            default:
                return R.array.gradient_solid;
        }
    }

    /**
     * Get a drawable with the corner radii set
     */
    public static GradientDrawable getGradientDrawable(Context c, int color,
                                                       boolean pressed) {
        GradientDrawable d = new GradientDrawable();
        d.setOrientation(Orientation.TOP_BOTTOM);
        int id = pressed ? getGradientResIdPressed(color)
                : getGradientResId(color);
        if (id != 0) {
            int[] colors = c.getResources().getIntArray(id);
            d.setColors(colors);
        }
        return d;
    }

    public static String getCommonButtonDisplayName(int id, Context c) {
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
            case Button.ID_SRC:
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
            case Button.ID_FFWD:
                return c.getString(R.string.button_text_fast_forward);
            case Button.ID_RWD:
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
            case Button.ID_HOME:
                return c.getString(R.string.button_text_home);
            case Button.ID_LIST:
                return c.getString(R.string.button_text_list);
            case Button.ID_FAV:
                return c.getString(R.string.button_text_fav);
            case Button.ID_GAME:
                return c.getString(R.string.button_text_game);
            case Button.ID_HELP:
                return c.getString(R.string.button_text_help);
            case Button.ID_LANG:
                return c.getString(R.string.button_text_lang);
            case Button.ID_MSG:
                return c.getString(R.string.button_text_msg);
            case Button.ID_SETTING:
                return c.getString(R.string.button_text_setting);

            default:
                return "?";
        }
    }

    private static int[] getButtonsForType(int type) {
        switch (type) {
            case Remote.TYPE_TV:
                return BUTTONS_TV;

            case Remote.TYPE_CABLE:
            case Remote.TYPE_BLU_RAY:
                return BUTTONS_CABLE;

            case Remote.TYPE_AUDIO:
                return BUTTONS_AUDIO_AMPLIFIER;

            case Remote.TYPE_AIR_CON:
                return BUTTONS_AIR_CONDITIONING;
            default:
                Log.w("", "Invalid remote type requested!: " + type);
                return BUTTONS_TV;
        }
    }

    public static Remote createEmptyRemote(Context c, int type) {
        Remote r = new Remote();
        r.details.type = type;

        int[] bb = getButtonsForType(type);
        for (int btn : bb) {
            String name = ComponentUtils.getCommonButtonDisplayName(btn, c);
            r.addButton(new Button(btn, name));
        }

        return r;
    }

}
