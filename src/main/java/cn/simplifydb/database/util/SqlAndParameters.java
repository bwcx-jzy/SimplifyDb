package cn.simplifydb.database.util;

import java.util.HashMap;
import java.util.List;

/**
 * sql 处理后对象
 *
 * @author jiangzeyin
 */
public final class SqlAndParameters {

    private List<Object> parameters;
    private List<String> columns;
    private HashMap<String, String> systemMap;
    private int isDelete;

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
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


    public List<Object> getParameters() {
        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "SqlAndParameters{" +
                "parameters=" + parameters +
                ", columns=" + columns +
                ", systemMap=" + systemMap +
                ", isDelete=" + isDelete +
                '}';
    }
}
