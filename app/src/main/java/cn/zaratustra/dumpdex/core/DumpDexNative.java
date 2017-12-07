package cn.zaratustra.dumpdex.core;

import java.io.File;

/**
 * Created by zaratustra on 2017/12/6.
 */

public class DumpDexNative {

    private static final String SO_PATH = "/data/data/cn.zaratustra.dumpdex/lib/libdump-dex.so";

    public static void chmodSoExecutable() {
        File file = new File(SO_PATH);
        file.setWritable(true, false);
        file.setReadable(true, false);
        file.setExecutable(true, false);
    }

    public static void loadSO() {
        System.load(SO_PATH);
    }

    public static native void dumpDexNative(String pkgName);
}
