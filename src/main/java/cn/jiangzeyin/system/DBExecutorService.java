package cn.jiangzeyin.system;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 系统线程池管理
 *
 * @author jiangzeyin
 */
public class DBExecutorService {
    private final static ThreadPoolExecutor THREAD_POOL_EXECUTOR = newCachedThreadPool();

    /**
     * 创建一个线程池
     *
     * @return 线程池
     * @author jiangzeyin
     */
    private static ThreadPoolExecutor newCachedThreadPool() {
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(50, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
        // 提交线程池失败 处理方法
        executorService.setRejectedExecutionHandler(new CallerRunsPolicy());
        // 创建线程方法
        SystemThreadFactory systemThreadFactory = new SystemThreadFactory("dbutil");
        executorService.setThreadFactory(systemThreadFactory);
        return executorService;
    }

    /**
     * 关闭所有线程池
     *
     * @author jiangzeyin
     */
    public static void shutdown() {
        SystemDbLog.getInstance().info("关闭数据库使用的线程池");
        THREAD_POOL_EXECUTOR.shutdown();
    }

    public static void execute(Runnable command) {
        THREAD_POOL_EXECUTOR.execute(command);
    }

    /**
     * 线程池工厂
     *
     * @author jiangzeyin
     */
    private static class SystemThreadFactory implements ThreadFactory {

        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        /**
         * @return the threadNumber
         */
        public int getThreadNumber() {
            return threadNumber.get();
        }

        SystemThreadFactory(String poolName) {
            if (poolName == null || poolName.isEmpty())
                poolName = "pool";
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = poolName + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
