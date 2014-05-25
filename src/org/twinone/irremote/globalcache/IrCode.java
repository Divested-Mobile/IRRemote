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
package org.twinone.irremote.globalcache;

import java.util.Locale;

import org.twinone.irremote.Button;
import org.twinone.irremote.Listable;
import org.twinone.irremote.Remote;
import org.twinone.irremote.ir.Signal;
import org.twinone.irremote.ir.SignalFactory;

import android.annotation.SuppressLint;

public class IrCode extends Listable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3967117959677153127L;

	public String Key;
	/** The key for API requests for this codeset */
	public String KeyName;
	/** The name of the codeset to display to the user */
	public String IRCode;

	/** Returns a signal that can be directly sent over the IR transmitter */
	@Override
	public String getKey() {
		return Key;
	}

	public Signal getSignal() {
		return SignalFactory.parse(Signal.FORMAT_GLOBALCACHE, IRCode);
	}

	@Override
	public int getType() {
		return UriData.TYPE_IR_CODE;
	}

	@Override
	public String getDisplayName() {
		return KeyName;
	}

	public static Remote toRemote(String name, IrCode[] irCodes) {
		Remote remote = new Remote();
		remote.name = name;
		for (IrCode code : irCodes) {
			Button b = IrCode.toButton(code);
			remote.addButton(b);
			remote.addButton(b);
			// remote.addButton(IrCode.toButton(code));
		}
		return remote;
	}

	public static Button toButton(IrCode irCode) {
		Button button = new Button();
		button.text = irCode.KeyName;
		button.format = Signal.FORMAT_GLOBALCACHE;
		button.code = irCode.IRCode;
		button.id = getBestMatchId(irCode);
		button.common = button.id != Button.ID_NONE;

		return button;
	}

	/** Attempt to get an ID for this button name */
	@SuppressLint("DefaultLocale")
	private static int getBestMatchId(IrCode irCode) {
		final String button = irCode.Key.toLowerCase(Locale.US);

		// Power
		if (button.equals("on, power onoff"))
			return Button.ID_POWER_ON;
		if (button.equals("off, power onoff"))
			return Button.ID_POWER_OFF;
		if (button.contains("power onoff"))
			return Button.ID_POWER;

		// Volumes, channels
		if (button.contains("volume up"))
			return Button.ID_VOL_UP;
		if (button.contains("volume down"))
			return Button.ID_VOL_DOWN;
		if (button.contains("channel up"))
			return Button.ID_CH_UP;
		if (button.contains("channel down"))
			return Button.ID_CH_DOWN;

		// Navigation
		if (button.contains("menu up"))
			return Button.ID_NAV_UP;
		if (button.contains("menu down"))
			return Button.ID_NAV_DOWN;
		if (button.contains("menu left"))
			return Button.ID_NAV_LEFT;
		if (button.contains("menu right"))
			return Button.ID_NAV_RIGHT;
		if (button.contains("menu select"))
			return Button.ID_NAV_OK;

		if (button.equals("back"))
			return Button.ID_BACK;
		if (button.contains("mute"))
			return Button.ID_MUTE;
		// At this point we can safely return a generic "menu" for any button
		// that matches menu, because the specific menu [direction] are already
		// returned above
		if (button.contains("menu"))
			return Button.ID_MENU;

		// Digits
		if (button.contains("digit 0"))
			return Button.ID_DIGIT_0;
		if (button.contains("digit 1"))
			return Button.ID_DIGIT_1;
		if (button.contains("digit 2"))
			return Button.ID_DIGIT_2;
		if (button.contains("digit 3"))
			return Button.ID_DIGIT_3;
		if (button.contains("digit 4"))
			return Button.ID_DIGIT_4;
		if (button.contains("digit 5"))
			return Button.ID_DIGIT_5;
		if (button.contains("digit 6"))
			return Button.ID_DIGIT_6;
		if (button.contains("digit 7"))
			return Button.ID_DIGIT_7;
		if (button.contains("digit 8"))
			return Button.ID_DIGIT_8;
		if (button.contains("digit 9"))
			return Button.ID_DIGIT_9;

		return Button.ID_NONE;
	}

}
