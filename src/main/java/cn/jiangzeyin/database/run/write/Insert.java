package cn.jiangzeyin.database.run.write;

import cn.jiangzeyin.database.DbWriteService;
import cn.jiangzeyin.database.base.WriteBase;
import cn.jiangzeyin.database.config.DatabaseContextHolder;
import cn.jiangzeyin.database.config.SystemColumn;
import cn.jiangzeyin.database.event.InsertEvent;
import cn.jiangzeyin.database.util.JdbcUtil;
import cn.jiangzeyin.database.util.SqlAndParameters;
import cn.jiangzeyin.database.util.SqlUtil;
import cn.jiangzeyin.system.DBExecutorService;
import cn.jiangzeyin.system.DbLog;
import cn.jiangzeyin.util.DbReflectUtil;
import com.alibaba.druid.util.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

/**
 * 普通insert
 *
 * @author jiangzeyin
 */
public class Insert<T> extends WriteBase<T> {

    private List<T> list;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }


    /**
     * 获取实体上的监听事件
     *
     * @return 事件接口
     */
    private InsertEvent getEvent(Object data) {
        if (data != null && InsertEvent.class.isAssignableFrom(data.getClass()))
            return (InsertEvent) data;
        return null;
    }

    /**
     *
     */
    public Insert(T data) {
        // TODO Auto-generated constructor stub
        super(data);
    }

    public Insert(List<T> list) {
        super(null);
        this.list = list;
    }

    /**
     * 添加数据
     *
     * @param data     对象
     * @param isThrows 发生异常是否抛出
     */
    public Insert(T data, boolean isThrows) {
        super(data);
        setThrows(isThrows);
    }

    public Insert(List<T> list, boolean isThrows) {
        super(null);
        this.list = list;
        setThrows(isThrows);
    }

    /**
     * 异步执行添加数据操作
     *
     * @author jiangzeyin
     */
    @Override
    public void run() {
        setAsync(true);
        setThrowable(new Throwable());
        getAsyncLog();
        DBExecutorService.execute(() -> {
            // TODO Auto-generated method stub
            Object id = syncRun();
            if (id == null) {
                DbLog.getInstance().info(getData() + "异步执行失败");
            }
        });
    }

    /**
     * 执行添加数据操作
     *
     * @return 结果id
     * @author jiangzeyin
     */
    public Object syncRun() {
        // TODO Auto-generated method stub
        InsertEvent event = null;
        try {
            Callback callback = getCallback();
            // 单个对象添加
            T data = getData();
            if (data != null) {
                // 加载事件
                event = getEvent(data);
                if (event != null) {
                    Event.BeforeCode beforeCode = event.beforeInsert(this, data);
                    if (beforeCode == Event.BeforeCode.END) {
                        DbLog.getInstance().info("本次执行取消：" + data);
                        return beforeCode.getResultCode();
                    }
                }
                String tag = DbWriteService.getDatabaseName(data);
                SqlAndParameters sqlAndParameters = SqlUtil.getInsertSql(this);
                DataSource dataSource = DatabaseContextHolder.getWriteDataSource(tag);
                setRunSql(sqlAndParameters.getSql());
                DbLog.getInstance().info(getTransferLog() + sqlAndParameters.getSql());
                Object key = JdbcUtil.executeInsert(dataSource, sqlAndParameters.getSql(), sqlAndParameters.getParameters());
                //T data = getData();
                DbReflectUtil.setFieldValue(data, SystemColumn.getDefaultKeyName(), key);
                // 实体事件
                if (event != null)
                    event.completeInsert(key);
                //  util
                if (callback != null) {
                    callback.success(key);
                }
                return key;
            }
            // 添加集合（多个对象）
            if (this.list != null && this.list.size() > 0) {
                Connection connection = null;
                try {
                    String tag = DbWriteService.getDatabaseName(list.get(0));
                    SqlAndParameters[] sqlAndParameters = SqlUtil.getInsertSqls(this);
                    connection = DatabaseContextHolder.getWriteConnection(tag);
                    setRunSql("more:" + sqlAndParameters[0].getSql());
                    for (int i = 0; i < sqlAndParameters.length; i++) {
                        data = this.list.get(i);
                        if (data == null)
                            continue;
                        event = getEvent(data);
                        if (event != null) {
                            Event.BeforeCode beforeCode = event.beforeInsert(this, data);
                            if (beforeCode == InsertEvent.BeforeCode.END) {
                                DbLog.getInstance().info("本次执行取消：" + data + " " + list);
                                continue;
                            }
                        }
                        DbLog.getInstance().info(sqlAndParameters[i].getSql());
                        Object key = JdbcUtil.executeInsert(connection, sqlAndParameters[i].getSql(), sqlAndParameters[i].getParameters());
                        if (key == null)
                            return null;
                        DbReflectUtil.setFieldValue(data, SystemColumn.getDefaultKeyName(), key);
                        if (event != null)
                            event.completeInsert(key);
                        if (callback != null) {
                            callback.success(key);
                        }
                    }
                    return 1;
                } finally {
                    // TODO: handle exception
                    JdbcUtils.close(connection);
                }
            }
            throw new RuntimeException("please add data");
        } catch (Exception e) {
            // TODO: handle exception
            isThrows(e);
            if (event != null)
                event.errorInsert(e);
        } finally {
            runEnd();
            recycling();
        }
        return null;
    }
}
