package org.twinone.irremote.providers;

import java.io.Serializable;

public abstract class BaseListable implements Comparable<BaseListable>,
        Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 5249675491365192665L;

    public abstract String getDisplayName();

    @Override
    public int compareTo(BaseListable another) {
        if (getDisplayName() == null || another == null
                || another.getDisplayName() == null) {
            return 0;
        }
        return getDisplayName().compareToIgnoreCase(another.getDisplayName());
    }
}
