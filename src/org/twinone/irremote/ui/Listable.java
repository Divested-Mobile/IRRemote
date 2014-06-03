package org.twinone.irremote.ui;

import java.io.Serializable;

public abstract class Listable implements Comparable<Listable>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5249675491365192665L;

	public abstract String getKey();

	public abstract int getType();

	public abstract String getDisplayName();

	@Override
	public int compareTo(Listable another) {
		if (getDisplayName() == null || another == null
				|| another.getDisplayName() == null) {
			return 0;
		}
		return getDisplayName().compareToIgnoreCase(another.getDisplayName());
	}
}
