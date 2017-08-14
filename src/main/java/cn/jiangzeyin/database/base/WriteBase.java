package cn.jiangzeyin.database.base;


import cn.jiangzeyin.system.SystemDbLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 写入数据
 *
 * @author jiangzeyin
 * @date 2016-10-12
 */
public abstract class WriteBase<T> extends Base<T> {

    private T data;
    private Throwable throwable;
    private boolean isAsync;

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public boolean isAsync() {
        return isAsync;
    }

    public void setAsync(boolean isAsyn) {
        this.isAsync = isAsyn;
    }

    /**
     * 异步执行
     *
     * @author jiangzeyin
     * @date 2016-10-12
     */
    public abstract void run();

    /**
     * 同步执行
     *
     * @return
     * @author jiangzeyin
     * @date 2016-10-12
     */
    public abstract long syncRun();

    /**
     * @param data
     */
    public WriteBase(T data) {
        // TODO Auto-generated constructor stub
        this.data = data;
    }

    public T getData() {
        return data;
    }

    @Override
    public List<String> getRemove() {
        // TODO Auto-generated method stub
        List<String> removes = super.getRemove();
        if (removes == null) {
            removes = new ArrayList<>();
        }
        // 这些字段不能认为修改 仅能由系统处理
        removes.add("createUser".toLowerCase());
        removes.add("lastModifyUser".toLowerCase());
        removes.add("lastModifyTime".toLowerCase());
        return removes;
    }

    public void setData(T data) {
        this.data = data;
    }

    public WriteBase<T> getWriteBase() {
        return this;
    }

    /**
     * @param t
     * @author jiangzeyin
     * @date 2016-11-21
     */
    @Override
    public void isThrows(Throwable t) {
        // TODO Auto-generated method stub
        if (isAsync()) {
            t.addSuppressed(getThrowable());
            if (isThrows()) {
                throw new RuntimeException(t);
            } else {
                SystemDbLog.getInstance().error("执行数据库操作", t);
            }
        } else {
            super.isThrows(t);
        }
    }

    /**
     * @author jiangzeyin
     * @date 2016-11-21
     */
    @Override
    protected void recycling() {
        // TODO Auto-generated method stub
        super.recycling();
        data = null;
    }
}
