package org.twinone.irremote.ir;

import java.io.Serializable;

public class Signal implements Serializable {

	
	public static final int FORMAT_AUTO = 0;
	public static final int FORMAT_PRONTO = 1;
	public static final int FORMAT_GLOBALCACHE = 2;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4245046369663289219L;

	public int frequency;
	public int[] pattern;

}
