package cn.simplifydb.test;

import cn.simplifydb.Init;
import cn.simplifydb.database.Page;
import cn.simplifydb.database.base.ReadBase;
import cn.simplifydb.database.run.read.SelectPage;
import com.alibaba.fastjson.JSONArray;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by jiangzeyin on 2018/9/18.
 */
public class TestPage {
    @Before
    public void init() throws Exception {
        Init.init();
    }


    /**
     * 手动调用实体方法
     */
    @Test
    public void page() {
        Page<cn.simplifydb.entity.test.Test> page = new Page<>();
        page.setPageNoAndSize(1, 1);
        SelectPage<cn.simplifydb.entity.test.Test> testSelect = new SelectPage<cn.simplifydb.entity.test.Test>(page) {
        };
        testSelect.setTclass(cn.simplifydb.entity.test.Test.class);
        List<cn.simplifydb.entity.test.Test> testList = testSelect.run();
        System.out.println(testList);
    }

    /**
     * 手动调用实体方法
     */
    @Test
    public void page2() {
        Page<cn.simplifydb.entity.test.Test> page = new Page<>();
        page.setPageNoAndSize(1, 2);
        SelectPage<cn.simplifydb.entity.test.Test> testSelect = new SelectPage<cn.simplifydb.entity.test.Test>(page) {
        };
        testSelect.setTclass(cn.simplifydb.entity.test.Test.class);
        testSelect.setResultType(ReadBase.Result.JsonArray);
        JSONArray jsonArray = testSelect.run();
        System.out.println(jsonArray);
        System.out.println(page);
    }


    @Test
    public void page3() {
        Page<cn.simplifydb.entity.test.Test> page = new Page<>();
        page.setPageNoAndSize(3, 2);
        page.setWhereWord("id>2");
        page.setOrderBy("id desc");
        SelectPage<cn.simplifydb.entity.test.Test> testSelect = new SelectPage<cn.simplifydb.entity.test.Test>(page) {
        };
        testSelect.setTclass(cn.simplifydb.entity.test.Test.class);
        testSelect.setResultType(ReadBase.Result.JsonArray);
        JSONArray jsonArray = testSelect.run();
        System.out.println(jsonArray);
        System.out.println(page);
    }
}
