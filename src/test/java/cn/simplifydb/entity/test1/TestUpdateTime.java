package cn.simplifydb.entity.test1;

import cn.simplifydb.entity.test1.base.BaseUpdateTime;

/**
 * Created by jiangzeyin on 2018/9/20.
 */
public class TestUpdateTime extends BaseUpdateTime {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
