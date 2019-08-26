package cn.org.zhixiang.aspect;

import cn.org.zhixiang.annotation.RateLimit;
import cn.org.zhixiang.ratelimit.RateLimiter;
import cn.org.zhixiang.util.RateLimitAlgorithm;
import cn.org.zhixiang.util.RateLimiterUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * Description :
 *
 * @author  syj
 * CreateTime    2018/09/05
 * Description   RateLimit注解切面类
 */
@Slf4j
@Aspect
@Component
public class RateLimitAnnotationAspect {

    @Autowired
    @Qualifier("rateLimiterMap")
    private Map<RateLimitAlgorithm, RateLimiter> rateLimiterMap;

    /**
     * 切点调用
     * @param joinPoint 切点
     */
    @Before("@within(cn.org.zhixiang.annotation.RateLimit) || @annotation(cn.org.zhixiang.annotation.RateLimit)")
    public void doBefore(JoinPoint joinPoint) {
        RateLimit rateLimitAnnotation = RateLimiterUtil.getRateLimitAnnotation(joinPoint);
        String key = RateLimiterUtil.getRateKey(joinPoint, rateLimitAnnotation.checkType());
        RateLimiter rateLimiter = rateLimiterMap.get(rateLimitAnnotation.algorithm());
        rateLimiter.consume(key,rateLimitAnnotation.limit(),rateLimitAnnotation.refreshInterval(),
                rateLimitAnnotation.tokenBucketStepNum(),rateLimitAnnotation.tokenBucketTimeInterval());
    }

}
