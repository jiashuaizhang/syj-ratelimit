package cn.org.zhixiang.util;

import cn.org.zhixiang.annotation.RateLimit;
import cn.org.zhixiang.exception.RateLimitErrorEnum;
import cn.org.zhixiang.exception.RateLimitException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Description :
 *
 * @author  syj
 * CreateTime    2018/09/05
 * Description   RateLimiter工具类
 */
public class RateLimiterUtil {

    private static String METHOD_PARAM_SEPARATOR = "#";
    private static String PARAM_SEPARATOR = ",";
    private static String PARAM_CLASS_SEPARATOR = "@";

    /**
     * 获取唯一标识此次请求的key
     * @param joinPoint 切点
     * @param checkTypeEnum 枚举
     * @return key
     */
    public static String getRateKey(JoinPoint joinPoint, CheckTypeEnum checkTypeEnum){
        StringBuilder key = appendCommonKey(joinPoint);
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
		switch (checkTypeEnum) {
			case USER:
				// 以用户信息作为key
				if (request.getUserPrincipal() != null) {
					key.append(request.getUserPrincipal().getName());
				} else {
					throw new RateLimitException(RateLimitErrorEnum.USER_NOT_FOUND);
				}
				break;
			case IP:
				// 以IP地址作为key
				key.append(getIpAddr(request));
				break;
			case CUSTOM:
				// 以自定义内容作为key
				if (request.getAttribute(Const.CUSTOM) != null) {
					key.append(request.getAttribute(Const.CUSTOM).toString());
				} else {
					throw new RateLimitException(RateLimitErrorEnum.CUSTOM_NOT_FOUND);
				}
				break;
			case ALL:
				break;
			default:
				throw new RateLimitException(String.format("unsuportted checkTypeEnum: [%s]", checkTypeEnum));
		}
        return key.toString();
    }

    /**
     * 从类或方法上提取RateLimit注解
     * @param joinPoint
     * @return
     */
    public static RateLimit getRateLimitAnnotation(JoinPoint joinPoint) {
        //获取方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        //方法上没有注解，则以类上的为准
        if(rateLimit == null) {
            Class<?> target = joinPoint.getTarget().getClass();
            rateLimit = target.getAnnotation(RateLimit.class);
        }
        return rateLimit;
    }

    /**
     * 根据方法信息拼接唯一标识
     * @param joinPoint
     * @return
     */
    private static StringBuilder appendCommonKey(JoinPoint joinPoint){
        StringBuilder key = new StringBuilder();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        key.append(signature.getName()).append(METHOD_PARAM_SEPARATOR);
        Class<?>[] parameterTypes=signature.getParameterTypes();
        int paramLength = parameterTypes.length;
        if(paramLength > 0) {
            for (int i = 0; i < paramLength - 1; i++) {
            	Class<?> clazz = parameterTypes[i];
            	key.append(clazz.getName()).append(PARAM_SEPARATOR);
    		}
        	Class<?> last = parameterTypes[paramLength - 1];
        	key.append(last.getName());
        }
        key.append(PARAM_CLASS_SEPARATOR).append(joinPoint.getTarget().getClass().getName());
        return key;
    }
    /**
     * 获取当前网络ip
     *
     * @param request HttpServletRequest
     * @return ip
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        if (ipAddress != null && ipAddress.length() > 15) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

}
