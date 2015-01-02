package org.twinone.irremote.account;

import android.content.Context;

import com.google.gson.Gson;

import org.twinone.irremote.util.FileUtils;

import java.io.File;
import java.io.Serializable;

public class UserInfo implements Serializable {

    public static final int MASK_ID = 1;
    public static final int MASK_ACCESS_TOKEN = 1 << 2;
    public static final int MASK_USERNAME = 1 << 1;
    public static final int MASK_PASSWORD = 1 << 3;
    public static final int MASK_EMAIL = 1 << 4;
    public static final int MASK_COUNTRY = 1 << 5;

    private static final int FILTER_SERVER_AUTH = MASK_USERNAME | MASK_ACCESS_TOKEN;

    public Integer id;
    public String username;
    public String access_token;
    public String email;
    private String password;
    private String country;

    private static File getFile(Context c) {
        return new File(c.getFilesDir(), "userinfo.json");
    }

    private UserInfo() {
    }

    private static void save(Context c, UserInfo userInfo) {
        // Never store passwords in clear text
        userInfo.password = null;
        String json = new Gson().toJson(userInfo);
        FileUtils.write(getFile(c), json);
    }

    public static UserInfo load(Context c) {
        String json = FileUtils.read(getFile(c));
        UserInfo ui = new Gson().fromJson(json, UserInfo.class);
        return ui == null ? new UserInfo() : ui;
    }

    public boolean isRegisteredButNotVerified() {
        return username != null && access_token == null;
    }

    public boolean isLoggedIn() {
        return access_token != null;
    }

    public UserInfo logout() {
        access_token = null;
        return this;
    }

    /**
     * @return The UserInfo needed to authenticate to the server
     */
    public static UserInfo getAuthInfo(Context c) {
        return load(c).mask(FILTER_SERVER_AUTH);
    }

    /**
     * Clear everything except the fields in the mask
     *
     * @return This UserInfo
     */
    public UserInfo mask(int mask) {
        if ((MASK_ID & mask) == 0) id = null;
        if ((MASK_USERNAME & mask) == 0) username = null;
        if ((MASK_ACCESS_TOKEN & mask) == 0) access_token = null;
        if ((MASK_PASSWORD & mask) == 0) password = null;
        if ((MASK_EMAIL & mask) == 0) email = null;
        if ((MASK_COUNTRY & mask) == 0) country = null;
        return this;
    }

    public void save(Context c) {
        save(c, this);
    }


}
