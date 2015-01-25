package org.twinone.androidlib.util;

import android.content.Context;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SimpleCache {

    private static File getFile(Context c, String name) {
        return new File(c.getCacheDir(),
                stringToKey(name));
    }

    public static void remove(Context c, String url) {
        FileUtils.remove(getFile(c, url));
    }

    public static boolean isAvailable(Context c, String url) {
        return FileUtils.exists(getFile(c, url));
    }

    /**
     * Returns the time when this file was last modified, measured in milliseconds since January 1st, 1970, midnight. Returns 0 if the file does not exist.
     * Returns:
     * the time when this file was last modified.
     */
    public static long getLastModified(Context c, String url) {
        return getFile(c, url).lastModified();
    }

    public static String read(Context c, String url) {
        return FileUtils.read(getFile(c, url));
    }

    public static String readWithNewLines(Context c, String filename) {
        return FileUtils.readWithNewLines(getFile(c, filename));
    }

    public static void write(Context c, String url, String data) {
        FileUtils.write(getFile(c, url), data);
    }

    public static final String stringToKey(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
