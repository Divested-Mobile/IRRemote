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

import org.twinone.irremote.ir.IRTransmitter;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RemoteFragment extends Fragment implements View.OnClickListener {

	private static final String TAG = "RemoteFragment";

	private Remote mRemote;
	private IRTransmitter mTransmitter;
	protected List<Button> mButtons = new ArrayList<Button>();

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

	private Button mButton0;
	private Button mButton1;
	private Button mButton2;
	private Button mButton3;
	private Button mButton4;
	private Button mButton5;
	private Button mButton6;
	private Button mButton7;
	private Button mButton8;
	private Button mButton9;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTransmitter = new IRTransmitter(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_remote, container, false);

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

		mButton0 = (Button) view.findViewById(R.id.button_digit_0);
		mButton1 = (Button) view.findViewById(R.id.button_digit_1);
		mButton2 = (Button) view.findViewById(R.id.button_digit_2);
		mButton3 = (Button) view.findViewById(R.id.button_digit_3);
		mButton4 = (Button) view.findViewById(R.id.button_digit_4);
		mButton5 = (Button) view.findViewById(R.id.button_digit_5);
		mButton6 = (Button) view.findViewById(R.id.button_digit_6);
		mButton7 = (Button) view.findViewById(R.id.button_digit_7);
		mButton8 = (Button) view.findViewById(R.id.button_digit_8);
		mButton9 = (Button) view.findViewById(R.id.button_digit_9);

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

		mButtons.add(mButton0);
		mButtons.add(mButton1);
		mButtons.add(mButton2);
		mButtons.add(mButton3);
		mButtons.add(mButton4);
		mButtons.add(mButton5);
		mButtons.add(mButton6);
		mButtons.add(mButton7);
		mButtons.add(mButton8);
		mButtons.add(mButton9);

		setup();
		return view;
	}

	protected void setup() {
		if (mRemote == null || mButtons == null)
			return;
		for (Button b : mButtons) {
			int buttonId = getButtonId(b.getId());
			if (mRemote.contains(true, buttonId)) {
				b.setText(mRemote.getButton(true, buttonId).getDisplayName(
						getActivity()));
				b.setOnClickListener(this);
				b.setEnabled(true);
			} else {
				b.setEnabled(false);
				b.setOnClickListener(null);
				b.setText(null);
			}
		}
		getActivity().setTitle(mRemote.name);
	}

	@Override
	public void onClick(View v) {
		transmit(true, getButtonId(v.getId()));
	}

	public void transmit(boolean common, int id) {
		final org.twinone.irremote.Button b = mRemote.getButton(common, id);
		mTransmitter.transmit(b.getSignal());
	}

	public void setRemote(String remoteName) {
		Remote remote = Remote.load(getActivity(), remoteName);
		if (remote == null) {
			Log.w(TAG, "Ignoring null remote");
		}
		mRemote = remote;

		setup();

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
