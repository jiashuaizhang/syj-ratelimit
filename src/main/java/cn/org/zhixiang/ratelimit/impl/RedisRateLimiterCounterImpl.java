package cn.org.zhixiang.ratelimit.impl;

import cn.org.zhixiang.exception.RateLimitErrorEnum;
import cn.org.zhixiang.exception.RateLimitException;
import cn.org.zhixiang.ratelimit.RateLimiter;
import cn.org.zhixiang.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.ArrayList;
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

    private DefaultRedisScript<String> redisScript;

    public RedisRateLimiterCounterImpl(DefaultRedisScript<String> redisScript){
        this.redisScript=redisScript;
    }

    /**
     *
     * @param key 限流key
     * @param limit 限流个数
     * @param lrefreshInterval 限流时间间隔
     * @param tokenBucketStepNum
     * @param tokenBucketTimeInterval
     */
    @Override
    public void consume(String key, long limit, long lrefreshInterval, long tokenBucketStepNum, long tokenBucketTimeInterval) {
        List<Object> keyList = new ArrayList(1);
        List<Object> argvList = new ArrayList<>(2);
        keyList.add(key);
        argvList.add(String.valueOf(limit));
        argvList.add(String.valueOf(lrefreshInterval));
        String result=redisTemplate.execute(redisScript, keyList, argvList.toArray()).toString();
        if(Const.REDIS_ERROR.equals(result)){
            throw new RateLimitException(RateLimitErrorEnum.TOO_MANY_REQUESTS);
        }
    }
}
