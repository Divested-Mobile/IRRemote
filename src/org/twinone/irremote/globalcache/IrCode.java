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

import org.twinone.irremote.Listable;
import org.twinone.irremote.ir.FormatFactory;
import org.twinone.irremote.ir.Signal;

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
		return FormatFactory.parseSignal(FormatFactory.FORMAT_GLOBALCACHE,
				IRCode);

	}

	@Override
	public int getType() {
		return UriData.TYPE_IR_CODE;
	}

	@Override
	public String getDisplayName() {
		return Key;
	}

}
