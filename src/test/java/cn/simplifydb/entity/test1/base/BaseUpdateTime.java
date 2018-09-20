package cn.simplifydb.entity.test1.base;

/**
 * 记录数据最后修改时间
 * Created by jiangzeyin on 2018/9/20.
 */
public abstract class BaseUpdateTime {
    
    private int modifyTime;

    public int getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(int modifyTime) {
        this.modifyTime = modifyTime;
    }
}
