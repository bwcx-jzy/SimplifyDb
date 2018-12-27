package cn.simplifydb.database.base;


import cn.jiangzeyin.StringUtil;
import cn.simplifydb.database.config.SystemColumn;
import cn.simplifydb.system.DbLog;
import cn.simplifydb.system.SystemSessionInfo;

import java.sql.Connection;

/**
 * 写入数据
 *
 * @author jiangzeyin
 */
public abstract class BaseWrite<T> extends Base<T> {
    /**
     * 操作人
     */
    private int optUserId;
    private Callback callback;
    protected T data;
    private Throwable throwable;
    private boolean isAsync;
    /**
     * before 事件结束消息
     */
    private String eventMsg;
    /**
     * 事务的链接信息
     */
    protected Connection transactionConnection;

    public int getOptUserId() {
        return optUserId;
    }

    public BaseWrite<T> setOptUserId(int optUserId) {
        this.optUserId = optUserId;
        return this;
    }

    /**
     * 设置回调事件监听
     *
     * @param callback 事件
     * @return BaseWrite
     */
    public BaseWrite<T> setCallback(Callback callback) {
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
        // 自动获取代码行号
        getAsyncLog();
    }

    protected BaseWrite(T data, Connection transactionConnection) {
        this.transactionConnection = transactionConnection;
        setData(data);
        setOptUserId(SystemSessionInfo.getUserId());
    }

    /**
     * 异步执行
     *
     * @author jiangzeyin
     */
    public abstract void run();

    public T getData() {
        return data;
    }

    /**
     * 设置要修改的实体
     *
     * @param data data
     * @return this
     */
    public BaseWrite<T> setData(T data) {
        this.data = data;
        return this;
    }

    @Override
    public Class<T> getTclass() {
        T t = getData();
        if (t != null) {
            return (Class<T>) t.getClass();
        }
        return super.getTclass();
    }

    @Override
    public BaseWrite<T> setKeyValue(Object keyValue) {
        throw new IllegalArgumentException("error");
    }

    @Override
    public BaseWrite<T> setKeyColumnAndValue(String column, Object keyValue) {
        throw new IllegalArgumentException("error");
    }


    /**
     * @param t 异常
     * @author jiangzeyin
     */
    @Override
    protected void isThrows(Throwable t) {
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
        this.data = null;
        this.throwable = null;
        this.transactionConnection = null;
        this.optUserId = 0;
        this.callback = null;
//        this.eventMsg = null;
    }

    public String getEventMsg() {
        if (StringUtil.isEmpty(this.eventMsg)) {
            return "Before END";
        }
        return this.eventMsg;
    }

    /**
     * 添加事件结束原因
     *
     * @param eventMsg 事件不再执行原因
     */
    public void setEventMsg(String eventMsg) {
        this.eventMsg = eventMsg;
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
             * 请调用事件消息方法
             *
             * @see BaseWrite#setEventMsg(java.lang.String)
             */
            END("结束", -100);

            BeforeCode(String desc, int resultCode) {
                this.desc = desc;
                this.resultCode = resultCode;
            }

            private String desc;
            private int resultCode;

            /**
             * 返回的状态码
             *
             * @return int
             */
            public int getResultCode() {
                return resultCode;
            }

            /**
             * 描述
             *
             * @return desc
             */
            public String getDesc() {
                return desc;
            }
        }
    }

    /**
     * 效验update 是否合法
     *
     * @param cls     cls
     * @param columns columns
     */
    protected void checkUpdate(Class cls, String columns) {
        if (SystemColumn.notCanUpdate(columns)) {
            throw new IllegalArgumentException(columns + " not update");
        }
        if (SystemColumn.isSequence(cls, columns)) {
            throw new IllegalArgumentException(columns + " not update sequence");
        }
    }

    @Override
    public String toString() {
        return super.toString() + "BaseWrite{" +
                "optUserId=" + optUserId +
                ", callback=" + callback +
                ", data=" + data +
                ", throwable=" + throwable +
                ", isAsync=" + isAsync +
                ", eventMsg='" + eventMsg + '\'' +
                ", transactionConnection=" + transactionConnection +
                '}';
    }

    /**
     * 事件回调
     */
    public interface Callback {
        /**
         * success
         * 插入后能成获取主键值  则直接返回对应主键，反之返回实体
         * <p>
         * remove 和 insert 并行执行 时  key 和count 值一样
         * <p>
         * insert 挨个 执行 count 始终为1
         *
         * @param key   主键
         * @param count 影响行数
         */
        void success(Object key, int count);
    }
}
