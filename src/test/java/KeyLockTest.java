import cn.jiangzeyin.DateUtil;
import cn.simplifydb.system.DBExecutorService;
import cn.simplifydb.util.KeyLock;

/**
 * Created by jiangzeyin on 2018/8/24.
 */
public class KeyLockTest {
    public static void main(String[] args) {
        for (int i = 0; i < 12 * 3; i++) {
            DBExecutorService.execute(new KeyLockRun(i + 1));
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DBExecutorService.execute(() -> {
            while (true) {
                int count = KeyLockRun.lock.getLockKeyCount();
                if (count <= 0) {
//                    System.exit(-1);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    static class KeyLockRun implements Runnable {
        private final int no;
        private final String name;
        public static final KeyLock<Integer> lock = new KeyLock<>();
        private final int group;

        public KeyLockRun(int no) {
            this.no = no;
            this.group = no % 3;
            switch (group) {
                case 0:
                    this.name = "【1组" + no + "】";
                    break;
                case 1:
                    this.name = "【2组" + no + "】";
                    break;
                case 2:
                    this.name = "【3组" + no + "】";
                    break;
                default:
                    this.name = "error";
                    break;
            }

        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + "  " + name + "  进入准备执行: " + DateUtil.getCurrentFormatTime(null));
            lock.lock(group);
            System.out.println(Thread.currentThread().getName() + "  " + name + "  获取锁  " + DateUtil.getCurrentFormatTime(null));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "  " + name + "释放锁  " + DateUtil.getCurrentFormatTime(null));
            lock.unlock(group);
        }
    }
}
