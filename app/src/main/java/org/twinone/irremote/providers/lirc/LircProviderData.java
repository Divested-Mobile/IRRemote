package org.twinone.irremote.providers.lirc;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.twinone.irremote.util.SimpleCache;

import java.io.Serializable;

public class LircProviderData implements Serializable {

    /**
     * A manufacturer
     */
    public static final int TYPE_MANUFACTURER = 0;
    // The type we want to query
    public int targetType = TYPE_MANUFACTURER;
    /**
     * A specific menu_main / button list
     */
    public static final int TYPE_CODESET = 2;
    /**
     * An IR code
     */
    public static final int TYPE_IR_CODE = 3;
    /**
     *
     */
    private static final long serialVersionUID = -8091426297558105438L;
    // API key that was generated using email address twinonetest@gmail.com
    private static final String BASE_URL = "http://lirc.sourceforge.net/remotes/";
    public String manufacturer;
    public String codeset;

    private static LircProviderData deserialize(String data) {
        return new Gson().fromJson(data, LircProviderData.class);
    }

    public String getUrl() {
        final String fqn = getFullyQualifiedName("/");
        Log.d("", "getUrl: " + fqn);
        return fqn == null ? BASE_URL : BASE_URL + fqn;
    }

    // never null
    public String getFullyQualifiedName(String separator) {
        StringBuilder sb = new StringBuilder();
        if (targetType == TYPE_MANUFACTURER)
            return null;
        sb.append(manufacturer);
        if (targetType == TYPE_CODESET)
            return sb.toString();
        sb.append(separator).append(codeset);
        return sb.toString();
    }

    public String getCacheName() {
        final String fqn = getFullyQualifiedName("_");
        return fqn == null ? "LIRC" : "LIRC_" + fqn;
    }

    @Override
    public String toString() {
        return getFullyQualifiedName("/");
    }

    /**
     * May be null if no manufacturer has been selected
     */
    public String getLastSelectedData() {
        switch (targetType) {
            case TYPE_CODESET:
                return manufacturer;
            case TYPE_IR_CODE:
                return codeset;
            default:
                return null;
        }
    }

    public void removeFromCache(Context c) {
        SimpleCache.remove(c, getCacheName());
    }

    public boolean isAvailableInCache(Context c) {
        return SimpleCache.isAvailable(c, getCacheName());
    }

    public LircProviderData clone() {
        return deserialize(serialize());
    }

    String serialize() {
        return new Gson().toJson(this);
    }

}