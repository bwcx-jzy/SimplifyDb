package test;

import com.alibaba.druid.sql.builder.impl.SQLSelectBuilderImpl;
import com.alibaba.druid.util.JdbcConstants;

/**
 * Created by jiangzeyin on 2018/9/19.
 */
public class SelectBuilderTest {
    public static void main(String[] args) {
        SQLSelectBuilderImpl sqlSelectBuilder = new SQLSelectBuilderImpl(JdbcConstants.MYSQL);
        sqlSelectBuilder.orderBy("sss desc").whereAnd("sss").whereOr("sss").selectWithAlias("a", "aaa");
        System.out.println(sqlSelectBuilder.getSQLSelect().getFirstQueryBlock().getSelectList());
        System.out.println(sqlSelectBuilder.getSQLSelect().getFirstQueryBlock().getFrom());
//        sqlSelectBuilder.limit(1);

        System.out.println(sqlSelectBuilder.getSQLSelect().getFirstQueryBlock().getLimit().getRowCount());
        System.out.println(sqlSelectBuilder.toString());
    }
}
