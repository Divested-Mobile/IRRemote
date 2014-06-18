package org.twinone.irremote.ir;

import android.content.Context;

public class Signal {

	public static final int FORMAT_AUTO = 0;
	public static final int FORMAT_PRONTO = 1;
	public static final int FORMAT_GLOBALCACHE = 2;

	public int frequency;
	public int[] pattern;

	public Signal(int frequency, int[] pattern) {
		this.frequency = frequency;
		this.pattern = pattern;
	}

	private boolean isFixed;

	public Signal fix(Context c) {
		if (isFixed)
			return this;
		this.pattern = new SignalCompat(c).fix(frequency, pattern);
		isFixed = true;
		return this;

	}

}
