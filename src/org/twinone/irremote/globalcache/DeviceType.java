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

public class DeviceType extends Listable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7685932096276835276L;
	/** The key for API requests for this device type */
	public String Key;
	/** The name of the device type to display to the user */
	public String DeviceType;

	@Override
	public String getKey() {
		return Key;
	}

	@Override
	public int getType() {
		return UriData.TYPE_DEVICE_TYPE;
	}

	@Override
	public String getDisplayName() {
		return DeviceType;
	}
}
