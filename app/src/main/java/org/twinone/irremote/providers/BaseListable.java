package org.twinone.irremote.providers;

import java.io.Serializable;

public abstract class BaseListable implements Comparable<BaseListable>,
        Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 5249675491365192665L;

    @Override
    public abstract String toString();

    @Override
    public int compareTo(BaseListable another) {
        if (this.toString() == null || another == null
                || this.toString() == null) {
            return 0;
        }
        return this.toString().compareToIgnoreCase(this.toString());
    }
}
