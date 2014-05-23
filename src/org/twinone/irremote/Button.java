/*
 * Copyright 2014 Luuk Willemsen (Twinone)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.twinone.irremote;

import java.io.Serializable;

import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.SignalFactory;

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

	/**
	 * The id is intended for use with very common buttons
	 */
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

	public String getDisplayName() {
		if (!isCommonButton())
			return text;
		else
			return getCommonButtonDisplayName();
	}

	private String getCommonButtonDisplayName() {
		return "";
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

}
