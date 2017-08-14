package cn.jiangzeyin.database.run.write;

import cn.jiangzeyin.database.base.Base;
import cn.jiangzeyin.database.config.DatabaseContextHolder;
import cn.jiangzeyin.database.util.SqlUtil;
import cn.jiangzeyin.system.SystemDbLog;
import cn.jiangzeyin.system.SystemExecutorService;
import cn.jiangzeyin.util.EntityInfo;
import com.alibaba.druid.util.JdbcUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 移除数据 即更改isDelete 状态
 *
 * @author jiangzeyin
 * @date 2016-10-17
 */
public class Remove<T> extends Base<T> {


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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    /**
     *
     */
    public Remove(Type type) {
        // TODO Auto-generated constructor stub
        this.type = type;
    }

    public Remove(Type type, boolean isThrows) {
        // TODO Auto-generated constructor stub
        this.type = type;
        setThrows(isThrows);
    }

    public List<Object> getParameters() {
        if (parameters == null)
            return new ArrayList<>();
        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
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
     * @date 2016-10-21
     */
    public void run() {

        SystemExecutorService.execute(() -> {
            // TODO Auto-generated method stub
            syncRun();
        });
    }

    /**
     * @return
     * @author jiangzeyin
     * @date 2016-10-21
     */
    public int syncRun() {
        try {
            String tag = EntityInfo.getDatabaseName(getTclass());
            String sql = SqlUtil.getRemoveSql(getTclass(), type, getIds(), getWhere());
            //SystemLog.SystemLog(LogType.sql, sql);
            SystemDbLog.getInstance().info(sql);
            setRunSql(sql);
            DataSource dataSource = DatabaseContextHolder.getWriteDataSource(tag);
            return JdbcUtils.executeUpdate(dataSource, sql, getParameters());
        } catch (Exception e) {
            // TODO: handle exception
            isThrows(e);
        } finally {
            recycling();
            parameters = null;
            ids = null;
            where = null;
            runEnd();
        }
        return 0;
    }

}
