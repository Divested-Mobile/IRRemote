package org.twinone.irremote.providers.twinone;

import android.content.Context;

import com.google.gson.Gson;

import org.twinone.irremote.util.FileUtils;

import java.io.File;

public class UserInfo {

    public static final int MASK_ID = 1 << 0;
    public static final int MASK_USERNAME = 1 << 1;
    public static final int MASK_ACCESS_TOKEN = 1 << 2;
    public static final int MASK_PASSWORD = 1 << 3;
    public static final int MASK_EMAIL = 1 << 4;
    public static final int MASK_LANGUAGE = 1 << 5;
    public int id;
    public String username;
    public String access_token;
    public String password;
    public String email;
    public String language;

    private static File getFile(Context c) {
        return new File(c.getFilesDir(), "userinfo.json");
    }

    public static void save(Context c, UserInfo userInfo) {
        // Never store passwords in clear text
        userInfo.password = null;
        String json = new Gson().toJson(userInfo);
        FileUtils.write(getFile(c), json);
    }

    public static UserInfo load(Context c) {
        String json = FileUtils.read(getFile(c));
        return new Gson().fromJson(json, UserInfo.class);
    }

    /**
     * Remove everything from this {@link UserInfo} except the fields specified
     * in the mask {@link UserInfo}
     *
     * @param mask
     */
    public void mask(int mask) {
        if ((MASK_ID & mask) != 0)
            id = 0;
        if ((MASK_USERNAME & mask) != 0)
            username = null;
        if ((MASK_ACCESS_TOKEN & mask) != 0)
            access_token = null;
        if ((MASK_PASSWORD & mask) != 0)
            password = null;
        if ((MASK_EMAIL & mask) != 0)
            email = null;
        if ((MASK_LANGUAGE & mask) != 0)
            language = null;
    }

    public void save(Context c) {
        save(c, this);
    }
}
