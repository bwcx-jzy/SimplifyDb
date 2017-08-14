package cn.jiangzeyin.database.event;


/**
 * 删除数据事件
 *
 * @author jiangzeyin
 * @date 2016-11-7
 */
public interface RemoveEvent {
    /**
     * 操作成功
     *
     * @param dataId
     * @author jiangzeyin
     * @date 2016-11-5
     */
    void completeR(long dataId);

    /**
     * 出现异常
     *
     * @param throwable
     * @author jiangzeyin
     * @date 2016-11-5
     */
    void errorR(Throwable throwable);
}
