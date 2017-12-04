package cn.jiangzeyin.system;

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
    private final static ThreadPoolExecutor THREAD_POOL_EXECUTOR = newCachedThreadPool();
    private final static BlockingQueue<Runnable> BLOCKING_QUEUE = new LinkedBlockingQueue<>();
    private final static ProxyHeader PROXY_HEADER = new ProxyHeader();

    /**
     * 创建一个线程池
     *
     * @return 线程池
     * @author jiangzeyin
     */
    private static ThreadPoolExecutor newCachedThreadPool() {
        SystemThreadFactory systemThreadFactory = new SystemThreadFactory("dbutil");
        assert BLOCKING_QUEUE != null;
        assert PROXY_HEADER != null;
        return new ThreadPoolExecutor(50,
                Integer.MAX_VALUE,
                60L,
                TimeUnit.SECONDS,
                BLOCKING_QUEUE, systemThreadFactory,
                PROXY_HEADER);
    }

    public static JSONObject getPoolRunInfo() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "dbutil");
        jsonObject.put("activeCount", THREAD_POOL_EXECUTOR.getActiveCount());
        jsonObject.put("maximumPoolSize", THREAD_POOL_EXECUTOR.getMaximumPoolSize());
        jsonObject.put("corePoolSize", THREAD_POOL_EXECUTOR.getCorePoolSize());
        jsonObject.put("largestPoolSize", THREAD_POOL_EXECUTOR.getLargestPoolSize());
        jsonObject.put("completedTaskCount", THREAD_POOL_EXECUTOR.getCompletedTaskCount());
        jsonObject.put("taskCount", THREAD_POOL_EXECUTOR.getTaskCount());
        jsonObject.put("poolSize", THREAD_POOL_EXECUTOR.getPoolSize());
        jsonObject.put("queueSize", BLOCKING_QUEUE.size());
        jsonObject.put("rejectedExecutionCount", PROXY_HEADER.rejectedExecutionCount.get());
        return jsonObject;
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
            if (t.getPriority() != Thread.MAX_PRIORITY)
                t.setPriority(Thread.MAX_PRIORITY);
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
