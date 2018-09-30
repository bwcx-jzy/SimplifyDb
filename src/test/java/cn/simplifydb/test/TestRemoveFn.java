package cn.simplifydb.test;

import cn.simplifydb.Init;
import cn.simplifydb.database.run.write.Remove;
import cn.simplifydb.entity.test1.TestRemove;
import org.junit.Before;
import org.junit.Test;

/**
 * 测试删除
 * Created by jiangzeyin on 2018/9/20.
 */
public class TestRemoveFn {

    @Before
    public void init() throws Exception {
        Init.init();
//        for (int i = 0; i < 5; i++) {
//            TestRemove testRemove = new TestRemove();
//            testRemove.setName("测试删除" + RandomUtil.getRandomCode(2));
//            new Insert<>(testRemove).syncRun();
//        }
    }

    @Test
    public void remove() {
        Remove<TestRemove> removeRemove = new Remove<TestRemove>(Remove.Type.remove) {
        };
        removeRemove.setIds(1);
        removeRemove.syncRun();
    }


    @Test
    public void remove2() {
        Remove<TestRemove> removeRemove = new Remove<TestRemove>(Remove.Type.remove) {
        };
        removeRemove.putUpdate("name", "测试删除remove");
        removeRemove.setIds(1);
        removeRemove.syncRun();
    }


    @Test
    public void remove3() {
        Remove<TestRemove> removeRemove = new Remove<TestRemove>(Remove.Type.delete) {
        };
//        removeRemove.putUpdate("name", "测试删除remove");
        removeRemove.setIds(2);
        removeRemove.syncRun();
    }

    @Test
    public void remove4() {
        Remove<TestRemove> removeRemove = new Remove<TestRemove>(Remove.Type.recovery) {
        };
        removeRemove.putUpdate("name", "测试删除recovery");
        removeRemove.setIds(4);
        removeRemove.where("id>1");
        removeRemove.syncRun();
    }


    @Test
    public void remove5() {
        Remove<TestRemove> removeRemove = new Remove<TestRemove>(Remove.Type.recovery) {
        };
        removeRemove.putUpdate("name", "测试删除recovery");
        removeRemove.setIds(5);
        removeRemove.where("name=?");
        removeRemove.addParameters("测试");
        removeRemove.syncRun();
    }

    @Test
    public void remove6() {
        Remove<TestRemove> removeRemove = new Remove<TestRemove>(Remove.Type.delete) {
        };
        removeRemove.setKeyColumnAndValue("name", 1);
//        removeRemove.setIds(22);
        removeRemove.syncRun();
    }
}
