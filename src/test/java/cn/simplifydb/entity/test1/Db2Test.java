package cn.simplifydb.entity.test1;

import cn.simplifydb.database.base.BaseWrite;
import cn.simplifydb.database.event.UpdateEvent;
import cn.simplifydb.database.run.write.Update;

/**
 * 2号数据库
 * Created by jiangzeyin on 2018/9/18.
 */
public class Db2Test implements UpdateEvent {
    private int id;
    private String name;
    private int sex;

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

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public Db2Test() {
    }

    @Override
    public BaseWrite.Event.BeforeCode beforeUpdate(Update<?> update, Object object) {
//        return BaseWrite.Event.BeforeCode.END;
        return null;
    }

    @Override
    public void completeUpdate(Object keyValue, int count) {

    }

    @Override
    public void errorUpdate(Throwable throwable) {

    }
}
