package org.wrpg.mnes.utils;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class SystemPropertyUtil {
    public static boolean contains(String key) {
        return get(key) != null;
    }

    public static String get(String key) {
        return get(key, (String)null);
    }

    public static String get(final String key, String def) {
        checkNonEmpty(key, "key");
        String value = null;

        try {
            if (System.getSecurityManager() == null) {
                value = System.getProperty(key);
            } else {
                value = (String) AccessController.doPrivileged(new PrivilegedAction<String>() {
                    public String run() {
                        return System.getProperty(key);
                    }
                });
            }
        } catch (SecurityException var4) {
            System.err.println("Unable to retrieve a system property "+key+"; default values will be used.");
            var4.printStackTrace();
        }

        return value == null ? def : value;
    }

    public static String checkNonEmpty(String value, String name) {
        if (((String)checkNotNull(value, name)).isEmpty()) {
            throw new IllegalArgumentException("Param '" + name + "' must not be empty");
        } else {
            return value;
        }
    }

    public static <T> T checkNotNull(T arg, String text) {
        if (arg == null) {
            throw new NullPointerException(text);
        } else {
            return arg;
        }
    }

    public static boolean getBoolean(String key, boolean def) {
        String value = get(key);
        if (value == null) {
            return def;
        } else {
            value = value.trim().toLowerCase();
            if (value.isEmpty()) {
                return def;
            } else if (!"true".equals(value) && !"yes".equals(value) && !"1".equals(value)) {
                if (!"false".equals(value) && !"no".equals(value) && !"0".equals(value)) {
                    System.err.println("Unable to parse the boolean system property '"+key+"':"+value+" - using the default value: "+def);
                    return def;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
    }

    public static int getInt(String key, int def) {
        String value = get(key);
        if (value == null) {
            return def;
        } else {
            value = value.trim();

            try {
                return Integer.parseInt(value);
            } catch (Exception var4) {
                System.err.println("Unable to parse the integer system property '"+key+"':"+value+" - using the default value: "+def);
                return def;
            }
        }
    }

    public static long getLong(String key, long def) {
        String value = get(key);
        if (value == null) {
            return def;
        } else {
            value = value.trim();

            try {
                return Long.parseLong(value);
            } catch (Exception var5) {
                System.err.println("Unable to parse the long integer system property '"+key+"':"+value+" - using the default value: "+def);
                return def;
            }
        }
    }

    private SystemPropertyUtil() {
    }

}
