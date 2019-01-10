package cn.simplifydb.database.run.write;

import cn.simplifydb.database.DbWriteService;
import cn.simplifydb.database.TransactionException;
import cn.simplifydb.database.config.DatabaseContextHolder;
import cn.simplifydb.database.run.TransactionLevel;
import cn.simplifydb.database.run.read.Select;
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

    //------------------------------  回调模式

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

    //------------------------------  回调模式end

    //------------------------------  显式模式

    private Transaction(String tag) {
        this.tag = tag;
        Objects.requireNonNull(tag);
    }

    /**
     * 创建事物操作对象
     *
     * @param cls class
     * @return 操作对象
     * @throws SQLException 异常
     */
    public static Operate create(Class cls) throws SQLException {
        String tag = DbWriteService.getInstance().getDatabaseName(cls);
        return create(tag);
    }

    /**
     * 创建事物操作对象
     *
     * @param tag 数据库标识
     * @return 操作对象
     * @throws SQLException 异常
     */
    public static Operate create(String tag) throws SQLException {
        Transaction transaction = new Transaction(tag);
        transaction.initConnection();
        return new Operate(transaction);
    }

    /**
     * 初始化数据库链接
     *
     * @throws SQLException 异常
     */
    private void initConnection() throws SQLException {
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
    }

    /**
     * 初始化
     */
    private void init() {
        try {
            initConnection();
        } catch (SQLException e) {
            callback.error(e);
            throw new TransactionException("Transaction init error:" + e.getMessage());
        }
        Operate operate = new Operate(this);
        try {
            boolean result = callback.start(operate);
            if (result) {
                operate.commit();
            } else {
                operate.rollback();
            }
        } catch (Exception e) {
            operate.rollback();
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
         * @return 是否提交事务  true 自动提交事务  false 回滚事务
         */
        boolean start(Operate operate);

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
        private boolean close;

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
         * 获取事务的查询对象
         *
         * @param cls class
         * @param <T> 操作泛型
         * @return select
         */
        public <T> Select<T> getSelect(Class<T> cls) {
            Select<T> select = new Select<>(transaction.connection);
            select.setTclass(cls);
            return select;
        }

        /**
         * 提交事务
         */
        public void commit() {
            if (close) {
                // 防止重复提交事务,兼容低版本
                return;
            }
            transaction.commit();
            close = true;
        }

        /**
         * 回滚事务
         */
        public void rollback() {
            if (close) {
                // 防止重复提交事务,兼容低版本
                return;
            }
            transaction.rollback();
            close = true;
        }
    }
}
