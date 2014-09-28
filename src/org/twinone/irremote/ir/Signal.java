package org.twinone.irremote.ir;

import java.util.Arrays;

public class Signal {

	public static final int FORMAT_UNKNOWN = -1;
	public static final int FORMAT_AUTO = 0;
	public static final int FORMAT_PRONTO = 1;
	public static final int FORMAT_GLOBALCACHE = 2;

	public int frequency;
	public int[] pattern;
	private boolean isFixed;

	public Signal(int frequency, int[] pattern) {
		this.frequency = frequency;
		this.pattern = Arrays.copyOf(pattern, pattern.length);
	}

	@Override
	public Signal clone() {
		Signal s = new Signal(frequency, pattern);
		s.isFixed = isFixed;
		return s;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Fixed:" + isFixed + " Signal@"
				+ frequency + "/");
		boolean a = false;
		for (int i : pattern) {
			if (a)
				sb.append(',');
			a = true;
			sb.append(i);
		}
		return sb.toString();
	}

	public Signal fix(SignalCorrector sc) {
		if (isFixed)
			return this;
		this.pattern = sc.fix(frequency, pattern);
		isFixed = true;
		return this;
	}

}
