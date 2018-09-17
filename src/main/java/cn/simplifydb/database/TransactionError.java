package cn.simplifydb.database;

/**
 * 事务异常
 * Created by jiangzeyin on 2018/6/21.
 */
public class TransactionError extends RuntimeException {
    /**
     * 异常信息
     *
     * @param message 消息
     */
    public TransactionError(String message) {
        super(message);
    }
}
