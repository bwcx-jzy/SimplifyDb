package cn.simplifydb.database;

import cn.jiangzeyin.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 对分页的基本数据进行一个简单的封装
 *
 * @param <T> 泛型
 * @author jiangzeyin
 */
@SuppressWarnings("unchecked")
public class Page<T> {
    /**
     * 页码，默认是第一页
     */
    private long pageNo;
    /**
     * 每页显示的记录数，默认是15
     */
    private long pageSize;
    /**
     * 总记录数
     */
    private long totalRecord;
    /**
     * 总页数
     */
    private long totalPage;
    /**
     * 对应的当前页记录
     */
    private List<T> results;
    /**
     * sql 后面where条件 需要自己设定
     */
    private String whereWord;
    /**
     * 排序字段
     */
    private String orderBy;
    private String sql;
    private List<Map<String, Object>> mapList;

    public List<Map<String, Object>> getMapList() {
        return mapList;
    }

    public Page setMapList(List<Map<String, Object>> mapList) {
        this.mapList = mapList;
        return this;
    }

    /**
     *
     */
    public Page() {
        // TODO Auto-generated constructor stub
    }

    public Page(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public Page setSql(String sql) {
        this.sql = sql;
        return this;
    }

    public String getWhereWord() {
        return whereWord;
    }

    public Page setWhereWord(String whereWord) {
        if (StringUtil.isEmpty(this.whereWord)) {
            this.whereWord = whereWord;
        } else {
            this.whereWord += " and " + whereWord;
        }
        return this;
    }

    /**
     * 设置分页条件
     *
     * @param pageNo   当前页码
     * @param pageSize 每页记录
     * @return this
     */
    public Page setPageNoAndSize(long pageNo, long pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        return this;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public Page setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public long getPageNo() {
        return pageNo;
    }

    public Page setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public long getPageSize() {
        return pageSize;
    }

    public Page setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public long getTotalRecord() {
        return totalRecord;
    }

    public Page setTotalRecord(long totalRecord) {
        this.totalRecord = totalRecord;
        // 在设置总页数的时候计算出对应的总页数，在下面的三目运算中加法拥有更高的优先级，所以最后可以不加括号。
        long totalPage = totalRecord % pageSize == 0 ? totalRecord / pageSize : totalRecord / pageSize + 1;
        this.setTotalPage(totalPage);
        return this;
    }

    // public void setTotalRecord(long totalRecord) {
    // this.totalRecord = totalRecord;
    // // 在设置总页数的时候计算出对应的总页数，在下面的三目运算中加法拥有更高的优先级，所以最后可以不加括号。
    // long totalPage = totalRecord % pageSize == 0 ? totalRecord / pageSize :
    // totalRecord / pageSize + 1;
    // this.setTotalPage(totalPage);
    // }

    public long getTotalPage() {
        return totalPage;
    }

    public Page setTotalPage(long totalPage) {
        this.totalPage = totalPage;
        return this;
    }

    public List<T> getResults() {
        return results;
    }

    public Page setResults(List<T> list) {
        this.results = list;
        return this;
    }

    public Page setResultsT(List<?> list) {
        this.results = (List<T>) list;
        return this;
    }

    public Page setDisplayPage(int start, int lenght) {
        int pageNo = 1;
        if (start >= lenght) {
            pageNo += start / lenght;
        }
        this.setPageNo(pageNo);
        this.setPageSize(lenght);
        return this;
    }

    @Override
    public String toString() {
        return "Page [pageNo=" + pageNo + ", pageSize=" + pageSize + ", results=" + results + ", totalPage=" + totalPage + ", totalRecord=" + totalRecord + "]";
    }

    public JSONObject toJSONObject(JSONArray jsonArray) {
        JSONObject data = new JSONObject();
        data.put("pageNo", getPageNo());
        data.put("pageSize", getPageSize());
        data.put("totalPage", getTotalPage());
        data.put("results", jsonArray);
        return data;
    }

    public JSONObject toJSONObject() {
        JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(getMapList()));
        return toJSONObject(jsonArray);
    }
}