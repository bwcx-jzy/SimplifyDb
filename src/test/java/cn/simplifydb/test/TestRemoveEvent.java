package cn.simplifydb.test;

import cn.simplifydb.Init;
import cn.simplifydb.database.run.write.Remove;
import cn.simplifydb.entity.test1.Db2Test;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by jiangzeyin on 2018/10/16.
 */
public class TestRemoveEvent {
    @Before
    public void init() throws Exception {
        Init.init();
    }

    @Test
    public void test() {
        Remove<Db2Test> remove = new Remove<Db2Test>(Remove.Type.remove) {
        };
        remove.setIds(1);
        remove.syncRun();
    }
}
