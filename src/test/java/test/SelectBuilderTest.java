package test;

import com.alibaba.druid.sql.builder.impl.SQLSelectBuilderImpl;
import com.alibaba.druid.util.JdbcConstants;

/**
 * Created by jiangzeyin on 2018/9/19.
 */
public class SelectBuilderTest {
    public static void main(String[] args) {
        SQLSelectBuilderImpl sqlSelectBuilder = new SQLSelectBuilderImpl(JdbcConstants.MYSQL);
        sqlSelectBuilder.from("test").orderBy("test desc").limit(1);


        System.out.println(sqlSelectBuilder);
    }
}
