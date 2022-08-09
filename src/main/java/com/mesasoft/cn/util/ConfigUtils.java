package com.mesasoft.cn.util;


import org.apache.log4j.Logger;

import java.util.Properties;

public class ConfigUtils {
    private static final Logger LOG = Logger.getLogger(ConfigUtils.class);
    private static Properties propCommon = new Properties();

    public static String getStringProperty(String key) {
            return propCommon.getProperty(key);
    }

    public static Integer getIntProperty(String key) {
            return Integer.parseInt(propCommon.getProperty(key));
    }

    public static Long getLongProperty(String key) {
        return Long.parseLong(propCommon.getProperty(key));
    }

    public static Boolean getBooleanProperty(String key) {
        return "true".equals(propCommon.getProperty(key).toLowerCase().trim());
    }

    public static String getEffectiveString(String s) {
        if (!(s == null)) {
            return s.length() == 0 ? null : s;
        } else {
            return null;
        }
    }

    static {
        try {
            propCommon.load(ConfigUtils.class.getClassLoader().getResourceAsStream("sketch.properties"));

        } catch (Exception e) {
            propCommon = null;
            LOG.error("配置加载失败");
        }
    }

}
