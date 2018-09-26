package cn.simplifydb.database.run.write;

import cn.simplifydb.database.base.BaseUpdate;
import cn.simplifydb.database.config.DatabaseContextHolder;
import cn.simplifydb.database.event.UpdateEvent;
import cn.simplifydb.system.DBExecutorService;
import cn.simplifydb.system.DbLog;
import com.alibaba.druid.util.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * update 数据库操作
 *
 * @author jiangzeyin
 */
public class Update<T> extends BaseUpdate<T> {

    /**
     * @param data 数据对象
     */
    public Update(T data) {
        super(data, null);
    }

    /**
     * 事务对象
     *
     * @param connection 连接信息
     */
    Update(Connection connection) {
        super(null, connection);
        setThrows(true);
    }

    public Update(T data, boolean isThrows) {
        super(data, null);
        setThrows(isThrows);
    }

    public Update() {
        super((T) null, null);
    }

    public Update(boolean isThrows) {
        super((T) null, null);
        setThrows(isThrows);
    }


    @Override
    public void run() {
        if (transactionConnection != null) {
            throw new RuntimeException("Transaction must sync");
        }
        setAsync();
        setThrowable(new Throwable());
        getAsyncLog();
        // TODO Auto-generated method stub
        DBExecutorService.execute(this::syncRun);
    }

    /**
     * 获取实体上的监听事件
     *
     * @return 事件接口
     */
    private UpdateEvent getEvent(Object data) {
        if (data != null && UpdateEvent.class.isAssignableFrom(data.getClass())) {
            return (UpdateEvent) data;
        }
        Class tCls = getTclass();
        if (UpdateEvent.class.isAssignableFrom(tCls)) {
            try {
                return (UpdateEvent) tCls.newInstance();
            } catch (InstantiationException | IllegalAccessException ignored) {
            }
        }
        return null;
    }

    /**
     * @return 影响行数
     * @author jiangzeyin
     */
    public int syncRun() {
        // TODO Auto-generated method stub
        UpdateEvent event = null;
        try {
            Callback callback = getCallback();
            T data = getData();
            String tag = getTag();
            event = getEvent(data);
            if (event != null) {
                Event.BeforeCode beforeCode = event.beforeUpdate(this, data);
                if (beforeCode == Event.BeforeCode.END) {
                    DbLog.getInstance().info("本次执行取消：" + data);
                    return beforeCode.getResultCode();
                }
            }
            String sql = builder();
            DbLog.getInstance().info(getTransferLog() + getRunSql());
            int count;
            if (transactionConnection == null) {
                DataSource dataSource = DatabaseContextHolder.getWriteDataSource(tag);
                count = JdbcUtils.executeUpdate(dataSource, sql, getParameters());
            } else {
                count = JdbcUtils.executeUpdate(transactionConnection, sql, getParameters());
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
            if (event != null) {
                event.errorUpdate(e);
            }
        } finally {
            runEnd();
            recycling();

        }
        return -1;
    }
}