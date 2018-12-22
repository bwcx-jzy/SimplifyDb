package cn.simplifydb.database.event;


import cn.simplifydb.database.base.BaseWrite;
import cn.simplifydb.database.run.write.Update;

/**
 * 更新数据事件
 *
 * @author jiangzeyin
 */
public interface UpdateEvent {

    /**
     * 开始事件之前
     *
     * @param object 添加的对象
     * @param update update 操作对象
     * @return 验证结果
     */
    BaseWrite.Event.BeforeCode beforeUpdate(Update<?> update, Object object);

    /**
     * 操作成功
     *
     * @param keyValue id
     * @param count    影响的行数
     */
    void completeUpdate(Object keyValue, int count);

    /**
     * 出现异常
     *
     * @param throwable 异常
     */
    void errorUpdate(Throwable throwable);
}
