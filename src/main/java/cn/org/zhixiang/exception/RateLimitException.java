package cn.org.zhixiang.exception;

/**
 * Description :
 *
 * @author  syj
 * CreateTime    2018/09/07
 * Description   业务异常信息类
 */
public class RateLimitException extends RuntimeException {
    private String msg;
    private Integer status;

    public RateLimitException(RateLimitErrorEnum error){
        this.msg = error.getMsg();
    }

    public RateLimitException(String msg) {
        super(msg);
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

