package cn.simplifydb.database.run.read;

import cn.simplifydb.database.base.BaseRead;
import cn.simplifydb.database.config.DatabaseContextHolder;
import cn.simplifydb.database.util.Util;
import cn.simplifydb.system.DbLog;
import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 分页查询
 *
 * @author jiangzeyin
 */
public class SelectPage<T> extends BaseRead<T> {

    /**
     * 页码，默认是第一页
     */
    private int pageNo = 1;
    /**
     * 每页显示的记录数，默认是15
     */
    private int pageSize = 5;
    /**
     * 总记录数
     */
    private int totalRecord;
    /**
     * 总页数
     */
    private int totalPage;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public SelectPage() {

    }

    /**
     * 固定类型
     *
     * @param result result
     */
    public SelectPage(Result result) {
        setResultType(result);
    }

    /**
     * 分页基本使用
     *
     * @param pageNo   页数
     * @param pageSize 每页条数
     */
    public SelectPage(int pageNo, int pageSize) {
        // TODO Auto-generated constructor stub
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public SelectPage setPageNoAndSize(int pageNo, int pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        return this;
    }

    public SelectPage setDisplayPage(int start, int length) {
        int pageNo = 1;
        if (start >= length) {
            pageNo += start / length;
        }
        this.setPageNo(pageNo);
        this.setPageSize(length);
        return this;
    }

    private SelectPage<T> setTotalRecord(long totalRecord) {
        this.totalRecord = (int) totalRecord;
        // 在设置总页数的时候计算出对应的总页数，在下面的三目运算中加法拥有更高的优先级，所以最后可以不加括号。
        long totalPage = totalRecord % pageSize == 0 ? totalRecord / pageSize : totalRecord / pageSize + 1;
        this.setTotalPage((int) totalPage);
        return this;
    }

    public SelectPage<T> setTotalPage(int totalPage) {
        this.totalPage = totalPage;
        return this;
    }

    public int getOffset() {
        return (getPageNo() - 1) * getPageSize();
    }

    /**
     * @return 结果
     * @author jiangzeyin
     */
    @SuppressWarnings({"hiding", "unchecked"})
    @Override
    public <T> T run() {
        // TODO Auto-generated method stub
        String countSql = null;
        try {
            String tag = getTag();
            String sql = builder();
            countSql = PagerUtils.count(sql, JdbcConstants.MYSQL);
            DataSource dataSource = DatabaseContextHolder.getReadDataSource(tag);
            List<Map<String, Object>> list;
            long count = 0;
            { // 查询数据总数
                list = JdbcUtils.executeQuery(dataSource, countSql, getParameters());
                if (list.size() > 0) {
                    Map<String, Object> countMap = list.get(0);
                    if (countMap != null) {
                        Collection collection = countMap.values();
                        count = (Long) collection.toArray()[0];
                    }
                }
                setTotalRecord(count);
            }
            DbLog.getInstance().info(getTransferLog() + getRunSql());
            if (count > 0) {
                // 查询数据
                countSql = null;
                sql = PagerUtils.limit(sql, JdbcConstants.MYSQL, getOffset(), getPageSize());
                list = JdbcUtils.executeQuery(dataSource, sql, getParameters());
            } else {
                list = new ArrayList<>();
            }
            {
                if (getResultType() == Result.JsonArray) {
                    return (T) JSON.toJSON(list);
                }
                // 结果是分页数据
                if (getResultType() == Result.PageResultType) {
                    JSONObject data = new JSONObject();
                    data.put("results", list);
                    data.put("pageNo", pageNo);
                    data.put("pageSize", pageSize);
                    data.put("totalPage", totalPage);
                    data.put("totalRecord", totalRecord);
                    return (T) data;
                }
                List<?> resultList = Util.convertList(this, list);
                return (T) resultList;
            }
        } catch (Exception e) {
            // TODO: handle exception
            if (countSql != null) {
                DbLog.getInstance().info(getTransferLog() + countSql);
            }
            isThrows(e);
        } finally {
            runEnd();
            recycling();
        }
        return null;
    }
}
