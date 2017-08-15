package cn.jiangzeyin.util;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/3.
 */
public class EntityInfo {

    private static ConvertDatabaseName convertDatabaseName;

    public static void setConvertDatabaseName(ConvertDatabaseName convertDatabaseName) {
        EntityInfo.convertDatabaseName = convertDatabaseName;
    }

    /**
     * @param cls 类
     * @return 库名
     */
    public static String getDatabaseName(Class cls) {
        Assert.notNull(cls);
        Assert.notNull(convertDatabaseName, "plase set convertDatabaseName");
        return convertDatabaseName.getDatabaseName(cls);
    }

    /**
     * @param object 实体
     * @return 库名
     */
    public static String getDatabaseName(Object object) {
        Assert.notNull(object);
        return getDatabaseName(object.getClass());
    }

    public interface ConvertDatabaseName {
        String getDatabaseName(Class cls);
    }
}
