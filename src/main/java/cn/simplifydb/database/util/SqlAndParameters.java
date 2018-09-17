package cn.simplifydb.database.util;

import java.util.HashMap;
import java.util.List;

/**
 * sql 处理后对象
 *
 * @author jiangzeyin
 */
public class SqlAndParameters {
    private String sql;
    private List<Object> parameters;
    private List<String> columns;
    private HashMap<String, String> systemMap;

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public HashMap<String, String> getSystemMap() {
        return systemMap;
    }

    public void setSystemMap(HashMap<String, String> systemMap) {
        this.systemMap = systemMap;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(CharSequence sql) {
        this.sql = sql.toString();
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }
}
