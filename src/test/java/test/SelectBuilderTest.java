package test;

import com.alibaba.druid.sql.builder.impl.SQLSelectBuilderImpl;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlSelectParser;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.util.JdbcConstants;

/**
 * Created by jiangzeyin on 2018/9/19.
 */
public class SelectBuilderTest {
    public static void main(String[] args) {
        SQLSelectBuilderImpl sqlSelectBuilder = new SQLSelectBuilderImpl(JdbcConstants.MYSQL);
        sqlSelectBuilder.from("test").orderBy("test desc");

        MySqlSelectParser mySqlStatementParser = new MySqlSelectParser(sqlSelectBuilder.toString());

        System.out.println(sqlSelectBuilder);
        System.out.println(mySqlStatementParser.parseTableSource());
    }
}
