package cn.jiangzeyin.database;

import cn.jiangzeyin.util.Assert;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/3.
 */
public final class DbWriteService {

    private volatile static WriteInterface writeInterface;

    private DbWriteService() {
    }

    public static void setWriteInterface(WriteInterface writeInterface) {
        if (DbWriteService.writeInterface != null)
            throw new IllegalArgumentException("duplicate set");
        DbWriteService.writeInterface = writeInterface;
    }

    public static WriteInterface getInstance() {
        Assert.notNull(writeInterface, "please set writeInterface");
        return writeInterface;
    }


    /**
     * convert database name
     */
    public interface WriteInterface {
        /**
         * 根据实体class 获取对应数据源标记
         *
         * @param cls 实体class
         * @return 数据源标记
         */
        String getDatabaseName(Class cls);

        /**
         * 获取实体对应的表名
         *
         * @param class1         实体class
         * @param isIndex        是否使用索引
         * @param index          索引信息
         * @param isDatabaseName 是否需要保护数据库名
         * @return 表名
         */
        String getTableName(Class<?> class1, boolean isIndex, String index, boolean isDatabaseName);
    }
}
