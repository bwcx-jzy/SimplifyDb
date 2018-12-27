package cn.simplifydb.database;

/**
 * 事务异常
 *
 * @author jiangzeyin
 * @date 2018/6/21
 */
public class TransactionException extends RuntimeException {
    /**
     * 异常信息
     *
     * @param message 消息
     */
    public TransactionException(String message) {
        super(message);
    }
}
