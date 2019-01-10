package cn.simplifydb.test;

import cn.jiangzeyin.RandomUtil;
import cn.simplifydb.Init;
import cn.simplifydb.database.base.BaseUpdate;
import cn.simplifydb.database.run.write.Update;
import cn.simplifydb.entity.test1.Db2Test;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试修改
 *
 * @author jiangzeyin
 * @date 2018/9/18
 */
public class TestUpdateMultiple {

    @Before
    public void init() throws Exception {
        Init.init();
    }

    @Test
    public void update() {

        Update<Db2Test> update = new Update<Db2Test>() {
        };


        BaseUpdate.MultipleUpdate multipleUpdate = new BaseUpdate.MultipleUpdate();
        multipleUpdate.setKeyColumn("id");
        multipleUpdate.setKeyValue(1);
        Map<String, Object> map = new HashMap<>();
        map.put("name", "test:" + RandomUtil.getRandomString(2));
        multipleUpdate.setUpdateMap(map);

        update.addMultipleUpdate(multipleUpdate);

        multipleUpdate = new BaseUpdate.MultipleUpdate();
        multipleUpdate.setKeyColumn("id");
        multipleUpdate.setKeyValue(2);
        map = new HashMap<>();
        map.put("name", "test:" + RandomUtil.getRandomString(2));
        multipleUpdate.setUpdateMap(map);

        update.addMultipleUpdate(multipleUpdate);
        System.out.println(update.syncRun());
    }
}
