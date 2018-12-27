package cn.simplifydb.database.run.write;

import cn.simplifydb.database.DbWriteService;
import cn.simplifydb.database.TransactionException;
import cn.simplifydb.database.config.DatabaseContextHolder;
import cn.simplifydb.database.run.TransactionLevel;
import cn.simplifydb.system.DbLog;
import com.alibaba.druid.util.JdbcUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 事务操作
 *
 * @author jiangzeyin
 */
public class Transaction {
    /**
     * 事务数据库连接
     */
    private Connection connection;
    /**
     *
     */
    private Callback callback;
    /**
     * 事物级别
     */
    private TransactionLevel transactionLevel;
    /**
     *
     */
    private String tag;
    /**
     * 是否支持事务
     */
    private Boolean isSupportTransaction = null;

    public Transaction(String tag, Callback callback, TransactionLevel transactionLevel) {
        Objects.requireNonNull(tag);
        Objects.requireNonNull(callback);
        this.tag = tag;
        this.callback = callback;
        this.transactionLevel = transactionLevel;
        init();
    }

    public Transaction(Class cls, Callback callback, TransactionLevel transactionLevel) {
        this(DbWriteService.getInstance().getDatabaseName(cls), callback, transactionLevel);
    }

    /**
     * 创建事物对象
     *
     * @param cls      操作class
     * @param callback 回调
     */
    public Transaction(Class cls, Callback callback) {
        this(cls, callback, null);
    }

    /**
     * 创建事物对象
     *
     * @param tag      数据源标记
     * @param callback 回调
     */
    public Transaction(String tag, Callback callback) {
        this(tag, callback, null);
    }

    /**
     * 初始化
     */
    private void init() {
        try {
            connection = DatabaseContextHolder.getWriteConnection(tag);
            if (connection == null) {
                throw new TransactionException("Transaction init getConnection error");
            }
            // 检查
            checkTransactionSupported(connection);
            // 设置事务级别
            if (null != transactionLevel) {
                int level = transactionLevel.getLevel();
                //用户定义的事务级别
                connection.setTransactionIsolation(level);
            }
            // 开始事物
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            callback.error(e);
            throw new TransactionException("Transaction init error:" + e.getMessage());
        }
        Operate operate = new Operate(this);
        try {
            callback.start(operate);
        } catch (Exception e) {
            rollback();
            callback.error(e);
            TransactionException transactionException = new TransactionException("Transaction error:" + e.getMessage());
            // 添加异常信息
            transactionException.addSuppressed(e);
            throw transactionException;
        }
    }

    /**
     * 检查数据库是否支持事务
     *
     * @param conn Connection
     * @throws SQLException 获取元数据信息失败
     */
    private void checkTransactionSupported(Connection conn) throws SQLException {
        if (null == isSupportTransaction) {
            isSupportTransaction = conn.getMetaData().supportsTransactions();
        }
        if (!isSupportTransaction) {
            throw new TransactionException("Transaction not supported for current database!");
        }
    }

    /**
     * 提交事务
     */
    private void commit() {
        try {
            connection.commit();
        } catch (SQLException e) {
            DbLog.getInstance().error("commit error", e);
            callback.error(e);
        } finally {
            JdbcUtils.close(connection);
        }
    }

    /**
     * 回滚
     */
    private void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            DbLog.getInstance().error("rollback error", e);
            callback.error(e);
        } finally {
            JdbcUtils.close(connection);
        }
    }

    /**
     * 事物接口
     */
    public interface Callback {
        /**
         * 事物已经准备好啦
         *
         * @param operate 可以操作的事物对象
         */
        void start(Operate operate);

        /**
         * 事物异常
         *
         * @param e 异常
         */
        void error(Exception e);
    }

    /**
     * 事物操作对象
     */
    public static class Operate {
        private Transaction transaction;

        private Operate(Transaction transaction) {
            Objects.requireNonNull(transaction);
            this.transaction = transaction;
        }

        /**
         * 获取事务的insert 操作对象
         *
         * @return insert
         */
        public <T> Insert<T> getInsert() {
            return new Insert<>(transaction.connection);
        }

        /**
         * 获取事务的insert 操作对象
         *
         * @param cls cls
         * @return insert
         */
        public <T> Insert<T> getInsert(Class<T> cls) {
            Insert<T> insert = getInsert();
            insert.setTclass(cls);
            return insert;
        }

        /**
         * 获取事务的update 操作对象
         *
         * @param cls 要操作的class
         * @return update
         */
        public <T> Update<T> getUpdate(Class<T> cls) {
            return new Update<T>(transaction.connection, cls);
        }

        /**
         * 获取事务的remove 操作对象
         *
         * @param type 操作类型
         * @return remove
         */
        public <T> Remove<T> getRemove(Remove.Type type) {
            return new Remove<>(transaction.connection, type);
        }

        /**
         * 获取事务的remove 操作对象
         *
         * @param cls  cls
         * @param type 操作类型
         * @return remove
         */
        public <T> Remove<T> getRemove(Remove.Type type, Class<T> cls) {
            Remove<T> remove = getRemove(type);
            remove.setTclass(cls);
            return remove;
        }

        /**
         * 提交事务
         */
        public void commit() {
            transaction.commit();
        }

        /**
         * 回滚事务
         */
        public void rollback() {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
}
