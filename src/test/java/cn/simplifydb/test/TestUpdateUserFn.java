package cn.simplifydb.test;

import cn.jiangzeyin.RandomUtil;
import cn.simplifydb.Init;
import cn.simplifydb.database.run.write.Update;
import cn.simplifydb.entity.test1.TestUpdateUser;
import cn.simplifydb.system.SystemSessionInfo;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by jiangzeyin on 2018/9/20.
 */
public class TestUpdateUserFn {

    @Before
    public void init() throws Exception {
        Init.init();
        SystemSessionInfo.setSessionUser(() -> {
            // 为了模仿效果这里就随机返回，标记是用户操作
            return RandomUtil.getRandomCode(1) % 2 == 0 ? 1 : 0;
        });
    }

    @Test
    public void update() {
        for (int i = 0; i < 5; i++) {
            Update<TestUpdateUser> timeUpdate = new Update<TestUpdateUser>() {
            };
            timeUpdate.putUpdate("name", "修改后");
            timeUpdate.setKeyValue(1);
            timeUpdate.syncRun();
        }
    }
}
