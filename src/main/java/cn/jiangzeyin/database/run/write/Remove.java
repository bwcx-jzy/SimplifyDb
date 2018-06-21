package cn.jiangzeyin.database.run.write;

import cn.jiangzeyin.database.DbWriteService;
import cn.jiangzeyin.database.base.WriteBase;
import cn.jiangzeyin.database.config.DatabaseContextHolder;
import cn.jiangzeyin.database.config.SystemColumn;
import cn.jiangzeyin.database.util.SqlUtil;
import cn.jiangzeyin.system.DBExecutorService;
import cn.jiangzeyin.system.DbLog;
import com.alibaba.druid.util.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.*;

/**
 * 移除数据 即更改isDelete 状态
 *
 * @author jiangzeyin
 */
public class Remove<T> extends WriteBase<T> {


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

    private String ids;
    private String where;
    private List<Object> parameters;
    private Type type;
    private HashMap<String, Object> update;


    public Remove(Connection transactionConnection) {
        super(transactionConnection);
        setThrows(true);
    }

    public HashMap<String, Object> getUpdate() {
        return update;
    }

    public void setUpdate(HashMap<String, Object> update) {
        if (type != Type.recovery)
            throw new IllegalArgumentException("type must " + Type.recovery);
        checkUpdate(getTclass(), update);
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
        if (type != Type.recovery)
            throw new IllegalArgumentException("type must " + Type.recovery);
        // 判断对应字段是否可以被修改
        if (SystemColumn.notCanUpdate(column))
            throw new IllegalArgumentException(column + " not update");
        if (SystemColumn.isSequence(getTclass(), column))
            throw new IllegalArgumentException(column + " not update sequence");
        if (update == null)
            update = new HashMap<>();
        update.put(column, value);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    /**
     * @param type 操作类型
     */
    public Remove(Type type) {
        // TODO Auto-generated constructor stub
        this(type, false);
    }

    public Remove(Type type, boolean isThrows) {
        // TODO Auto-generated constructor stub
        this.type = type;
        setThrows(isThrows);
        if (SystemColumn.Active.NO_ACTIVE == SystemColumn.Active.getActiveValue()) {
            if (type != Type.delete)
                throw new IllegalArgumentException("please set systemColumn.active");
        }
    }

    public List<Object> getParameters() {
        if (parameters == null)
            return new ArrayList<>();
        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    public void setParameters(Object... parameters) {
        if (this.parameters == null)
            this.parameters = new LinkedList<>();
        Collections.addAll(this.parameters, parameters);
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public void setIds(int id) {
        this.ids = String.valueOf(id);
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    /**
     * @author jiangzeyin
     */
    public void run() {
        // TODO Auto-generated method stub
        if (transactionConnection != null)
            throw new RuntimeException("Transaction must sync");
        setAsync();
        getAsyncLog();
        setThrowable(new Throwable());
        DBExecutorService.execute(this::syncRun);
    }

    /**
     * @return 影响行数
     * @author jiangzeyin
     */
    public int syncRun() {
        if (type == null)
            throw new IllegalArgumentException("type null");
        try {
            WriteBase.Callback callback = getCallback();
            String tag = DbWriteService.getInstance().getDatabaseName(getTclass());
            String sql = SqlUtil.getRemoveSql(this);
            DbLog.getInstance().info(getTransferLog() + sql);
            setRunSql(sql);
            int up;
            if (transactionConnection != null) {
                up = JdbcUtils.executeUpdate(transactionConnection, sql, getParameters());
            } else {
                DataSource dataSource = DatabaseContextHolder.getWriteDataSource(tag);
                up = JdbcUtils.executeUpdate(dataSource, sql, getParameters());
            }
            if (up > 0 && callback != null) {
                callback.success(up);
            }
            return up;
        } catch (Exception e) {
            // TODO: handle exception
            isThrows(e);
        } finally {
            runEnd();
            recycling();
            parameters = null;
            ids = null;
            where = null;
        }
        return 0;
    }


}
