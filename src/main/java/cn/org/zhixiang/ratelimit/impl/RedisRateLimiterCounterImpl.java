package cn.org.zhixiang.ratelimit.impl;

import cn.org.zhixiang.exception.RateLimitErrorEnum;
import cn.org.zhixiang.exception.RateLimitException;
import cn.org.zhixiang.ratelimit.RateLimiter;
import cn.org.zhixiang.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description :
 *
 * @author  syj
 * CreateTime    2018/09/05
 * Description
 */
@Slf4j
public class RedisRateLimiterCounterImpl implements RateLimiter {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisSerializer stringRedisSerializer;

    private RedisScript<String> redisScript;

    public RedisRateLimiterCounterImpl(RedisScript<String> redisScript){
        this.redisScript=redisScript;
    }

    /**
     *
     * @param key 限流key
     * @param limit 限流个数
     * @param refreshInterval 限流时间间隔
     * @param tokenBucketStepNum
     * @param tokenBucketTimeInterval
     */
    @Override
    public void consume(String key, long limit, long refreshInterval, long tokenBucketStepNum, long tokenBucketTimeInterval) {
        List<Object> keyList = Arrays.asList(key);
        Object[] argv = {String.valueOf(limit), String.valueOf(refreshInterval)};
        String result = (String) redisTemplate.execute(redisScript, stringRedisSerializer, stringRedisSerializer, keyList, argv);
        if(Const.REDIS_ERROR.equals(result)){
            throw new RateLimitException(RateLimitErrorEnum.TOO_MANY_REQUESTS);
        }
    }
}
