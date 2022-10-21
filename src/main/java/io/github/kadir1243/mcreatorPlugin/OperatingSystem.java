package io.github.kadir1243.mcreatorPlugin;

import java.util.Locale;

public enum OperatingSystem {
    WINDOWS("Windows", 32),
    WINDOWS64("Windows", 64),
    LINUX("Linux", 32),
    LINUX64("Linux", 64),
    MACOS("Macos", 64),
    UNKNOWN("", 0);

    private final String osName;
    private final int arch;

    OperatingSystem(String osName, int arch) {
        this.osName = osName;
        this.arch = arch;
    }

    public String getOsName() {
        return osName;
    }

    public int getArch() {
        return arch;
    }

    public static final OperatingSystem CURRENT = getCurrent();

    private static OperatingSystem getCurrent() {
        String defaultOsName = System.getProperty("os.name");
        String defaultArch = System.getProperty("os.arch");
        if (defaultOsName == null || defaultArch == null) {
            return UNKNOWN;
        }
        String s = defaultOsName.toLowerCase(Locale.ENGLISH).trim();
        if (s.startsWith("win")) {
            if (defaultArch.equals("amd64")) {
                return WINDOWS64;
            }
            return WINDOWS;
        }
        if (s.contains("linux")) {
            if (defaultArch.contains("64")) {
                return LINUX64;
            }
            return LINUX;
        }
        if (s.contains("mac"))
            return MACOS;
        return UNKNOWN;
    }
}
