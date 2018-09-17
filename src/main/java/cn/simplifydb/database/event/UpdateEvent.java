package cn.simplifydb.database.event;


import cn.simplifydb.database.base.WriteBase;
import cn.simplifydb.database.run.write.Update;

/**
 * 更新数据事件
 *
 * @author jiangzeyin
 */
public interface UpdateEvent extends WriteBase.Event {

    /**
     * 开始事件之前
     *
     * @param object 添加的对象
     * @param update update 操作对象
     * @return 验证结果
     * @author jiangzeyin
     */
    BeforeCode beforeUpdate(Update<?> update, Object object);

    /**
     * 操作成功
     *
     * @param keyValue id
     * @author jiangzeyin
     */
    void completeUpdate(Object keyValue);

    /**
     * 出现异常
     *
     * @param throwable 异常
     * @author jiangzeyin
     */
    void errorUpdate(Throwable throwable);
}
