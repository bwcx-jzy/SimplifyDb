package cn.jiangzeyin.util;

import cn.jiangzeyin.StringUtil;

/**
 * Created by jiangzeyin on 2017/8/14.
 */
public class Assert {

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object object) {
        notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    public static String simplifyClassName(String className) {
        String[] packages = StringUtil.stringToArray(className, ".");
        if (packages == null || packages.length < 1)
            return "";
        int len = packages.length;
        if (len == 1)
            return packages[0];
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < len - 1; i++) {
            String item = packages[i];
            name.append(item.substring(0, 1)).append(".");
        }
        name.append(packages[len - 1]);
        return name.toString();
    }
}
