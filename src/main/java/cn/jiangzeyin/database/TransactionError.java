package cn.jiangzeyin.database;

/**
 * 事务异常
 * Created by jiangzeyin on 2018/6/21.
 */
public class TransactionError extends RuntimeException {
    public TransactionError(String message) {
        super(message);
    }
}
