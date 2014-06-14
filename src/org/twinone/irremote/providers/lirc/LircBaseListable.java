package org.twinone.irremote.providers.lirc;

import org.twinone.irremote.providers.BaseListable;

public abstract class LircBaseListable extends BaseListable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1291089171782696574L;

	public abstract String getKey();

	public abstract int getType();

}
