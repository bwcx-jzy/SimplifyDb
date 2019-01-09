package cn.simplifydb.util;

import cn.jiangzeyin.StringUtil;

import java.util.List;
import java.util.Map;

/**
 * 工具类
 *
 * @author jiangzeyin
 */
public class Util {

    public static boolean checkListMapNull(List<Map<String, Object>> list) {
        if (list.size() < 1) {
            return true;
        }
        Map<String, Object> map = list.get(0);
        if (map == null) {
            return true;
        }
        return map.size() <= 0;
    }

    /**
     * 还原html 实体
     *
     * @param htmlStr 字符串
     * @return 还原后的
     */
    public static String unescape(String htmlStr) {
        if (StringUtil.isEmpty(htmlStr)) {
            return htmlStr;
        }
        return htmlStr.replace("&apos;", "'")
                .replace("&#039;", "'")
                .replace("&#39;", "'")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&amp;", "&")
                .replace("&nbsp;", " ");
    }

    public static String getStackTraceLine(int line) {
//        int len = sync ? 5 : 4;
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[line];
        return String.format("[%s-%s-%s]", StringUtil.simplifyClassName(stackTraceElement.getClassName()), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }
}
