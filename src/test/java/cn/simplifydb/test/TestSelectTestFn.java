package cn.simplifydb.test;

import cn.simplifydb.Init;
import cn.simplifydb.database.run.read.Select;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author jiangzeyin
 * @date 2018/9/17
 */
public class TestSelectTestFn {

    @Before
    public void init() throws Exception {
        Init.init();
    }

    /**
     * 手动调用实体方法
     */
    @Test
    public void selectEntity() {
//        MySqlLexer.DEFAULT_MYSQL_KEYWORDS.
//        SQLSelectBuilderImpl sqlSelectBuilder = new SQLSelectBuilderImpl(JdbcConstants.MYSQL);
//        sqlSelectBuilder.
        Select<cn.simplifydb.entity.test.Test> testSelect = new Select<cn.simplifydb.entity.test.Test>() {
        };
        testSelect.setColumns("DISTINCT(id)", "name");
//        testSelect.select("DISTINCT  id", "id");
        List<cn.simplifydb.entity.test.Test> testList = testSelect.run();
        System.out.println(testList);
    }

}
