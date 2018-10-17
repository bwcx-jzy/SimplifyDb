package cn.simplifydb.database.event;

import cn.simplifydb.database.base.BaseWrite;
import cn.simplifydb.database.run.write.Remove;

/**
 * 删除数据事件
 *
 * @author jiangzeyin
 */
public interface RemoveEvent {
    /**
     * 操作成功
     *
     * @param remove remove
     * @return BaseWrite.Event.BeforeCode 是否继续执行
     * @author jiangzeyin
     */
    BaseWrite.Event.BeforeCode beforeRemove(Remove<?> remove);

    /**
     * 出现异常
     *
     * @param throwable 异常
     * @author jiangzeyin
     */
    void errorRemove(Throwable throwable);
}
