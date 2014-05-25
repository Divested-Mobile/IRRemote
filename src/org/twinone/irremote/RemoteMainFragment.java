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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RemoteMainFragment extends Fragment implements
		View.OnTouchListener {

	private Button mButtonPower;
	private Button mButtonVolUp;
	private Button mButtonVolDown;
	private Button mButtonChUp;
	private Button mButtonChDown;
	private Button mButtonNavUp;
	private Button mButtonNavDown;
	private Button mButtonNavLeft;
	private Button mButtonNavRight;
	private Button mButtonNavOk;
	private Button mButtonBack;
	private Button mButtonMute;
	private Button mButtonMenu;

	private List<Button> mButtons;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_remote_main, container,
				false);

		mButtonPower = (Button) view.findViewById(R.id.button_power);
		mButtonVolUp = (Button) view.findViewById(R.id.button_vol_up);
		mButtonVolDown = (Button) view.findViewById(R.id.button_vol_down);
		mButtonChUp = (Button) view.findViewById(R.id.button_ch_up);
		mButtonChDown = (Button) view.findViewById(R.id.button_ch_down);
		mButtonNavUp = (Button) view.findViewById(R.id.button_nav_up);
		mButtonNavDown = (Button) view.findViewById(R.id.button_nav_down);
		mButtonNavLeft = (Button) view.findViewById(R.id.button_nav_left);
		mButtonNavRight = (Button) view.findViewById(R.id.button_nav_right);
		mButtonNavOk = (Button) view.findViewById(R.id.button_nav_ok);
		mButtonBack = (Button) view.findViewById(R.id.button_back);
		mButtonMute = (Button) view.findViewById(R.id.button_mute);
		mButtonMenu = (Button) view.findViewById(R.id.button_menu);

		mButtons = new ArrayList<Button>();
		mButtons.add(mButtonPower);
		mButtons.add(mButtonVolUp);
		mButtons.add(mButtonVolDown);
		mButtons.add(mButtonChUp);
		mButtons.add(mButtonChDown);
		mButtons.add(mButtonNavUp);
		mButtons.add(mButtonNavDown);
		mButtons.add(mButtonNavLeft);
		mButtons.add(mButtonNavRight);
		mButtons.add(mButtonNavOk);
		mButtons.add(mButtonBack);
		mButtons.add(mButtonMute);
		mButtons.add(mButtonMenu);

		for (Button b : mButtons) {
			b.setOnTouchListener(this);

			int buttonId = getButtonId(b.getId());
			if (getRemote().contains(true, buttonId)) {
				b.setText(getRemote().getButton(true, buttonId).getDisplayName(
						getActivity()));
			} else {
				b.setEnabled(false);
			}
		}

		getActivity().setTitle(getRemote().name);

		return view;
	}

	private Remote getRemote() {
		return ((RemoteActivity) getActivity()).getRemote();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			((RemoteActivity) getActivity()).transmit(true,
					getButtonId(v.getId()));
			return false;
		}
		return false;
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
		default:
			return org.twinone.irremote.Button.ID_NONE;
		}
	}
}
