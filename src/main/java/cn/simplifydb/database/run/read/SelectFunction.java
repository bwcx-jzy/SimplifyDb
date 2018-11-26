package cn.simplifydb.database.run.read;

import cn.jiangzeyin.StringUtil;
import cn.simplifydb.database.base.BaseRead;
import cn.simplifydb.database.config.DatabaseContextHolder;
import cn.simplifydb.database.util.JdbcUtil;
import cn.simplifydb.database.util.SqlUtil;
import cn.simplifydb.database.util.Util;
import cn.simplifydb.system.DbLog;
import com.alibaba.druid.util.JdbcUtils;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 查询函数
 *
 * @author jiangzeyin
 */
@SuppressWarnings("unchecked")
public class SelectFunction<T> extends BaseRead<T> {

    private String name;

    public SelectFunction(String name, String tag) {
        // TODO Auto-generated constructor stub
        this.name = name;
        super.setTag(tag);
    }

    public String getName() {
        if (StringUtil.isEmpty(name)) {
            throw new IllegalArgumentException("function Empty");
        }
        return name;
    }

    public SelectFunction setName(String name) {
        this.name = name;
        return this;
    }


    @Override
    public T run() {
        try {
            DataSource dataSource = DatabaseContextHolder.getReadDataSource(getTag());
            String sql = SqlUtil.function(getName(), getParameters());
            setRunSql(sql);
            DbLog.getInstance().info(getTransferLog() + sql);
            List<Map<String, Object>> list = JdbcUtils.executeQuery(dataSource, sql, getParameters());
            if (Util.checkListMapNull(list)) {
                return null;
            }
            // 判断是否开启还原
            if (isUnescapeHtml()) {
                JdbcUtil.htmlUnescape(list);
            }
            Map<String, Object> map = list.get(0);
            Collection<Object> collection = map.values();
            return (T) collection.toArray()[0];
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
    public String toString() {
        return super.toString() + "SelectFunction{" +
                "name='" + name + '\'' +
                '}';
    }
}
