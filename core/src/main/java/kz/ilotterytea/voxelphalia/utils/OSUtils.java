package kz.ilotterytea.voxelphalia.utils;

import kz.ilotterytea.voxelphalia.VoxelphaliaConstants;

public class OSUtils {
    private static final String OS = System.getProperty("os.name").toLowerCase();

    static public boolean isAndroid = System.getProperty("java.runtime.name").contains("Android");
    static public boolean isMac = !isAndroid && OS.contains("mac");
    static public boolean isWindows = !isAndroid && OS.contains("windows");
    static public boolean isLinux = !isAndroid && OS.contains("linux");
    static public boolean isIos = !isAndroid && (!(isWindows || isLinux || isMac)) || OS.startsWith("ios");

    public static boolean isMobile = isIos || isAndroid;

    public static final boolean isPC = isWindows || isMac || isLinux;

    static {
        try {
            Class.forName("com.google.gwt.core.client.GWT");
        } catch (Exception ignored) { /* IGNORED */ }

        boolean isMOEiOS = "iOS".equals(System.getProperty("moe.platform.name"));
        if (isMOEiOS || (!isAndroid && !isWindows && !isLinux && !isMac)) {
            isIos = true;
            isAndroid = false;
            isWindows = false;
            isLinux = false;
            isMac = false;
            isMobile = true;
        }
    }

    public static String getUserDataDirectory() {
        String path;

        if ((path = System.getenv("XDG_DATA_HOME")) == null) {
            path = System.getProperty("user.home");
            if (isLinux || isMac) {
                path += "/Documents";
            } else if (isWindows) {
                path += "/Saved Games";
            }
        }

        if (path == null) throw new RuntimeException("Failed to determine path to user data directory");

        return path + "/" + VoxelphaliaConstants.Metadata.APP_ID;
    }

    public static String getPicturesDirectory() {
        String path;

        if ((path = System.getenv("XDG_DATA_HOME")) == null) {
            path = System.getProperty("user.home") + "/Pictures";
        }

        return path + "/" + VoxelphaliaConstants.Metadata.APP_ID;
    }
}
