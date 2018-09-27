package cn.simplifydb.database.config;

import cn.jiangzeyin.StringUtil;
import cn.simplifydb.database.annotation.FieldConfig;
import cn.simplifydb.sequence.ISequence;
import cn.simplifydb.util.DbReflectUtil;
import com.alibaba.druid.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 系统字段
 *
 * @author jiangzeyin
 */
public class SystemColumn {
    /**
     * putUpdate 时sql 执行  字符串开始标记
     */
    public static String SQL_FUNCTION_VAL_PREFIX = "#{";
    /**
     * putUpdate 时sql 执行  字符串结尾标记
     */
    public static String SQL_FUNCTION_VAL_SUFFIX = "}";


    private static String pwdColumn = "";
    private static final List<String> NOT_PUT_UPDATE = new ArrayList<>();
    private static final HashMap<String, String> COLUMN_DEFAULT_VALUE = new HashMap<>();
    private static final List<String> WRITE_DEFAULT_REMOVE = new ArrayList<>();
    private static final List<String> READ_DEFAULT_REMOVE = new ArrayList<>();
    private static String defaultSelectColumns = "*";
    private static String defaultRefKeyName = "id";
    private static String defaultKeyName = "id";

    /**
     * @return default ref key name
     */
    public static String getDefaultRefKeyName() {
        return defaultRefKeyName;
    }

    /**
     * @return default key name
     */
    public static String getDefaultKeyName() {
        return defaultKeyName;
    }

    public static String getDefaultSelectColumns() {
        return defaultSelectColumns;
    }

    public static boolean isWriteRemove(String name) {
        return name != null && WRITE_DEFAULT_REMOVE.contains(name.toLowerCase());
    }

    public static boolean isReadRemove(String name) {
        return name != null && READ_DEFAULT_REMOVE.contains(name.toLowerCase());
    }


    /**
     * 获取字段的默认值
     *
     * @param name 字段名称
     * @return 默认值
     */
    public static String getDefaultValue(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        return COLUMN_DEFAULT_VALUE.get(name.toLowerCase());
    }

    /**
     * 判断是否为sequence 字段
     *
     * @param cls  cls
     * @param name 字段名称
     * @return true 需要生成主键
     */
    public static boolean isSequence(Class<?> cls, String name) {
        Field field = DbReflectUtil.getField(cls, name);
        if (field == null) {
            return false;
        }
        FieldConfig fieldConfig = field.getAnnotation(FieldConfig.class);
        if (fieldConfig == null) {
            return false;
        }
        Class<? extends ISequence> sequenceCls = fieldConfig.sequence();
        return sequenceCls != ISequence.class;
    }

    /**
     * 判断是否可以修改
     *
     * @param name 字段名称
     * @return true 可以修改
     */
    public static boolean notCanUpdate(String name) {
        return StringUtils.isEmpty(name) || NOT_PUT_UPDATE.contains(name.toLowerCase());
    }

    /**
     * 逻辑删除
     */
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

    /**
     *
     */
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

        public static boolean isStatus(Class cls) {
            if (status) {
                Field field = DbReflectUtil.getField(cls, SystemColumn.Modify.getColumn());
                if (field == null) {
                    return false;
                }
            }
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
        if (!StringUtils.isEmpty(Active.column)) {
            Active.activeValue = Integer.parseInt(properties.getProperty(ConfigProperties.PROP_SYSTEM_COLUMN_ACTIVE_VALUE));
            Active.inActiveValue = Integer.parseInt(properties.getProperty(ConfigProperties.PROP_SYSTEM_COLUMN_UN_ACTIVE));
        }
        // 系统修改时间字段
        String status = properties.getProperty(ConfigProperties.PROP_SYSTEM_COLUMN_MODIFY_STATUS);
        if (Boolean.valueOf(status)) {
            Modify.status = true;
            String userColumn = properties.getProperty(ConfigProperties.PROP_SYSTEM_COLUMN_MODIFY_COLUMN);
            if (StringUtils.isEmpty(userColumn)) {
                throw new IllegalArgumentException(ConfigProperties.PROP_SYSTEM_COLUMN_MODIFY_COLUMN + " is null");
            }
            Modify.column = userColumn;
            String userTime = properties.getProperty(ConfigProperties.PROP_SYSTEM_COLUMN_MODIFY_TIME);
            if (StringUtils.isEmpty(userTime)) {
                throw new IllegalArgumentException(ConfigProperties.PROP_SYSTEM_COLUMN_MODIFY_TIME + " is null");
            }
            Modify.time = userTime;
        }
        // 不允许修改的字段
        String notPutUpdate = properties.getProperty(ConfigProperties.PROP_SYSTEM_COLUMN_NOT_PUT_UPDATE);
        if (!StringUtils.isEmpty(notPutUpdate)) {
            String[] array = StringUtil.stringToArray(notPutUpdate.toLowerCase());
            if (array != null) {
                NOT_PUT_UPDATE.addAll(Arrays.asList(array));
            }
        }
        // 默认值
        String defaultValue = properties.getProperty(ConfigProperties.PROP_SYSTEM_COLUMN_COLUMN_DEFAULT_VALUE);
        if (!StringUtils.isEmpty(defaultValue)) {
            String[] array = StringUtil.stringToArray(defaultValue, ",");
            if (array != null) {
                for (String item : array) {
                    String[] value = item.split(":");
                    COLUMN_DEFAULT_VALUE.put(value[0].toLowerCase(), value[1]);
                }
            }
        }
        //
        String writeDef = properties.getProperty(ConfigProperties.PROP_SYSTEM_COLUMN_WRITE_DEFAULT_REMOVE);
        if (!StringUtils.isEmpty(writeDef)) {
            String[] array = StringUtil.stringToArray(writeDef.toLowerCase());
            if (array != null) {
                WRITE_DEFAULT_REMOVE.addAll(Arrays.asList(array));
            }
        }
        //
        String readDef = properties.getProperty(ConfigProperties.PROP_SYSTEM_COLUMN_READ_DEFAULT_REMOVE);
        if (!StringUtils.isEmpty(readDef)) {
            String[] array = StringUtil.stringToArray(readDef.toLowerCase());
            if (array != null) {
                READ_DEFAULT_REMOVE.addAll(Arrays.asList(array));
            }
        }
        //
        String columnsDef = properties.getProperty(ConfigProperties.PROP_SYSTEM_COLUMN_SELECT_DEFAULT_COLUMNS);
        if (!StringUtils.isEmpty(columnsDef)) {
            defaultSelectColumns = columnsDef;
        }
        // 默认外键列名称
        String refKeyDef = properties.getProperty(ConfigProperties.PROP_SYSTEM_COLUMN_DEFAULT_REF_KEY_NAME);
        if (!StringUtils.isEmpty(refKeyDef)) {
            defaultRefKeyName = refKeyDef;
        }

        // 默认主键列名称
        String keyDef = properties.getProperty(ConfigProperties.PROP_SYSTEM_COLUMN_DEFAULT_KEY_NAME);
        if (!StringUtils.isEmpty(keyDef)) {
            defaultKeyName = keyDef;
        }
    }
}
