package cn.simplifydb.database.base;

import cn.jiangzeyin.StringUtil;
import cn.simplifydb.database.annotation.EntityConfig;
import cn.simplifydb.database.config.ModifyUser;
import cn.simplifydb.database.config.SystemColumn;
import cn.simplifydb.database.util.SqlAndParameters;
import cn.simplifydb.database.util.SqlUtil;
import cn.simplifydb.util.DbReflectUtil;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.builder.impl.SQLUpdateBuilderImpl;
import com.alibaba.druid.util.JdbcConstants;

import java.sql.Connection;
import java.util.*;

/**
 * 修改相关
 * Created by jiangzeyin on 2018/9/19.
 *
 * @author jiangzeyin
 */
public abstract class BaseUpdate<T> extends BaseWrite<T> implements SQLUpdateAndDeleteBuilder {
    protected SQLUpdateBuilderImpl sqlUpdateBuilder;
    protected String ids;
    private Object keyValue;

    private SqlAndParameters sqlAndParameters;
    private HashMap<String, Object> update = new HashMap<>();


    protected BaseUpdate(T data, Connection transactionConnection) {
        super(data, transactionConnection);
        sqlUpdateBuilder = new SQLUpdateBuilderImpl(JdbcConstants.MYSQL);
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public void setIds(int id) {
        setIds(String.valueOf(id));
    }

    public HashMap<String, Object> getUpdate() {
        return update;
    }

    @Override
    public BaseUpdate<T> setKeyValue(Object keyValue) {
        return setKeyColumnAndValue(SystemColumn.getDefaultKeyName(), keyValue);
    }

    @Override
    public BaseUpdate<T> setKeyColumnAndValue(String column, Object keyValue) {
        this.keyValue = keyValue;
        this.keyColumn = column;
        sqlUpdateBuilder.whereAnd(column + "=!keyValue");
        return this;
    }

    public BaseUpdate<T> setUpdate(HashMap<String, Object> update) {
        if (update != null) {
            Set<Map.Entry<String, Object>> set = update.entrySet();
            for (Map.Entry<String, Object> entry : set) {
                putUpdate(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    /**
     * 添加要更新的字段
     *
     * @param column 列名
     * @param value  值
     * @author jiangzeyin
     */
    public BaseUpdate<T> putUpdate(String column, Object value) {
        checkUpdate(getTclass(), column);
        String strValue = StringUtil.convertNULL(value);
        if (strValue.startsWith("#{") && strValue.endsWith("}")) {
            strValue = strValue.substring(strValue.indexOf("#{") + 2, strValue.indexOf("}"));
            sqlUpdateBuilder.set(column + "=" + strValue);
        } else {
            addParameters(value);
            sqlUpdateBuilder.set(column + "=?");
        }
        update.put(column, value);
        return this;
    }

    @Override
    public List<Object> getParameters() throws Exception {
        List<Object> newList = new LinkedList<>();
        SqlAndParameters sqlAndParameters = getSqlAndParameters();
        if (sqlAndParameters != null) {
            newList.addAll(0, sqlAndParameters.getParameters());
        }
        List<Object> parameters = super.getParameters();
        if (parameters != null) {
            newList.addAll(parameters);
        }
        return newList;
    }


    /**
     * sql 记录操作人和时间
     */
    protected void loadModifyUser() {
        int optUserId = getOptUserId();
        if (optUserId < 1) {
            return;
        }
        Class cls = getTclass();
        if (ModifyUser.Modify.isModifyClass(cls)) {
            sqlUpdateBuilder.set(ModifyUser.Modify.getColumnUser() + "=" + optUserId);
            sqlUpdateBuilder.set(ModifyUser.Modify.getColumnTime() + "=" + ModifyUser.Modify.getModifyTime());
        }
    }

    protected void loadModify() {
        Class cls = getTclass();
        EntityConfig entityConfig = (EntityConfig) cls.getAnnotation(EntityConfig.class);
        boolean isLogUpdate = true;
        if (entityConfig != null && !entityConfig.update()) {
            isLogUpdate = false;
        }
        if (isLogUpdate && SystemColumn.Modify.isStatus(cls)) {
            sqlUpdateBuilder.set(SystemColumn.Modify.getColumn() + "=" + SystemColumn.Modify.getTime());
        }
    }

    private SqlAndParameters getSqlAndParameters() throws Exception {
        if (sqlAndParameters == null && data != null) {
            sqlAndParameters = SqlUtil.getWriteSql(this, data);
        }
        return sqlAndParameters;
    }

    @Override
    protected void recycling() {
        super.recycling();
        ids = null;
        sqlUpdateBuilder = null;
        sqlAndParameters = null;
        keyValue = null;
        keyColumn = null;
        update = null;
    }

    @Override
    public String builder() throws Exception {
        SqlAndParameters sqlAndParameters = getSqlAndParameters();
        if (sqlAndParameters != null) {
            List<String> columns = sqlAndParameters.getColumns();
            if (columns != null) {
                HashMap<String, String> map = sqlAndParameters.getSystemMap();
                for (String item : columns) {
                    Object val = null;
                    if (map != null) {
                        val = map.get(item);
                    }
                    if (val == null) {
                        sqlUpdateBuilder.set(item + "=?");
                    } else {
                        sqlUpdateBuilder.setValue(item, val);
                    }
                }
            }
            Object objId = DbReflectUtil.getFieldValue(data, SystemColumn.getDefaultKeyName());
            //   防止整表更新
            Objects.requireNonNull(objId, "没有找到任何更新条件");
            sqlUpdateBuilder.whereAnd(SystemColumn.getDefaultKeyName() + "='" + objId.toString() + "'");
        } else {
            if (ids != null) {
                sqlUpdateBuilder.whereAnd(SystemColumn.getDefaultKeyName() + " in(" + ids + ")");
            }
        }
        loadModify();
        loadModifyUser();
        SQLUpdateStatement sqlUpdateStatement = sqlUpdateBuilder.getSQLUpdateStatement();
        if (sqlUpdateStatement == null || sqlUpdateStatement.getFrom() == null) {
            String tableName = SqlUtil.getTableName(this, getTclass());
            sqlUpdateBuilder.from(tableName);
        }
        String sql = sqlUpdateBuilder.toString();
        if (keyValue != null) {
            sql = sql.replaceAll("!keyValue", "'" + keyValue.toString() + "'");
        }
        setRunSql(sql);
        return sql;
    }

    @Override
    public SQLUpdateAndDeleteBuilder from(String table) {
        sqlUpdateBuilder.from(table);
        return this;
    }

    @Override
    public SQLUpdateAndDeleteBuilder from(String table, String alias) {
        sqlUpdateBuilder.from(table, alias);
        return this;
    }

    @Override
    public SQLUpdateAndDeleteBuilder limit(int rowCount) {
        sqlUpdateBuilder.limit(rowCount);
        return this;
    }

    @Override
    public SQLUpdateAndDeleteBuilder limit(int rowCount, int offset) {
        sqlUpdateBuilder.limit(rowCount, offset);
        return this;
    }

    @Override
    public SQLUpdateAndDeleteBuilder where(String sql) {
        sqlUpdateBuilder.where(sql);
        return this;
    }

    @Override
    public SQLUpdateAndDeleteBuilder whereAnd(String sql) {
        sqlUpdateBuilder.whereAnd(sql);
        return this;
    }

    @Override
    public SQLUpdateAndDeleteBuilder whereOr(String sql) {
        sqlUpdateBuilder.whereOr(sql);
        return this;
    }

    @Override
    public SQLUpdateAndDeleteBuilder set(String... items) {
        sqlUpdateBuilder.set(items);
        return this;
    }
}
