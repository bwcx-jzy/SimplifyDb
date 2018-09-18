package cn.simplifydb;

import cn.jiangzeyin.StringUtil;
import cn.simplifydb.database.DbWriteService;
import cn.simplifydb.database.config.DataSourceConfig;
import cn.simplifydb.system.DbLog;

/**
 * 初始化数据库
 * Created by jiangzeyin on 2018/9/17.
 */
public class Init {

    public static void init() throws Exception {
        // 添加日志接口
        DbLog.setDbLogInterface(new DbLog.DbLogInterface() {
            @Override
            public void info(Object object) {
                System.out.println("打印info：" + object);
            }

            @Override
            public void error(String msg, Throwable t) {
                System.out.println("打印error：" + msg);
                t.printStackTrace();
            }

            @Override
            public void warn(Object msg) {
                System.out.println("打印warn：" + msg);
            }

            @Override
            public void warn(String msg, Throwable t) {
                System.out.println("打印warn：" + msg);
                t.printStackTrace();
            }
        });
        // 初始化配置
        DataSourceConfig.init("classpath:/db.properties");

        // 添加表和实体转化接口
        DbWriteService.setWriteInterface(new DbWriteService.WriteInterface() {
            /**
             * 返回class 所在的数据库标记
             *
             * 这里可以工具包名来确定，不同库中的在不同包下，如果项目中只有一个库，固定返回就行
             *
             * 这里推荐使用给class添加注解来实现
             *
             * @param cls class
             * @return 数据库标记 和   配置文件中的sourceTag 相关
             */
            @Override
            public String getDatabaseName(Class cls) {
                // 实例项目就简写
                String[] strs = StringUtil.stringToArray(cls.getPackage().getName(), ".");
                return strs[strs.length - 1];
            }

            @Override
            public String getTableName(Class<?> class1, boolean isIndex, String index, boolean isDatabaseName) {
                return class1.getSimpleName();
            }
        });
    }

}
