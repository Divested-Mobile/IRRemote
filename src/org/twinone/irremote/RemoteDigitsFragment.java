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

public class RemoteDigitsFragment extends BaseButtonFragment {

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_remote_digits,
				container, false);

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

}
