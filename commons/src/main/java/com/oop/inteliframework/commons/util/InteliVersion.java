package com.oop.inteliframework.commons.util;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

public class InteliVersion {
    private static String stringVersion;
    private static int intVersion;

    public InteliVersion() {
    }

    public static boolean is(int version2) {
        return intVersion == version2;
    }

    public static boolean isAfter(int version2) {
        return intVersion > version2;
    }

    public static boolean isBefore(int version2) {
        return intVersion < version2;
    }

    public static boolean isOrAfter(int version2) {
        return intVersion == version2 || intVersion > version2;
    }

    public static String getStringVersion() {
        return stringVersion;
    }

    public static int getIntVersion() {
        return intVersion;
    }

    static {
        String fullVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        stringVersion = StringUtils.replace(fullVersion, "_", " ");
        intVersion = Integer.parseInt(StringUtils.replace(fullVersion.split("_")[1].split("_R")[0], "v", ""));
    }
}
