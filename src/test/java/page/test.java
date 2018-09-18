package page;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcConstants;

/**
 * Created by jiangzeyin on 2018/9/18.
 */
public class test {
    public static void main(String[] args) {
        String sql = PagerUtils.count("select * from dd", JdbcConstants.MYSQL);
        System.out.println(sql);
    }
}
