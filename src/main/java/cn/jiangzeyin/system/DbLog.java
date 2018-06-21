package cn.jiangzeyin.system;

/**
 * 日志
 * Created by jiangzeyin on 2017/8/14.
 */
public final class DbLog {
    private DbLog() {
    }

    private volatile static DbLogInterface dbLogInterface;

    public static void setDbLogInterface(DbLogInterface dbLogInterface) {
        if (DbLog.dbLogInterface != null)
            throw new IllegalArgumentException("duplicate set");
        DbLog.dbLogInterface = dbLogInterface;
    }

    public static DbLogInterface getInstance() {
        if (dbLogInterface == null) {
            System.err.println("please set dbLogInterface");
            throw new IllegalArgumentException("please set dbLogInterface");
        }
        return dbLogInterface;
    }

    /**
     * 日志回调接口
     */
    public interface DbLogInterface {
        void info(Object object);

        void error(String msg, Throwable t);

        void warn(Object msg);

        void warn(String msg, Throwable t);
    }
}


