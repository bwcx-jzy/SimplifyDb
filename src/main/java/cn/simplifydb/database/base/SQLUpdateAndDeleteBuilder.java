package cn.simplifydb.database.base;

/**
 * 删除和修改合并
 * Created by jiangzeyin on 2018/9/19.
 *
 * @author jiangzeyin
 */
public interface SQLUpdateAndDeleteBuilder {

    SQLUpdateAndDeleteBuilder from(String table);

    SQLUpdateAndDeleteBuilder from(String table, String alias);

    SQLUpdateAndDeleteBuilder limit(int rowCount);

    SQLUpdateAndDeleteBuilder limit(int rowCount, int offset);

    SQLUpdateAndDeleteBuilder where(String sql);

    SQLUpdateAndDeleteBuilder whereAnd(String sql);

    SQLUpdateAndDeleteBuilder whereOr(String sql);

    SQLUpdateAndDeleteBuilder set(String... items);

    String builder() throws Exception;
}
