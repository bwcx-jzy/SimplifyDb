package cn.simplifydb.database.run.read;

import cn.simplifydb.database.base.BaseRead;
import cn.simplifydb.database.config.DataSourceConfig;
import cn.simplifydb.database.config.DatabaseContextHolder;
import cn.simplifydb.database.util.JdbcUtil;
import cn.simplifydb.system.DbLog;
import cn.simplifydb.util.KeyLock;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.util.JdbcUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * 判断是否存在
 *
 * @author jiangzeyin
 */
@SuppressWarnings("unchecked")
public class IsExists<T> extends BaseRead<T> {
    public static final String COUNT_SUM = "countSum";

    /**
     * 表锁
     */
    private final static KeyLock<Class> LOCK = new KeyLock<>();
    /**
     * 是否使用锁
     */
    private boolean useLock = false;

    public boolean isUseLock() {
        return useLock;
    }

    public void setUseLock(boolean useLock) {
        this.useLock = useLock;
    }


    public IsExists(String keyColumn, String keyValue) {
        this(true);
        //setKeyValue(keyValue);
        setKeyColumnAndValue(keyColumn, keyValue);
    }

    public IsExists() {
        setThrows(true);
    }

    /**
     * 异常是否抛出
     *
     * @param isThrows isThrows
     */
    public IsExists(boolean isThrows) {
        setThrows(isThrows);
    }

    public IsExists(String tag) {
        setTag(tag);
    }


    @Override
    protected String builder() {
        SQLSelectQueryBlock sqlSelectQueryBlock = sqlSelectBuilder.getSQLSelect().getFirstQueryBlock();
        if (sqlSelectQueryBlock == null || sqlSelectQueryBlock.getSelectList().size() <= 0) {
            sqlSelectBuilder.selectWithAlias("count(*)", COUNT_SUM);
            limit(1);
        }
        //
        if (sqlSelectQueryBlock != null) {
            SQLLimit sqlLimit = sqlSelectQueryBlock.getLimit();
            if (sqlLimit == null) {
                limit(1);
            }
        }
        return super.builder();
    }

    private List<Map<String, Object>> doData() throws Exception {
        Class runClass = getTclass();
        if (useLock) {
            LOCK.lock(runClass);
        }
        try {
            String tag = getTag();
            String sql = builder();
            DbLog.getInstance().info(getTransferLog() + getRunSql());
            DataSource dataSource = DatabaseContextHolder.getReadDataSource(tag);
            List<Map<String, Object>> list = JdbcUtils.executeQuery(dataSource, sql, getParameters());
            // 判断是否开启还原
            if (DataSourceConfig.UNESCAPE_HTML) {
                JdbcUtil.htmlUnescape(list);
            }
            return list;
        } finally {
            LOCK.unlock(runClass);
        }
    }


    /**
     * 查询是否存在
     *
     * @return 结果
     * @author jiangzeyin
     */
    @Override
    public <T> T run() {
        try {
            List<Map<String, Object>> list = doData();
            if (list == null || list.size() < 1) {
                return null;
            }
            Map<String, Object> map = list.get(0);
            String columns = getColumns();
            if (COUNT_SUM.equals(columns)) {
                return (T) map.get(COUNT_SUM);
            }
            if (columns != null) {
                // 只有一列自动返回对应数据类型
                columns = getRealColumnName(columns);
                return (T) map.get(columns);
            }
            return (T) map;
        } catch (Exception e) {
            // TODO: handle exception
            isThrows(e);
        } finally {
            runEnd();
            recycling();
        }
        return null;
    }

    /**
     * 判断是否存在
     *
     * @return 是否存在
     * @author jiangzeyin
     */
    public boolean runBoolean() {
        try {
            List<Map<String, Object>> list = doData();
            if (list == null || list.size() < 1) {
                return false;
            }
            Map<String, Object> map = list.get(0);
            Object object = map.get(COUNT_SUM);
            if (object == null) {
                throw new RuntimeException("查询结果没有countSum");
            }
            if (object instanceof Long) {
                Long count = (Long) object;
                return count > 0L;
            }
            if (object instanceof Integer) {
                Integer count = (Integer) object;
                return count > 0;
            }
            throw new RuntimeException("查询结果类型异常" + object);
        } catch (Exception e) {
            // TODO: handle exception
            isThrows(e);
        } finally {
            runEnd();
            recycling();
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + "IsExists{" +
                "useLock=" + useLock +
                '}';
    }
}
