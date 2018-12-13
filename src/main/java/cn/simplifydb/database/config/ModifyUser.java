package cn.simplifydb.database.config;

import cn.jiangzeyin.StringUtil;
import cn.simplifydb.system.DbLog;
import com.alibaba.druid.util.StringUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * 记录最后修改人
 * Created by jiangzeyin on 2017/8/14.
 *
 * @author jiangzeyin
 */
public class ModifyUser {
    
    public static class Modify {
        private static HashSet<Class<?>> modify_class = new HashSet<>();
        private static String modifyTime;
        private static String columnUser;
        private static String columnTime;

        public static String getColumnTime() {
            return columnTime;
        }

        public static String getColumnUser() {
            return columnUser;
        }

        public static String getModifyTime() {
            return modifyTime;
        }

        public static boolean isModifyClass(Class tClass) {
            return isAssignableFrom(modify_class, tClass);
        }
    }

    private static boolean isAssignableFrom(Set<Class<?>> set, Class cls) {
        if (set == null) {
            return false;
        }
        for (Class<?> item : set) {
            if (item.isAssignableFrom(cls)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 记录数据创建者
     */
    public static class Create {
        private static HashSet<Class<?>> create_class = new HashSet<>();
        private static String columnUser;

        public static String getColumnUser() {
            return columnUser;
        }

        public static boolean isCreateClass(Class tClass) {
            return isAssignableFrom(create_class, tClass);
        }
    }

    static void initCreate(Properties properties) {
        Objects.requireNonNull(properties);
        String createClass = properties.getProperty(ConfigProperties.PROP_CREATE_CLASS);
        if (StringUtils.isEmpty(createClass)) {
            return;
        }

        String[] createClassS = StringUtil.stringToArray(createClass);
        if (createClassS == null || createClassS.length < 1) {
            DbLog.getInstance().warn(ConfigProperties.PROP_CREATE_CLASS + " is null");
        } else {
            for (String item : createClassS) {
                try {
                    Create.create_class.add(Class.forName(item));
                } catch (ClassNotFoundException e) {
                    DbLog.getInstance().error("load class", e);
                }
            }

            String columnUser = properties.getProperty(ConfigProperties.PROP_CREATE_COLUMN_USER);
            if (StringUtils.isEmpty(columnUser)) {
                DbLog.getInstance().warn(ConfigProperties.PROP_LAST_MODIFY_COLUMN_USER + " is null");
            } else {
                Create.columnUser = columnUser;
            }
        }
    }

    static void initModify(Properties properties) {
        Objects.requireNonNull(properties);
        String modifyClass = properties.getProperty(ConfigProperties.PROP_LAST_MODIFY_CLASS);
        if (!StringUtils.isEmpty(modifyClass)) {
            String[] modifyClassS = StringUtil.stringToArray(modifyClass);
            if (modifyClassS == null || modifyClassS.length < 1) {
                DbLog.getInstance().warn(ConfigProperties.PROP_LAST_MODIFY_CLASS + " is null");
            } else {
                for (String item : modifyClassS) {
                    try {
                        Modify.modify_class.add(Class.forName(item));
                    } catch (ClassNotFoundException e) {
                        DbLog.getInstance().error("load class", e);
                    }
                }

                String modifyTime = properties.getProperty(ConfigProperties.PROP_LAST_MODIFY_TIME);
                if (StringUtils.isEmpty(modifyTime)) {
                    DbLog.getInstance().warn(ConfigProperties.PROP_LAST_MODIFY_TIME + " is null");
                } else {
                    Modify.modifyTime = modifyTime;
                }

                String columnUser = properties.getProperty(ConfigProperties.PROP_LAST_MODIFY_COLUMN_USER);
                if (StringUtils.isEmpty(columnUser)) {
                    DbLog.getInstance().warn(ConfigProperties.PROP_LAST_MODIFY_COLUMN_USER + " is null");
                } else {
                    Modify.columnUser = columnUser;
                }

                String columnTime = properties.getProperty(ConfigProperties.PROP_LAST_MODIFY_COLUMN_TIME);
                if (StringUtils.isEmpty(columnTime)) {
                    DbLog.getInstance().warn(ConfigProperties.PROP_LAST_MODIFY_COLUMN_TIME + " is null");
                } else {
                    Modify.columnTime = columnTime;
                }
            }
        }
    }
}
