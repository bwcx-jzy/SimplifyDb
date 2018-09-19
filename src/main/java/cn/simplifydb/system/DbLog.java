package cn.simplifydb.system;

/**
 * 日志
 * Created by jiangzeyin on 2017/8/14.
 *
 * @author jiangzeyin
 */
public final class DbLog {
    private DbLog() {
    }

    private volatile static DbLogInterface dbLogInterface;

    /**
     * 设置日志接口
     *
     * @param dbLogInterface 接口
     */
    public static void setDbLogInterface(DbLogInterface dbLogInterface) {
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
        /**
         * info
         *
         * @param object obj
         */
        void info(Object object);

        /**
         * 错误
         *
         * @param msg msg
         * @param t   异常
         */
        void error(String msg, Throwable t);

        /**
         * 警告消息
         *
         * @param msg msg
         */
        void warn(Object msg);

        /**
         * 警告带异常
         *
         * @param msg 消息
         * @param t   异常
         */
        void warn(String msg, Throwable t);
    }
}


