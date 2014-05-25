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

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RemoteFragment extends Fragment implements View.OnClickListener {

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
	private Button mButtonDigit0;
	private Button mButtonDigit1;
	private Button mButtonDigit2;
	private Button mButtonDigit3;
	private Button mButtonDigit4;
	private Button mButtonDigit5;
	private Button mButtonDigit6;
	private Button mButtonDigit7;
	private Button mButtonDigit8;
	private Button mButtonDigit9;

	private Button[] mButtons = { mButtonPower, mButtonVolUp, mButtonVolDown,
			mButtonChUp, mButtonChDown, mButtonNavUp, mButtonNavDown,
			mButtonNavLeft, mButtonNavRight, mButtonNavOk, mButtonBack,
			mButtonMute, mButtonMenu, mButtonDigit0, mButtonDigit1,
			mButtonDigit2, mButtonDigit3, mButtonDigit4, mButtonDigit5,
			mButtonDigit6, mButtonDigit7, mButtonDigit8, mButtonDigit9 };

	private int[] mButtonIds = { R.id.button_power_onoff, R.id.button_vol_up,
			R.id.button_vol_down, R.id.button_ch_up, R.id.button_ch_down,
			R.id.button_nav_up, R.id.button_nav_down, R.id.button_nav_left,
			R.id.button_nav_right, R.id.button_nav_ok, R.id.button_back,
			R.id.button_mute, R.id.button_menu, R.id.button_digit_0,
			R.id.button_digit_1, R.id.button_digit_2, R.id.button_digit_3,
			R.id.button_digit_4, R.id.button_digit_5, R.id.button_digit_6,
			R.id.button_digit_7, R.id.button_digit_8, R.id.button_digit_9, };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_remote_main, container,
				false);
		for (int i = 0; i < mButtons.length; i++) {
			mButtons[i] = (Button) view.findViewById(mButtonIds[i]);
			mButtons[i].setOnClickListener(this);
		}
		mButtons[0].setText("Tesst!!!");

		Log.d("", "mButtonPower.text = " + mButtonPower.getText());

		return view;
	}

	@Override
	public void onClick(View view) {

	}
}
