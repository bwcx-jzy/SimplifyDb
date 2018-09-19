package cn.simplifydb.database.base;


import cn.simplifydb.database.config.SystemColumn;
import cn.simplifydb.system.DbLog;
import cn.simplifydb.system.SystemSessionInfo;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Set;

/**
 * 写入数据
 *
 * @author jiangzeyin
 */
@SuppressWarnings("unchecked")
public abstract class BaseWrite<T> extends Base<T> {
    /**
     * 操作人
     */
    private int optUserId;
    private Callback callback;
    private T data;
    private Throwable throwable;
    private boolean isAsync;
    /**
     * 事务的链接信息
     */
    protected Connection transactionConnection;

    public int getOptUserId() {
        return optUserId;
    }

    public void setOptUserId(int optUserId) {
        this.optUserId = optUserId;
    }

    /**
     * 设置回调事件监听
     *
     * @param callback 事件
     * @return BaseWrite
     */
    public BaseWrite setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    public Callback getCallback() {
        return callback;
    }

    private Throwable getThrowable() {
        return throwable;
    }

    protected void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    private boolean isAsync() {
        return isAsync;
    }

    protected void setAsync() {
        this.isAsync = true;
    }

    protected BaseWrite(Connection transactionConnection) {
        this.transactionConnection = transactionConnection;
    }

    protected BaseWrite() {
        setOptUserId(SystemSessionInfo.getUserId());
    }

    /**
     * 异步执行
     *
     * @author jiangzeyin
     */
    public abstract void run();

    /**
     * @param data 对应实体
     */
    public BaseWrite(T data) {
        // TODO Auto-generated constructor stub
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public BaseWrite setData(T data) {
        this.data = data;
        return this;
    }

    /**
     * @param t 异常
     * @author jiangzeyin
     */
    @Override
    public void isThrows(Throwable t) {
        // TODO Auto-generated method stub
        if (isAsync()) {
            t.addSuppressed(getThrowable());
            if (isThrows()) {
                throw new RuntimeException(t);
            } else {
                DbLog.getInstance().error("执行数据库操作", t);
            }
        } else {
            super.isThrows(t);
        }
    }

    /**
     * @author jiangzeyin
     */
    @Override
    protected void recycling() {
        // TODO Auto-generated method stub
        super.recycling();
        data = null;
        throwable = null;
        transactionConnection = null;
        optUserId = 0;
    }

    public interface Event {

        /**
         * 操作前
         */
        enum BeforeCode {
            /**
             * 继续执行
             */
            CONTINUE("继续", 0),
            /**
             * 结束执行
             */
            END("结束", -100);

            BeforeCode(String desc, int resultCode) {
                this.desc = desc;
                this.resultCode = resultCode;
            }

            private String desc;
            private int resultCode;

            public int getResultCode() {
                return resultCode;
            }

            public String getDesc() {
                return desc;
            }
        }
    }

    /**
     * 效验update 是否合法
     *
     * @param cls    cls
     * @param update map
     */
    protected static void checkUpdate(Class cls, HashMap<String, Object> update) {
        if (update != null) {
            Set<String> set = update.keySet();
            for (String item : set) {
                if (SystemColumn.notCanUpdate(item)) {
                    throw new IllegalArgumentException(item + " not update");
                }
                if (SystemColumn.isSequence(cls, item)) {
                    throw new IllegalArgumentException(item + " not update sequence");
                }
            }
        }
    }

    /**
     * 事件回调
     */
    public interface Callback {
        /**
         * success
         * 插入后能成获取主键值  则直接返回对应主键，反之返回实体
         *
         * @param key 主键
         */
        void success(Object key);
    }

}
