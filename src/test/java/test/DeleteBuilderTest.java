package test;

import com.alibaba.druid.sql.builder.impl.SQLDeleteBuilderImpl;
import com.alibaba.druid.util.JdbcConstants;

/**
 * Created by jiangzeyin on 2018/9/19.
 */
public class DeleteBuilderTest {
    public static void main(String[] args) {
        SQLDeleteBuilderImpl sqlDeleteBuilder = new SQLDeleteBuilderImpl(JdbcConstants.MYSQL);
        sqlDeleteBuilder.from("ss");
        sqlDeleteBuilder.whereAnd("sss=1");
        System.out.println(sqlDeleteBuilder);
    }
}
