package org.twinone.irremote.util;

import android.content.Context;

import java.io.File;

public class SimpleCache {

    private static File getFile(Context c, String name) {
        return new File(c.getCacheDir(), name);
    }

    public static void remove(Context c, String filename) {
        FileUtils.remove(getFile(c, filename));
    }

    public static boolean isAvailable(Context c, String filename) {
        return FileUtils.exists(getFile(c, filename));
    }

    public static String read(Context c, String filename) {
        return FileUtils.read(getFile(c, filename));
    }

    public static String readWithNewLines(Context c, String filename) {
        return FileUtils.readWithNewLines(getFile(c, filename));
    }

    public static String[] readLines(Context c, String filename) {
        return FileUtils.readLines(getFile(c, filename));
    }

    public static void write(Context c, String filename, String data) {
        FileUtils.write(getFile(c, filename), data);
    }

}
