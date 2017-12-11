package cn.jiangzeyin.database;

import cn.jiangzeyin.util.Assert;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/3.
 */
public class DbWriteService {

    private volatile static WriteInterface writeInterface;

    private DbWriteService() {

    }

    public static void setWriteInterface(WriteInterface writeInterface) {
        DbWriteService.writeInterface = writeInterface;
    }


    /**
     * @param cls 类
     * @return 库名
     */
    public static String getDatabaseName(Class cls) {
        Assert.notNull(cls);
        Assert.notNull(writeInterface, "please set writeInterface");
        return writeInterface.getDatabaseName(cls);
    }

    /**
     * @param object 实体
     * @return 库名
     */
    public static String getDatabaseName(Object object) {
        Assert.notNull(object);
        return getDatabaseName(object.getClass());
    }

    /**
     * 获取表名
     *
     * @param cls            class
     * @param isIndex        是否索引
     * @param index          索引列
     * @param isDatabaseName 是否获取数据名
     * @return 表名
     */
    public static String getTableName(Class<?> cls, boolean isIndex, String index, boolean isDatabaseName) {
        Assert.notNull(cls);
        Assert.notNull(writeInterface, "please set writeInterface");
        return writeInterface.getTableName(cls, isIndex, index, isDatabaseName);
    }

    /**
     * convert database name
     */
    public interface WriteInterface {
        String getDatabaseName(Class cls);

        String getTableName(Class<?> class1, boolean isIndex, String index, boolean isDatabaseName);
    }
}
