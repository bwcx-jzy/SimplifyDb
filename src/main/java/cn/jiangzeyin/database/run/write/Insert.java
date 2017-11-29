package cn.jiangzeyin.database.run.write;

import cn.jiangzeyin.database.EntityInfo;
import cn.jiangzeyin.database.base.WriteBase;
import cn.jiangzeyin.database.config.DatabaseContextHolder;
import cn.jiangzeyin.database.config.SystemColumn;
import cn.jiangzeyin.database.event.InsertEvent;
import cn.jiangzeyin.database.util.JdbcUtil;
import cn.jiangzeyin.database.util.SqlAndParameters;
import cn.jiangzeyin.database.util.SqlUtil;
import cn.jiangzeyin.system.SystemDbLog;
import cn.jiangzeyin.system.SystemExecutorService;
import cn.jiangzeyin.util.ref.ReflectUtil;
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

    private InsertEvent event;
    private List<T> list;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public InsertEvent getEvent() {
        if (event == null) {
            T data = getData();
            if (data != null && InsertEvent.class.isAssignableFrom(data.getClass())) {
                setEvent((InsertEvent) data);
            }
        }
        return event;
    }

    private InsertEvent getEvent(Object data) {
        if (event != null)
            return event;
        if (data != null && InsertEvent.class.isAssignableFrom(data.getClass()))
            return (InsertEvent) data;
        return null;
    }

    /**
     * 添加数据事件对象
     *
     * @param event 事件
     * @author jiangzeyin
     */
    public void setEvent(InsertEvent event) {
        this.event = event;
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
     * 添加数据 并设置添加事件对象
     *
     * @param data  对象
     * @param event 事件
     */
    public Insert(T data, InsertEvent event) {
        super(data);
        this.event = event;
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
        SystemExecutorService.execute(() -> {
            // TODO Auto-generated method stub
            long id = syncRun();
            if (id <= 0) {
                SystemDbLog.getInstance().info(getData() + "异步执行失败：" + id);
            }
        });
    }

    /**
     * 执行添加数据操作
     *
     * @return 结果id
     * @author jiangzeyin
     */
    @Override
    public long syncRun() {
        // TODO Auto-generated method stub
        try {
            // 加载事件
            getEvent();
            // 单个对象添加
            if (this.list == null && getData() != null) {
                if (event != null) {
                    int beforeCode = event.beforeI(getData());
                    if (beforeCode == InsertEvent.BeforeCode.END.getCode()) {
                        SystemDbLog.getInstance().info("本次执行取消：" + getData() + " " + list);
                        return InsertEvent.BeforeCode.END.getResultCode();
                    }
                }
                String tag = EntityInfo.getDatabaseName(getData());
                SqlAndParameters sqlAndParameters = SqlUtil.getInsertSql(getWriteBase());
                DataSource dataSource = DatabaseContextHolder.getWriteDataSource(tag);
                setRunSql(sqlAndParameters.getSql());
                SystemDbLog.getInstance().info(sqlAndParameters.getSql());
                Long id = JdbcUtil.executeInsert(dataSource, sqlAndParameters.getSql(), sqlAndParameters.getParameters());
                T data = getData();
                if (data != null) {
                    ReflectUtil.setFieldValue(data, SystemColumn.getDefaultKeyName(), id);
                }
                if (event != null)
                    event.completeI(id);
                return id;
            }
            // 添加集合（多个对象）
            if (this.list.size() > 0) {
                Connection connection = null;
                try {
                    String tag = EntityInfo.getDatabaseName(list.get(0));
                    SqlAndParameters[] sqlAndParameters = SqlUtil.getInsertSql(this);
                    connection = DatabaseContextHolder.getWriteConnection(tag);
                    setRunSql("more:" + sqlAndParameters[0].getSql());
                    for (int i = 0; i < sqlAndParameters.length; i++) {
                        T data = this.list.get(i);
                        if (data == null)
                            continue;
                        InsertEvent insertEvent = getEvent(data);
                        if (insertEvent != null) {
                            int beforeCode = insertEvent.beforeI(data);
                            if (beforeCode == InsertEvent.BeforeCode.END.getCode()) {
                                SystemDbLog.getInstance().info("本次执行取消：" + data + " " + list);
                                continue;
                            }
                        }
                        SystemDbLog.getInstance().info(sqlAndParameters[i].getSql());
                        Long id = JdbcUtil.executeInsert(connection, sqlAndParameters[i].getSql(), sqlAndParameters[i].getParameters());
                        if (id < 1)
                            return -1;
                        ReflectUtil.setFieldValue(data, SystemColumn.getDefaultKeyName(), id);
                        if (insertEvent != null)
                            insertEvent.completeI(id);
                    }
                    return 1;
                } finally {
                    // TODO: handle exception
                    JdbcUtils.close(connection);
                }
            }
            return -1;
        } catch (Exception e) {
            // TODO: handle exception
            isThrows(e);
            if (event != null)
                event.errorI(e);
        } finally {
            recycling();
            runEnd();
        }
        return 0L;
    }
}
