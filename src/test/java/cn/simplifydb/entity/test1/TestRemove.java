package cn.simplifydb.entity.test1;

import cn.simplifydb.database.base.BaseWrite;
import cn.simplifydb.database.event.RemoveEvent;
import cn.simplifydb.database.run.write.Remove;
import cn.simplifydb.entity.test1.base.BaseRemove;

/**
 * 测试remove
 * Created by jiangzeyin on 2018/9/20.
 */
public class TestRemove extends BaseRemove implements RemoveEvent {
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

    @Override
    public BaseWrite.Event.BeforeCode beforeRemove(Remove<?> remove) {
        return BaseWrite.Event.BeforeCode.END;
//        return null;
    }

    @Override
    public void errorRemove(Throwable throwable) {

    }
}
