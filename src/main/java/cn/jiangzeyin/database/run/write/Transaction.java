package cn.jiangzeyin.database.run.write;

import cn.jiangzeyin.database.config.DatabaseContextHolder;
import cn.jiangzeyin.system.DbLog;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务操作
 * Created by jiangzeyin on 2018/4/4.
 */
public class Transaction {

    private Connection connection;

    public Transaction(String tag, Callback callback) throws SQLException {
        connection = DatabaseContextHolder.getWriteConnection(tag);
        connection.setAutoCommit(false);
        Operate operate = new Operate(this);
        callback.start(operate);
    }

    /**
     * 提交事务
     */
    private void commit() {
        try {
            connection.commit();
        } catch (SQLException e) {
            DbLog.getInstance().error("commit", e);
            try {
                connection.rollback();
            } catch (SQLException e1) {
                DbLog.getInstance().error("rollback", e1);
            }
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                DbLog.getInstance().error("finally close", e);
            }
        }
    }

    /**
     * 回滚
     */
    private void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            DbLog.getInstance().error("rollback", e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                DbLog.getInstance().error("finally close", e);
            }
        }
    }

    public interface Callback {
        void start(Operate operate);
    }

    public class Operate {
        private Transaction transaction;

        Operate(Transaction transaction) {
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
            transaction.rollback();
        }
    }
}
