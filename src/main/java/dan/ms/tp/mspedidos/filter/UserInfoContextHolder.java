package dan.ms.tp.mspedidos.filter;

import dan.ms.tp.mspedidos.dto.auth.UserInfo;

public class UserInfoContextHolder {
    private static final ThreadLocal<UserInfo> userContext = new ThreadLocal<>();

    public static void setUser(UserInfo user) {
        userContext.set(user);
    }

    public static UserInfo getUser() {
        return userContext.get();
    }

    public static void clear() {
        userContext.remove();
    }
}