package cn.simplifydb.test;

import cn.simplifydb.Init;
import cn.simplifydb.database.run.write.Update;
import cn.simplifydb.entity.test1.TestUpdateTime;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by jiangzeyin on 2018/9/20.
 */
public class TestUpdateTimeFn {
    
    @Before
    public void init() throws Exception {
        Init.init();
//        TestUpdateTime testUpdate = new TestUpdateTime();
//        testUpdate.setName("测试修改");
//        new Insert<>(testUpdate).syncRun();
    }

    @Test
    public void update() {
        Update<TestUpdateTime> timeUpdate = new Update<TestUpdateTime>() {
        };
        timeUpdate.putUpdate("name", "修改后");
        timeUpdate.setKeyValue(1);
        timeUpdate.syncRun();
    }
}
