package cn.org.zhixiang.util;

import cn.org.zhixiang.annotation.CheckTypeEnum;
import cn.org.zhixiang.exception.RateLimitErrorEnum;
import cn.org.zhixiang.exception.RateLimitException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
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
        //以用户信息作为key
        if(CheckTypeEnum.USER.equals(checkTypeEnum)){
            if(request.getUserPrincipal()!=null){
                key.append(request.getUserPrincipal().getName());
            }else{
                throw new RateLimitException(RateLimitErrorEnum.USER_NOT_FOUND);
            }
        }
        //以IP地址作为key
        if(CheckTypeEnum.IP.equals(checkTypeEnum)){
            key.append(getIpAddr(request));
        }
        //以自定义内容作为key
        if(CheckTypeEnum.CUSTOM.equals(checkTypeEnum)){
            if(request.getAttribute(Const.CUSTOM)!=null){
                key.append(request.getAttribute(Const.CUSTOM).toString());
            }else{
                throw new RateLimitException(RateLimitErrorEnum.CUSTOM_NOT_FOUND);
            }
        }
        return key.toString();
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
        Class[] parameterTypes=signature.getParameterTypes();
        for (Class clazz:parameterTypes){
            key.append(clazz.getName());
            if(clazz != parameterTypes[parameterTypes.length - 1]){
                key.append(PARAM_SEPARATOR);
            }
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
