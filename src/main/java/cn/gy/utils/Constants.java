package cn.gy.utils;

public class Constants {
    public static class User_AUTH {
        public final static String AUTH_UNKNOWN = "1001";
        public final static String AUTH_AUTHENTICATION_FAILED = "1002";//认证失败
        public final static String AUTH_PARAMETER_ERROR = "1003";//用户名密码参数错误，为空
        public final static String AUTH_TOKEN_INVALID = "1004";//token无效
    }

    public static class Redis_Expire {
        public static final long DEFAULT_EXPIRE = 60;//80s 有慢sql，超时时间设置长一点
        public final static int SESSION_TIMEOUT = 2 * 60 * 60;//默认2h
    }

    public static class MessageStatus {
        public final static Integer OVER = -1;//已结抢过
        public static final Integer SUCCESS = 1;//成功抢到
        public final static Integer FAIL = 0;//没抢到
    }

    public static class OrderStatus {
        public final static Integer TOPAY = 0;//待支付
        public static final Integer SUCCESS = 1;//支付成功
        public final static Integer FAIL = 2;//支付失败
    }
}
