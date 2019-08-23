package cn.org.zhixiang.ratelimit.impl;

import cn.org.zhixiang.exception.RateLimitErrorEnum;
import cn.org.zhixiang.exception.RateLimitException;
import cn.org.zhixiang.ratelimit.RateLimiter;
import cn.org.zhixiang.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.time.Instant;
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
public class RedisRateLimiterTokenBucketImpl implements RateLimiter {

    @Autowired
    private RedisTemplate redisTemplate;

    private DefaultRedisScript<String> redisScript;



    public RedisRateLimiterTokenBucketImpl(DefaultRedisScript<String> redisScript){
        this.redisScript=redisScript;

    }

    /**
     * 消费一个令牌
     * @param key 限流key
     * @param limit 桶的容量
     * @param lrefreshInterval 冗余参数，值任意
     * @param tokenBucketStepNum 一个窗口的令牌个数
     * @param tokenBucketTimeInterval 窗口大小: 单位秒
     */
    @Override
    public void consume(String key, long limit, long lrefreshInterval, long tokenBucketStepNum, long tokenBucketTimeInterval) {
        List<Object> keyList = new ArrayList(1);
        List<Object> argvList = new ArrayList<>(4);
        keyList.add(key);
        argvList.add(String.valueOf(limit));
        argvList.add(String.valueOf(tokenBucketStepNum));
        argvList.add(String.valueOf(tokenBucketTimeInterval));
        argvList.add(String.valueOf(Instant.now().getEpochSecond()));
        String result=redisTemplate.execute(redisScript, keyList, argvList.toArray()).toString();
        if(Const.REDIS_ERROR.equals(result)){
            throw new RateLimitException(RateLimitErrorEnum.TOO_MANY_REQUESTS);
        }

    }

}
