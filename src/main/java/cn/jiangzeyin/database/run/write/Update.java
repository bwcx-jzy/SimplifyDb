package cn.jiangzeyin.database.run.write;

import cn.jiangzeyin.database.DbWriteService;
import cn.jiangzeyin.database.base.WriteBase;
import cn.jiangzeyin.database.config.DatabaseContextHolder;
import cn.jiangzeyin.database.config.SystemColumn;
import cn.jiangzeyin.database.event.UpdateEvent;
import cn.jiangzeyin.database.util.SqlAndParameters;
import cn.jiangzeyin.database.util.SqlUtil;
import cn.jiangzeyin.system.DBExecutorService;
import cn.jiangzeyin.system.DbLog;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * update 数据库操作
 *
 * @author jiangzeyin
 */
public class Update<T> extends WriteBase<T> {

    private String where;
    private List<Object> whereParameters;
    private Object keyValue;
    private String keyColumn;
    private HashMap<String, Object> update;

    /**
     *
     */
    public Update(T data) {
        super(data);
    }

    /**
     * 事务对象
     *
     * @param connection 连接信息
     */
    Update(Connection connection) {
        super(connection);
    }

    public Update(T data, boolean isThrows) {
        super(data);
        setThrows(isThrows);
    }

    public Update() {
        super((T) null);
    }

    public Update(boolean isThrows) {
        super((T) null);
        setThrows(isThrows);
    }

    public HashMap<String, Object> getUpdate() {
        return update;
    }

    public void setUpdate(HashMap<String, Object> update) {
        this.update = update;
    }

    /**
     * 添加要更新的字段
     *
     * @param column 列名
     * @param value  值
     * @author jiangzeyin
     */
    public void putUpdate(String column, Object value) {
        // 判断对应字段是否可以被修改
        if (SystemColumn.notCanUpdate(column))
            throw new IllegalArgumentException(column + " not update");
        if (update == null)
            update = new HashMap<>();
        update.put(column, value);
    }

    public Object getKeyValue() {
        return keyValue;
    }

    /**
     * 设置查询主键值
     *
     * @param keyValue 键
     * @author jiangzeyin
     */
    public void setKeyValue(Object keyValue) {
        this.keyValue = keyValue;
    }

    public String getKeyColumn() {
        if (StringUtils.isEmpty(keyColumn))
            return SystemColumn.getDefaultKeyName();
        return keyColumn;
    }

    /**
     * 设置主键列名
     * <p>
     * 默认为 id
     *
     * @param keyColumn 列
     * @author jiangzeyin
     */
    public void setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public void AppendWhere(String where) {
        if (StringUtils.isEmpty(this.where))
            this.where = where;
        else
            this.where += " and " + where;
    }

    public List<Object> getWhereParameters() {
        return whereParameters;
    }

    public void setWhereParameters(List<Object> whereParameters) {
        this.whereParameters = whereParameters;
    }

    public void setWhereParameters(Object... whereParameters) {
        if (this.whereParameters == null)
            this.whereParameters = new LinkedList<>();
        Collections.addAll(this.whereParameters, whereParameters);
    }

    @Override
    public void run() {
        if (transactionConnection != null)
            throw new RuntimeException("Transaction must sync");
        setAsync();
        setThrowable(new Throwable());
        getAsyncLog();
        // TODO Auto-generated method stub
        DBExecutorService.execute(this::syncRun);
    }

    @Override
    public Class<?> getTclass() {
        T t = getData();
        if (t != null)
            return t.getClass();
        return super.getTclass();
    }

    /**
     * 获取实体上的监听事件
     *
     * @return 事件接口
     */
    private UpdateEvent getEvent(Object data) {
        if (data != null && UpdateEvent.class.isAssignableFrom(data.getClass()))
            return (UpdateEvent) data;
        return null;
    }

    /**
     * @return 影响行数
     * @author jiangzeyin
     */
    public long syncRun() {
        // TODO Auto-generated method stub
        UpdateEvent event = null;
        try {
            Callback callback = getCallback();
            T data = getData();
            String tag;
            if (data == null) {
                Class<?> tClass = getTclass();
                tag = DbWriteService.getDatabaseName(tClass);
                if (UpdateEvent.class.isAssignableFrom(tClass)) {
                    event = (UpdateEvent) tClass.newInstance();
                }
            } else {
                tag = DbWriteService.getDatabaseName(data);
                event = getEvent(data);
            }
            if (event != null) {
                Event.BeforeCode beforeCode = event.beforeUpdate(this, data);
                if (beforeCode == Event.BeforeCode.END) {
                    DbLog.getInstance().info("本次执行取消：" + data + "  " + getUpdate());
                    return beforeCode.getResultCode();
                }
            }
            SqlAndParameters sqlAndParameters = SqlUtil.getUpdateSql(this);
            String sql = sqlAndParameters.getSql();
            DbLog.getInstance().info(getTransferLog() + sql);
            setRunSql(sql);
            int count;
            if (transactionConnection == null) {
                DataSource dataSource = DatabaseContextHolder.getWriteDataSource(tag);
                count = JdbcUtils.executeUpdate(dataSource, sql, sqlAndParameters.getParameters());
            } else {
                count = JdbcUtils.executeUpdate(transactionConnection, sql, sqlAndParameters.getParameters());
            }
            Object keyValue = getKeyValue();
            if (event != null) {
                if (keyValue != null) {
                    event.completeUpdate(getKeyValue());
                } else {
                    event.completeUpdate(data);
                }
            }
            if (callback != null) {
                callback.success(keyValue);
            }
            return count;
        } catch (Exception e) {
            // TODO: handle exception
            isThrows(e);
            if (event != null)
                event.errorUpdate(e);
        } finally {
            runEnd();
            recycling();
            this.update = null;
            this.whereParameters = null;
        }
        return 0L;
    }
}
