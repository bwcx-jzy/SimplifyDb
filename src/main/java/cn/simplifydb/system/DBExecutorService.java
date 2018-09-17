package cn.simplifydb.system;

import com.alibaba.fastjson.JSONObject;

import java.util.concurrent.*;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 系统线程池管理
 *
 * @author jiangzeyin
 */
public class DBExecutorService {
    private final static BlockingQueue<Runnable> BLOCKING_QUEUE = new LinkedBlockingQueue<>();
    private final static ProxyHeader PROXY_HEADER = new ProxyHeader();
    private final static SystemThreadFactory SYSTEM_THREAD_FACTORY = new SystemThreadFactory("dbutil");
    private final static ThreadPoolExecutor THREAD_POOL_EXECUTOR = newCachedThreadPool();

    /**
     * 创建一个线程池
     *
     * @return 线程池
     * @author jiangzeyin
     */
    private static ThreadPoolExecutor newCachedThreadPool() {
        return new ThreadPoolExecutor(50,
                Integer.MAX_VALUE,
                5L,
                TimeUnit.MINUTES,
                BLOCKING_QUEUE,
                SYSTEM_THREAD_FACTORY,
                PROXY_HEADER);
    }

    public static JSONObject getPoolRunInfo() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "dbutil");
        // 核心数
        jsonObject.put("corePoolSize", THREAD_POOL_EXECUTOR.getCorePoolSize());
        // 当前工作集数
        jsonObject.put("poolSize", THREAD_POOL_EXECUTOR.getPoolSize());
        // 活跃线程数
        jsonObject.put("activeCount", THREAD_POOL_EXECUTOR.getActiveCount());
        // 曾经最大线程数
        jsonObject.put("largestPoolSize", THREAD_POOL_EXECUTOR.getLargestPoolSize());
        // 已完成数
        jsonObject.put("completedTaskCount", THREAD_POOL_EXECUTOR.getCompletedTaskCount());
        // 当前任务数
        jsonObject.put("taskCount", THREAD_POOL_EXECUTOR.getTaskCount());
        // 任务队列数
        jsonObject.put("queueSize", BLOCKING_QUEUE.size());
        // 拒绝任务数
        jsonObject.put("rejectedExecutionCount", PROXY_HEADER.rejectedExecutionCount.get());
        // 最大线程编号
        jsonObject.put("maxThreadNumber", SYSTEM_THREAD_FACTORY.threadNumber.get());
        // 最大线程数
        jsonObject.put("maximumPoolSize", THREAD_POOL_EXECUTOR.getMaximumPoolSize());
        return jsonObject;
    }

    /**
     * 关闭所有线程池
     *
     * @author jiangzeyin
     */
    public static void shutdown() {
        DbLog.getInstance().info("关闭数据库使用的线程池");
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

        SystemThreadFactory(String poolName) {
            if (poolName == null || poolName.isEmpty()) {
                poolName = "pool";
            }
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = poolName + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.MAX_PRIORITY) {
                t.setPriority(Thread.MAX_PRIORITY);
            }
            return t;
        }
    }

    private static class ProxyHeader extends CallerRunsPolicy {
        private final AtomicInteger rejectedExecutionCount = new AtomicInteger(0);

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            rejectedExecutionCount.getAndIncrement();
            super.rejectedExecution(r, e);
        }
    }
}
