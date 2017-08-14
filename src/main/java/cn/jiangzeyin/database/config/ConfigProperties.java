package cn.jiangzeyin.database.config;

/**
 * Created by jiangzeyin on 2017/8/14.
 */
public class ConfigProperties {
    // 多数据库标识
    public static final String PROP_SOURCE_TAG = "sourceTag";
    // 多数据源对应配置路径
    public static final String PROP_CONFIG_PATH = "configPath";

    public static final String PROP_SYSTEM_KEY = "systemKey";

    public static final String PROP_SYSTEM_KEY_COLUMN = "systemKeyColumn";
    // 修改
    public static final String PROP_LAST_MODIFY = "lastModify";
    // 标记修改时间表达式
    public static final String PROP_LAST_MODIFY_TIME = PROP_LAST_MODIFY + ".time";
    // 有修改者实体类
    public static final String PROP_LAST_MODIFY_CLASS = PROP_LAST_MODIFY + ".class";
    // 实体对应修改者id 字段
    public static final String PROP_LAST_MODIFY_COLUMN_USER = PROP_LAST_MODIFY + ".column.user";
    // 实体修改时间字段
    public static final String PROP_LAST_MODIFY_COLUMN_TIME = PROP_LAST_MODIFY + ".column.time";
    // 创建
    public static final String PROP_CREATE = "create";
    // 有创建者实体类
    public static final String PROP_CREATE_CLASS = PROP_CREATE + ".class";
    // 实体对应创建者id 字段
    public static final String PROP_CREATE_COLUMN_USER = PROP_CREATE + ".column.user";
}
