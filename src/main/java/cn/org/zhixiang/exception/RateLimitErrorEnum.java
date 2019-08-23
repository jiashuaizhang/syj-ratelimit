package cn.org.zhixiang.exception;

import static cn.org.zhixiang.util.Const.CUSTOM;

/**
 * Description :
 *
 * @author  syj
 * CreateTime    2018/09/07
 * Description    通用错误信息
 */
public enum RateLimitErrorEnum {

    TOO_MANY_REQUESTS("syj-rateLimit say: You have made too many requests,please try again later"),
    USER_NOT_FOUND("syj-rateLimit say: not found user info ,please check request.getUserPrincipal().getName()"),
    CUSTOM_NOT_FOUND(String.format("syj-rateLimit say: not found custom info ,please check request.getAttribute('%s')", CUSTOM));

    private final String msg;

    RateLimitErrorEnum(String msg){
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

}
