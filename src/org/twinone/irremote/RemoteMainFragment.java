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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RemoteMainFragment extends BaseButtonFragment {

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

		setup();
		return view;
	}

}
