package cn.simplifydb.database.run.write;

import cn.simplifydb.database.base.Base;
import cn.simplifydb.database.base.BaseUpdate;
import cn.simplifydb.database.base.BaseWrite;
import cn.simplifydb.database.base.SQLUpdateAndDeleteBuilder;
import cn.simplifydb.database.config.DataSourceConfig;
import cn.simplifydb.database.config.DatabaseContextHolder;
import cn.simplifydb.database.config.SystemColumn;
import cn.simplifydb.database.event.RemoveEvent;
import cn.simplifydb.database.util.SqlUtil;
import cn.simplifydb.system.DBExecutorService;
import cn.simplifydb.system.DbLog;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.builder.impl.SQLDeleteBuilderImpl;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 移除数据 即更改isDelete 状态
 *
 * @author jiangzeyin
 */
public class Remove<T> extends BaseUpdate<T> {

    private SQLDeleteBuilderImpl sqlDeleteBuilder;
    private Type type;

    public enum Type {
        /**
         * 物理删除
         */
        delete,
        /**
         * 撤销清除
         */
        recovery,
        /**
         * 清除
         */
        remove
    }

    public Type getType() {
        return type;
    }

    /**
     * 事物模式
     *
     * @param type                  操作类型
     * @param transactionConnection connection
     */
    public Remove(Connection transactionConnection, Type type) {
        // TODO Auto-generated constructor stub
        super(null, transactionConnection);
        setType(type);
        setThrows(true);
    }

    public Remove(Type type) {
        super(null, null);
        // TODO Auto-generated constructor stub
        setType(type);
    }

    public Remove(Type type, boolean isThrows) {
        // TODO Auto-generated constructor stub
        super(null, null);
        setType(type);
        setThrows(isThrows);
    }

    @Override
    protected void checkUpdate(Class cls, String columns) {
        if (type == Type.delete) {
            throw new IllegalArgumentException("type error " + Type.delete);
        }
        super.checkUpdate(cls, columns);
    }

    private void setType(Type type) {
        this.type = type;
        if (SystemColumn.Active.NO_ACTIVE == SystemColumn.Active.getActiveValue()) {
            if (type != Type.delete) {
                throw new IllegalArgumentException("please set systemColumn.active");
            }
        }
        if (type == Type.delete) {
            sqlDeleteBuilder = new SQLDeleteBuilderImpl(JdbcConstants.MYSQL);
        }
    }

    /**
     * @author jiangzeyin
     */
    @Override
    public void run() {
        // TODO Auto-generated method stub
        if (transactionConnection != null) {
            throw new RuntimeException("Remove Transaction must sync");
        }
        setAsync();
        setThrowable(new Throwable());
        DBExecutorService.execute(this::syncRun);
    }

    /**
     * @return 影响行数
     * @author jiangzeyin
     */
    public int syncRun() {
        if (type == null) {
            throw new IllegalArgumentException("type null");
        }
        RemoveEvent removeEvent = null;
        try {
            BaseWrite.Callback callback = getCallback();
            String tag = getTag();
            String sql = builder();
            Class tCls = getTclass();
            // 事件
            if (RemoveEvent.class.isAssignableFrom(tCls)) {
                removeEvent = (RemoveEvent) Base.getObject(tCls);
                Event.BeforeCode beforeCode = removeEvent.beforeRemove(this);
                if (beforeCode == Event.BeforeCode.END) {
                    if (!DataSourceConfig.isActive()) {
                        DbLog.getInstance().info("本次执行取消：" + this);
                    }
                    return beforeCode.getResultCode();
                }
            }
            DbLog.getInstance().info(getTransferLog(4) + getRunSql());
            int up;
            if (transactionConnection != null) {
                up = JdbcUtils.executeUpdate(transactionConnection, sql, getParameters());
            } else {
                DataSource dataSource = DatabaseContextHolder.getWriteDataSource(tag);
                up = JdbcUtils.executeUpdate(dataSource, sql, getParameters());
            }
            if (callback != null) {
                callback.success(up, up);
            }
            return up;
        } catch (Exception e) {
            // TODO: handle exception
            isThrows(e);
            if (removeEvent != null) {
                removeEvent.errorRemove(e);
            }
        } finally {
            runEnd();
            recycling();
        }
        return 0;
    }

    @Override
    public String builder() throws Exception {
        if (sqlDeleteBuilder == null) {
            // 逻辑删除和恢复
            int status = type == Remove.Type.remove ? SystemColumn.Active.getInActiveValue() : SystemColumn.Active.getActiveValue();
            sqlUpdateBuilder.setValue(SystemColumn.Active.getColumn(), status);
            return super.builder();
        }
        SQLDeleteStatement sqlDeleteStatement = sqlDeleteBuilder.getSQLDeleteStatement();
        if (sqlDeleteStatement == null || sqlDeleteStatement.getFrom() == null) {
            String tableName = SqlUtil.getTableName(this, getTclass());
            sqlDeleteBuilder.from(tableName);
        }
        // 检查
        securityCheck(sqlDeleteBuilder);
        String sql = sqlDeleteBuilder.toString();
        setRunSql(sql);
        return sql;
    }

    @Override
    public SQLUpdateAndDeleteBuilder from(String table) {
        if (sqlDeleteBuilder == null) {
            sqlUpdateBuilder.from(table);
        } else {
            sqlDeleteBuilder.from(table);
        }
        return this;
    }

    @Override
    public SQLUpdateAndDeleteBuilder from(String table, String alias) {
        if (sqlDeleteBuilder == null) {
            sqlUpdateBuilder.from(table, alias);
        } else {
            sqlDeleteBuilder.from(table, alias);
        }
        return this;
    }

    @Override
    public SQLUpdateAndDeleteBuilder limit(int rowCount) {
        if (sqlDeleteBuilder == null) {
            sqlUpdateBuilder.limit(rowCount);
        } else {
            sqlDeleteBuilder.limit(rowCount);
        }
        return this;
    }

    @Override
    public SQLUpdateAndDeleteBuilder limit(int rowCount, int offset) {
        if (sqlDeleteBuilder == null) {
            sqlUpdateBuilder.limit(rowCount, offset);
        } else {
            sqlDeleteBuilder.limit(rowCount, offset);
        }
        return this;
    }

    @Override
    public SQLUpdateAndDeleteBuilder where(String sql) {
        if (sqlDeleteBuilder == null) {
            sqlUpdateBuilder.where(sql);
        } else {
            sqlDeleteBuilder.where(sql);
        }
        return this;
    }

    @Override
    public SQLUpdateAndDeleteBuilder whereAnd(String sql) {
        if (sqlDeleteBuilder == null) {
            sqlUpdateBuilder.whereAnd(sql);
        } else {
            sqlDeleteBuilder.whereAnd(sql);
        }
        return this;
    }

    @Override
    public SQLUpdateAndDeleteBuilder whereOr(String sql) {
        if (sqlDeleteBuilder == null) {
            sqlUpdateBuilder.whereOr(sql);
        } else {
            sqlDeleteBuilder.whereOr(sql);
        }
        return this;
    }

    @Override
    public SQLUpdateAndDeleteBuilder set(String... items) {
        if (sqlDeleteBuilder == null) {
            sqlUpdateBuilder.set(items);
        } else {
            throw new IllegalArgumentException("not set");
        }
        return this;
    }

    @Override
    public String toString() {
        return super.toString() + "Remove{" +
                "sqlDeleteBuilder=" + sqlDeleteBuilder +
                ", type=" + type +
                '}';
    }
}
