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

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.view.View;
import android.widget.Button;

public abstract class BaseButtonFragment extends Fragment implements
		View.OnClickListener {

	protected List<Button> mButtons = new ArrayList<Button>();

	protected void setup() {

		for (Button b : mButtons) {
			int buttonId = getButtonId(b.getId());
			if (getRemote().contains(true, buttonId)) {
				b.setText(getRemote().getButton(true, buttonId).getDisplayName(
						getActivity()));
				b.setOnClickListener(this);
			} else {
				b.setEnabled(false);
			}
		}

		getActivity().setTitle(getRemote().name);

	}

	protected Remote getRemote() {
		if (!isAdded()) {
			throw new IllegalStateException(
					"Called getRemote but was not attached to activity");
		}
		return ((RemoteActivity) getActivity()).getRemote();
	}

	@Override
	public void onClick(View v) {
		((RemoteActivity) getActivity()).transmit(true, getButtonId(v.getId()));
	}

	public int getButtonId(int viewId) {
		switch (viewId) {
		case R.id.button_power:
			return org.twinone.irremote.Button.ID_POWER;
		case R.id.button_vol_up:
			return org.twinone.irremote.Button.ID_VOL_UP;
		case R.id.button_vol_down:
			return org.twinone.irremote.Button.ID_VOL_DOWN;
		case R.id.button_ch_up:
			return org.twinone.irremote.Button.ID_CH_UP;
		case R.id.button_ch_down:
			return org.twinone.irremote.Button.ID_CH_DOWN;
		case R.id.button_nav_up:
			return org.twinone.irremote.Button.ID_NAV_UP;
		case R.id.button_nav_down:
			return org.twinone.irremote.Button.ID_NAV_DOWN;
		case R.id.button_nav_left:
			return org.twinone.irremote.Button.ID_NAV_LEFT;
		case R.id.button_nav_right:
			return org.twinone.irremote.Button.ID_NAV_RIGHT;
		case R.id.button_nav_ok:
			return org.twinone.irremote.Button.ID_NAV_OK;
		case R.id.button_back:
			return org.twinone.irremote.Button.ID_BACK;
		case R.id.button_mute:
			return org.twinone.irremote.Button.ID_MUTE;
		case R.id.button_menu:
			return org.twinone.irremote.Button.ID_MENU;

		case R.id.button_digit_0:
			return org.twinone.irremote.Button.ID_DIGIT_0;
		case R.id.button_digit_1:
			return org.twinone.irremote.Button.ID_DIGIT_1;
		case R.id.button_digit_2:
			return org.twinone.irremote.Button.ID_DIGIT_2;
		case R.id.button_digit_3:
			return org.twinone.irremote.Button.ID_DIGIT_3;
		case R.id.button_digit_4:
			return org.twinone.irremote.Button.ID_DIGIT_4;
		case R.id.button_digit_5:
			return org.twinone.irremote.Button.ID_DIGIT_5;
		case R.id.button_digit_6:
			return org.twinone.irremote.Button.ID_DIGIT_6;
		case R.id.button_digit_7:
			return org.twinone.irremote.Button.ID_DIGIT_7;
		case R.id.button_digit_8:
			return org.twinone.irremote.Button.ID_DIGIT_8;
		case R.id.button_digit_9:
			return org.twinone.irremote.Button.ID_DIGIT_9;
		default:
			return org.twinone.irremote.Button.ID_NONE;
		}
	}
}
