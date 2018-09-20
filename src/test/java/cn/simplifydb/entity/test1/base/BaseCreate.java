package cn.simplifydb.entity.test1.base;

import cn.simplifydb.database.annotation.FieldConfig;

/**
 * Created by jiangzeyin on 2018/9/20.
 */
public abstract class BaseCreate {
    private int createUser;
    @FieldConfig(insertDefValue = "UNIX_TIMESTAMP(NOW())")
    private long userCreateTime;

    public long getUserCreateTime() {
        return userCreateTime;
    }

    public void setUserCreateTime(long userCreateTime) {
        this.userCreateTime = userCreateTime;
    }

    public int getCreateUser() {
        return createUser;
    }

    public void setCreateUser(int createUser) {
        this.createUser = createUser;
    }
}
