package cn.jiangzeyin.util;

/**
 * Created by jiangzeyin on 2017/2/3.
 */

/**
 * @author jiangzeyin
 * @create 2017 02 03 14:34
 */
public class EntityInfo {

    private static ConvertDatabaseName convertDatabaseName;

    public static void setConvertDatabaseName(ConvertDatabaseName convertDatabaseName) {
        EntityInfo.convertDatabaseName = convertDatabaseName;
    }

    /**
     * @param cls
     * @return
     */
    public static String getDatabaseName(Class cls) {
        Assert.notNull(cls);
        Assert.notNull(convertDatabaseName, "plase set convertDatabaseName");
        return convertDatabaseName.getDatabaseName(cls);
    }

    /**
     * @param object
     * @return
     */
    public static String getDatabaseName(Object object) {
        Assert.notNull(object);
        return getDatabaseName(object.getClass());
    }

    public interface ConvertDatabaseName {
        String getDatabaseName(Class cls);
    }
}
