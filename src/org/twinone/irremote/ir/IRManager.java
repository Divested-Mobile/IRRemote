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
package org.twinone.irremote.ir;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.hardware.ConsumerIrManager.CarrierFrequencyRange;

public class IRManager {

	// private static final String TAG = "IRManager";

	private Context mContext;
	private ConsumerIrManager mIrManager;

	public IRManager(Context context) {
		mContext = context;
		mIrManager = (ConsumerIrManager) mContext
				.getSystemService(Context.CONSUMER_IR_SERVICE);
	}

	public boolean hasIrEmitter() {
		return mIrManager.hasIrEmitter();
	}

	/**
	 * 
	 * @return true if the signal has been transmitted
	 */
	public boolean transmit(Signal signal) {
		if (!isFrequencySupported(signal.frequency))
			return false;
		mIrManager.transmit(signal.frequency, signal.pattern);
		return true;
	}

	private boolean isFrequencySupported(int frequency) {
		for (CarrierFrequencyRange cfr : mIrManager.getCarrierFrequencies()) {
			if (frequency <= cfr.getMaxFrequency()
					&& frequency >= cfr.getMinFrequency()) {
				return true;
			}
		}
		return false;
	}
}
