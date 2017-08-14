package cn.jiangzeyin.database.event;



/**
 * 添加数据事件
 *
 * @author jiangzeyin
 * @date 2016-11-7
 */
public interface InsertEvent {
    /**
     *
     */
    enum BeforeCode implements BaseEnum {
        CONTINUE(0, "继续",0), END(1, "结束",-100);

        BeforeCode(int code, String desc,int resultCode) {
            this.code = code;
            this.desc = desc;
            this.resultCode = resultCode;
        }

        private int code;
        private String desc;
        private int resultCode;

        public int getResultCode() {
            return resultCode;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 开始事件之前
     *
     * @return
     * @author jiangzeyin
     * @date 2017-5-02
     */
    int beforeI(Object object);

    /**
     * 操作成功
     *
     * @param dataId
     * @author jiangzeyin
     * @date 2016-11-5
     */
    void completeI(long dataId);

    /**
     * 出现异常
     *
     * @param throwable
     * @author jiangzeyin
     * @date 2016-11-5
     */
    void errorI(Throwable throwable);
}
