package org.twinone.irremote.providers.lirc;

import org.twinone.irremote.providers.BaseListable;

public class LircListable extends BaseListable {

    /**
     *
     */
    private static final long serialVersionUID = -1291089171782696574L;

    public String href;
    public String name;
    public int type;

    @Override
    public String toString() {
        return name;
    }

}
