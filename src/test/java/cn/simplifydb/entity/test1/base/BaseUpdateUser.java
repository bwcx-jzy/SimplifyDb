package cn.simplifydb.entity.test1.base;

/**
 * 记录数据最后修改时间
 * Created by jiangzeyin on 2018/9/20.
 */
public abstract class BaseUpdateUser extends BaseUpdateTime {
    
    private int lastModifyUser;
    private int lastModifyTime;

    public int getLastModifyUser() {
        return lastModifyUser;
    }

    public void setLastModifyUser(int lastModifyUser) {
        this.lastModifyUser = lastModifyUser;
    }

    public int getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(int lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }
}
