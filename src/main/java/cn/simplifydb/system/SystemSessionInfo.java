package cn.simplifydb.system;

/**
 * 系统session 信息
 *
 * @author jiangzeyin
 */
public final class SystemSessionInfo {
    private volatile static SessionUser sessionUser;

    private SystemSessionInfo() {
        throw new AssertionError("No SystemSessionInfo instances for you!");
    }

    /**
     * @param sessionUser 接口
     * @author jiangzeyin
     */
    public static void setSessionUser(SessionUser sessionUser) {
        SystemSessionInfo.sessionUser = sessionUser;
    }

    /**
     * 获取当前操作session 用户名
     *
     * @return 用户名
     * @author jiangzeyin
     */
    public static String getUserName() {
        if (sessionUser == null) {
            return "";
        }
        return sessionUser.getUserName();
    }

    /**
     * 获取当前操作session 用户id
     *
     * @return id
     * @author jiangzeyin
     */
    public static int getUserId() {
        if (sessionUser == null) {
            return -1;
        }
        return sessionUser.getUserId();
    }

    public static String userIdGetName(int userId) {
        if (sessionUser == null) {
            return "";
        }
        return sessionUser.userIdGetName(userId);
    }

    /**
     * 获取session 信息接口
     *
     * @author jiangzeyin
     */
    public interface SessionUser {
        /**
         * 返回当前操作的用户名
         *
         * @return name
         */
        String getUserName();

        /**
         * 返回当前操作的用户Id
         *
         * @return 必反
         */
        int getUserId();

        /**
         * 根据用户id 获取用户名
         *
         * @param userId id
         * @return name
         */
        String userIdGetName(int userId);
    }
}
