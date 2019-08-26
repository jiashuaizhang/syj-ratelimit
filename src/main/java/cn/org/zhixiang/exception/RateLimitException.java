package cn.org.zhixiang.exception;

/**
 * Description :
 *
 * @author  syj
 * CreateTime    2018/09/07
 * Description   业务异常信息类
 */
public class RateLimitException extends RuntimeException {
	
	private static final long serialVersionUID = -8096976507466419703L;

    public RateLimitException(RateLimitErrorEnum error){
        this(error.getMsg());
    }

    public RateLimitException(String msg) {
        super(msg);
    }

}

