package cn.jiangzeyin.database.run.write;

import cn.jiangzeyin.database.DbWriteService;
import cn.jiangzeyin.database.TransactionError;
import cn.jiangzeyin.database.config.DatabaseContextHolder;
import cn.jiangzeyin.system.DbLog;
import com.alibaba.druid.util.JdbcUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 事务操作
 * Created by jiangzeyin on 2018/4/4.
 */
public class Transaction {
    /**
     * 事务数据库连接
     */
    private Connection connection;
    private Callback callback;

    public Transaction(Class cls, Callback callback) {
        this(DbWriteService.getInstance().getDatabaseName(cls), callback);
    }

    public Transaction(String tag, Callback callback) {
        Objects.requireNonNull(callback);
        this.callback = callback;
        try {
            connection = DatabaseContextHolder.getWriteConnection(tag);
            if (connection == null)
                throw new TransactionError("Transaction init getConnection error");
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            callback.error(e);
            throw new TransactionError("Transaction init error:" + e.getMessage());
        }
        Operate operate = new Operate(this);
        try {
            callback.start(operate);
        } catch (Exception e) {
            rollback();
            callback.error(e);
            throw new TransactionError("Transaction error:" + e.getMessage());
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
        void start(Operate operate);

        void error(Exception e);
    }

    /**
     * 事物操作对象
     */
    public class Operate {
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
        public Insert getInsert() {
            return new Insert(transaction.connection);
        }

        /**
         * 获取事务的update 操作对象
         *
         * @return update
         */
        public Update getUpdate() {
            return new Update(transaction.connection);
        }

        /**
         * 获取事务的remove 操作对象
         *
         * @return remove
         */
        public Remove getRemove() {
            return new Remove(transaction.connection);
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
            if (transaction != null)
                transaction.rollback();
        }
    }
}
