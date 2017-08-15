package cn.jiangzeyin.database.config;

import cn.jiangzeyin.util.StringUtil;
import com.alibaba.druid.util.StringUtils;

import java.util.Properties;

/**
 * Created by jiangzeyin on 2017/8/15.
 */
public class SystemColumn {
    private static String pwdColumn = "";

    public static class Active {
        public static final int NO_ACTIVE = -100;
        private static String column = "";
        private static int activeValue = NO_ACTIVE;
        private static int inActiveValue;

        public static int getInActiveValue() {
            return inActiveValue;
        }

        public static String getColumn() {
            return column;
        }

        public static int getActiveValue() {
            return activeValue;
        }
    }

    public static class Modify {
        private static boolean status = false;
        private static String column;
        private static String time;

        public static String getColumn() {
            return column;
        }

        public static String getTime() {
            return time;
        }

        public static boolean isStatus() {
            return status;
        }
    }

    public static String getPwdColumn() {
        return pwdColumn;
    }

    static void init(Properties properties) {
        // 密码字段
        String tempPwdColumn = properties.getProperty(ConfigProperties.PROP_SYSTEM_COLUMN_PWD);
        pwdColumn = StringUtil.convertNULL(tempPwdColumn);
        // 状态字段
        String column = properties.getProperty(ConfigProperties.PROP_SYSTEM_COLUMN_ACTIVE);
        Active.column = StringUtil.convertNULL(column);
        if (StringUtils.isEmpty(Active.column)) {
            Active.activeValue = Integer.parseInt(properties.getProperty(ConfigProperties.PROP_SYSTEM_COLUMN_ACTIVE_VALUE));
            Active.inActiveValue = Integer.parseInt(properties.getProperty(ConfigProperties.PROP_SYSTEM_COLUMN_UN_ACTIVE));
        }
        // 系统修改时间字段
        String status = properties.getProperty(ConfigProperties.PROP_SYSTEM_COLUMN_MODIFY_STATUS);
        if (Boolean.valueOf(status)) {
            Modify.status = true;
            String column_ = properties.getProperty(ConfigProperties.PROP_SYSTEM_COLUMN_MODIFY_COLUMN);
            if (StringUtils.isEmpty(column_))
                throw new IllegalArgumentException(ConfigProperties.PROP_SYSTEM_COLUMN_MODIFY_COLUMN + " is null");
            Modify.column = column_;
            String time_ = properties.getProperty(ConfigProperties.PROP_SYSTEM_COLUMN_MODIFY_TIME);
            if (StringUtils.isEmpty(time_))
                throw new IllegalArgumentException(ConfigProperties.PROP_SYSTEM_COLUMN_MODIFY_TIME + " is null");
            Modify.time = time_;
        }
    }
}
