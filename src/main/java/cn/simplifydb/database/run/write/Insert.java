package cn.simplifydb.database.run.write;

import cn.jiangzeyin.StringUtil;
import cn.simplifydb.database.base.BaseWrite;
import cn.simplifydb.database.config.DataSourceConfig;
import cn.simplifydb.database.config.DatabaseContextHolder;
import cn.simplifydb.database.config.ModifyUser;
import cn.simplifydb.database.config.SystemColumn;
import cn.simplifydb.database.event.InsertEvent;
import cn.simplifydb.database.util.JdbcUtil;
import cn.simplifydb.database.util.SqlAndParameters;
import cn.simplifydb.database.util.SqlUtil;
import cn.simplifydb.system.DBExecutorService;
import cn.simplifydb.system.DbLog;
import cn.simplifydb.util.DbReflectUtil;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.builder.impl.SQLBuilderImpl;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 普通insert
 *
 * @author jiangzeyin
 */
public class Insert<T> extends BaseWrite<T> {
    private List<T> list;
    /**
     * 是否批量执行插入操作
     */
    private boolean batch;

    public void setBatch(boolean batch) {
        this.batch = batch;
    }

    public List<T> getList() {
        return list;
    }

    public Insert<T> setList(List<T> list) {
        if (list != null && list.size() > 0) {
            super.setData(list.get(0));
        } else {
            throw new IllegalArgumentException("list empty");
        }
        this.list = list;
        return this;
    }


    /**
     * 获取实体上的监听事件
     *
     * @return 事件接口
     */
    private InsertEvent getEvent(Object data) {
        if (data != null && InsertEvent.class.isAssignableFrom(data.getClass())) {
            return (InsertEvent) data;
        }
        return null;
    }

    public Insert(Connection connection) {
        super(null, connection);
        setThrows(true);
    }

    /**
     * @param data 数据对象
     */
    public Insert(T data) {
        // TODO Auto-generated constructor stub
        super(null, null);
        setData(data);
    }

    public Insert(List<T> list) {
        super(null, null);
        setList(list);
    }

    /**
     * 添加数据
     *
     * @param data     对象
     * @param isThrows 发生异常是否抛出
     */
    public Insert(T data, boolean isThrows) {
        super(data, null);
        setThrows(isThrows);
    }

    public Insert(List<T> list, boolean isThrows) {
        super(null, null);
        setList(list);
        setThrows(isThrows);
    }

    @Override
    public Insert<T> setData(T data) {
        super.setData(data);
        if (list == null) {
            list = new ArrayList<>();
        } else {
            list.clear();
        }
        list.add(data);
        return this;
    }

    /**
     * 异步执行添加数据操作
     *
     * @author jiangzeyin
     */
    @Override
    public void run() {
        if (transactionConnection != null) {
            throw new RuntimeException("Transaction must sync");
        }
        setAsync();
        setThrowable(new Throwable());
        DBExecutorService.execute(() -> {
            // TODO Auto-generated method stub
            Object id = syncRun();
            if (id == null) {
                DbLog.getInstance().info(getData() + "异步执行失败");
            }
        });
    }

    @Override
    public String builder() {
        return null;
    }

    /**
     * 执行添加数据操作
     *
     * @return 结果id
     * @author jiangzeyin
     */
    public Object syncRun() {
        // TODO Auto-generated method stub
        if (this.list == null || this.list.size() <= 0) {
            throw new RuntimeException("please add data");
        }
        int size = this.list.size();
        try {
            Callback callback = getCallback();
            // 添加集合（多个对象）
            if (batch) {
                return batchRun(callback);
            }
            // 挨个执行
            Object[] result = itemRun(callback);
            if (size == 1) {
                return result[1];
            }
            return result[0];
        } catch (Exception e) {
            // TODO: handle exception
            isThrows(e);
        } finally {
            runEnd();
            recycling();
        }
        return null;
    }

    @Override
    protected void recycling() {
        super.recycling();
        this.list = null;
    }

    /**
     * 批量插入
     *
     * @param callback callback
     * @return 成功的个数
     * @throws Exception e
     */
    private int batchRun(Callback callback) throws Exception {
        SqlAndParameters[] sqlAndParameters = SqlUtil.getInsertSqls(this);
        int createUser = getOptUserId();
        Class cls = getTclass();
        SQLInsertStatement sqlInsertStatement = new SQLInsertStatement();
        String tableName = SqlUtil.getTableName(null, cls);
        sqlInsertStatement.setTableName(new SQLIdentifierExpr(tableName));
        //
        List<Object> par = new ArrayList<>();
        for (int i = 0, size = sqlAndParameters.length; i < size; i++) {
            SqlAndParameters sqlAndParameter = sqlAndParameters[i];
            HashMap<String, String> map = sqlAndParameter.getSystemMap();
            List<String> columns = sqlAndParameter.getColumns();
            if (columns != null) {
                SQLInsertStatement.ValuesClause valuesClause = new SQLInsertStatement.ValuesClause();
                for (String item : columns) {
                    if (i == 0) {
                        sqlInsertStatement.addColumn(SQLParserUtils.createExprParser(item, JdbcConstants.MYSQL).expr());
                    }
                    Object val = null;
                    if (map != null) {
                        val = map.get(item);
                    }
                    addSystemColumns(val, valuesClause);
                }
                // 数据创建人
                if (createUser != -1 && ModifyUser.Create.isCreateClass(cls)) {
                    if (i == 0) {
                        sqlInsertStatement.addColumn(SQLParserUtils.createExprParser(ModifyUser.Create.getColumnUser(), JdbcConstants.MYSQL).expr());
                    }
                    valuesClause.addValue(SQLBuilderImpl.toSQLExpr(createUser, JdbcConstants.MYSQL));
                }
                int isDeleteValue = sqlAndParameter.getIsDelete();
                // 处理插入默认状态值
                if (isDeleteValue != SystemColumn.Active.NO_ACTIVE) {
                    if (i == 0) {
                        sqlInsertStatement.addColumn(SQLParserUtils.createExprParser(SystemColumn.Active.getColumn(), JdbcConstants.MYSQL).expr());
                    }
                    valuesClause.addValue(SQLBuilderImpl.toSQLExpr(isDeleteValue, JdbcConstants.MYSQL));
                }
                sqlInsertStatement.addValueCause(valuesClause);
                //
                par.addAll(sqlAndParameter.getParameters());
            }
        }
        String sql = sqlInsertStatement.toString();
        setRunSql(sql);
        DbLog.getInstance().info(getTransferLog(5) + getRunSql());
        int count;
        if (transactionConnection == null) {
            String tag = getTag();
            DataSource dataSource = DatabaseContextHolder.getWriteDataSource(tag);
            count = JdbcUtils.executeUpdate(dataSource, sql, par);
        } else {
            count = JdbcUtils.executeUpdate(transactionConnection, sql, par);
        }
        if (callback != null) {
            callback.success(count);
        }
        return count;
    }

    private void doInfo(Class class1, SQLInsertStatement sqlInsertStatement, int isDeleteValue) {
        int createUser = getOptUserId();
        SQLInsertStatement.ValuesClause valuesClause = sqlInsertStatement.getValues();
        // 获取修改数据的操作人
        if (createUser != -1 && ModifyUser.Create.isCreateClass(class1)) {
            sqlInsertStatement.addColumn(SQLParserUtils.createExprParser(ModifyUser.Create.getColumnUser(), JdbcConstants.MYSQL).expr());

            valuesClause.addValue(SQLBuilderImpl.toSQLExpr(createUser, JdbcConstants.MYSQL));
        }
        // 处理插入默认状态值
        if (isDeleteValue != SystemColumn.Active.NO_ACTIVE) {
            sqlInsertStatement.addColumn(SQLParserUtils.createExprParser(SystemColumn.Active.getColumn(), JdbcConstants.MYSQL).expr());
            valuesClause.addValue(SQLBuilderImpl.toSQLExpr(isDeleteValue, JdbcConstants.MYSQL));
        }
    }

    /**
     * 系统默认字段
     *
     * @param val          val
     * @param valuesClause values
     */
    private void addSystemColumns(Object val, SQLInsertStatement.ValuesClause valuesClause) {
        if (val == null) {
            SQLExprParser sqlExpr = SQLParserUtils.createExprParser("?", JdbcConstants.MYSQL);
            valuesClause.addValue(sqlExpr.additive());
        } else {
            SQLExprParser sqlExpr = SQLParserUtils.createExprParser(StringUtil.convertNULL(val), JdbcConstants.MYSQL);
            valuesClause.addValue(sqlExpr.additive());
        }
    }

    private String builderSql(SqlAndParameters sqlAndParameter) {
        SQLInsertStatement sqlInsertStatement = new SQLInsertStatement();
        Class cls = getTclass();
        String tableName = SqlUtil.getTableName(null, cls);
        sqlInsertStatement.setTableName(new SQLIdentifierExpr(tableName));
        //
        List<String> columns = sqlAndParameter.getColumns();
        if (columns != null) {
            SQLInsertStatement.ValuesClause valuesClause = new SQLInsertStatement.ValuesClause();
            HashMap<String, String> map = sqlAndParameter.getSystemMap();
            for (String item : columns) {
                Object val = null;
                if (map != null) {
                    val = map.get(item);
                }
                sqlInsertStatement.addColumn(SQLParserUtils.createExprParser(item, JdbcConstants.MYSQL).expr());
                addSystemColumns(val, valuesClause);
            }
            sqlInsertStatement.setValues(valuesClause);
            //
            doInfo(cls, sqlInsertStatement, sqlAndParameter.getIsDelete());
        }
        return sqlInsertStatement.toString();
    }

    /**
     * 按个执行
     *
     * @param callback 回调
     * @return 成功的个数  和 最后一次主键
     * @throws Exception e
     */
    private Object[] itemRun(Callback callback) throws Exception {
        Connection connection = null;
        T data;
        InsertEvent event = null;
        int successCount = 0;
        Object key = null;
        try {
            SqlAndParameters[] sqlAndParameters = SqlUtil.getInsertSqls(this);
            SqlAndParameters sqlAndParameter;
            String sql;
            for (int i = 0; i < sqlAndParameters.length; i++) {
                data = this.list.get(i);
                if (data == null) {
                    continue;
                }
                sqlAndParameter = sqlAndParameters[i];
                sql = builderSql(sqlAndParameter);
                setRunSql(sql);
                if (transactionConnection == null) {
                    String tag = getTag();
                    connection = DatabaseContextHolder.getWriteConnection(tag);
                } else {
                    connection = transactionConnection;
                }
                event = getEvent(data);
                if (event != null) {
                    Event.BeforeCode beforeCode = event.beforeInsert(this, data);
                    if (beforeCode == BaseWrite.Event.BeforeCode.END) {
                        if (!DataSourceConfig.isActive()) {
                            DbLog.getInstance().info("本次执行取消：" + data + " " + list);
                        }
                        // 标记取消的返回码
                        key = beforeCode.getResultCode();
                        continue;
                    }
                }
                DbLog.getInstance().info(getTransferLog(5) + getRunSql());
                key = JdbcUtil.executeInsert(connection, sql, sqlAndParameter.getParameters());
                if (key == null) {
                    key = DbReflectUtil.getFieldValue(data, SystemColumn.getDefaultKeyName());
                } else {
                    DbReflectUtil.setFieldValue(data, SystemColumn.getDefaultKeyName(), key);
                }
                if (event != null) {
                    event.completeInsert(key);
                }
                if (callback != null) {
                    //   异步回调如果  key是null 则 直接 返回实体
                    if (key == null) {
                        key = data;
                    }
                    callback.success(key);
                }
                successCount++;
            }
        } catch (Exception e) {
            if (event != null) {
                event.errorInsert(e);
            }
            throw e;
        } finally {
            // TODO: handle exception
            if (transactionConnection == null) {
                //  事物连接有事物对象管理
                JdbcUtils.close(connection);
            }
        }
        return new Object[]{successCount, key};
    }

    @Override
    public String toString() {
        return super.toString() + "Insert{" +
                "list=" + list +
                ", batch=" + batch +
                '}';
    }
}
