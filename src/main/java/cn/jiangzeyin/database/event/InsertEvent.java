package cn.jiangzeyin.database.event;

import cn.jiangzeyin.database.base.WriteBase;
import cn.jiangzeyin.database.run.write.Insert;

/**
 * 添加数据事件
 *
 * @author jiangzeyin
 */
public interface InsertEvent extends WriteBase.Event {

    /**
     * 开始事件之前
     *
     * @param object 添加的对象
     * @param insert insert 操作对象
     * @return 验证结果
     * @author jiangzeyin
     */
    BeforeCode beforeInsert(Insert<?> insert, Object object);

    /**
     * 操作成功
     *
     * @param dataId 结果id
     * @author jiangzeyin
     */
    void completeInsert(Object dataId);

    /**
     * 出现异常
     *
     * @param throwable 异常
     * @author jiangzeyin
     */
    void errorInsert(Throwable throwable);
}
