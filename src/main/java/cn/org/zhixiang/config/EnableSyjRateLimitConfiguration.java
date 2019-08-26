package cn.org.zhixiang.config;


import cn.org.zhixiang.ratelimit.RateLimiter;
import cn.org.zhixiang.ratelimit.impl.RedisRateLimiterCounterImpl;
import cn.org.zhixiang.ratelimit.impl.RedisRateLimiterTokenBucketImpl;
import cn.org.zhixiang.util.RateLimitAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Description :
 *
 * @author  syj
 * CreateTime    2018/09/05
 * Description
 */
@Slf4j
@Configuration
@ComponentScan(basePackages="cn.org.zhixiang")
public class EnableSyjRateLimitConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory, StringRedisSerializer stringRedisSerializer) {
        RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(stringRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisRateLimiterTokenBucketImpl tokenRateLimiter() {
        DefaultRedisScript<String> consumeRedisScript=new DefaultRedisScript();
        consumeRedisScript.setResultType(String.class);
        consumeRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/redis-ratelimiter-tokenBucket.lua")));
        return new RedisRateLimiterTokenBucketImpl(consumeRedisScript);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisRateLimiterCounterImpl counterRateLimiter() {
        DefaultRedisScript<String> redisScript=new DefaultRedisScript();
        redisScript.setResultType(String.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/redis-ratelimiter-counter.lua")));
        return new RedisRateLimiterCounterImpl(redisScript);
    }

    @Bean
    @ConditionalOnMissingBean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean("rateLimiterMap")
    public Map<RateLimitAlgorithm, RateLimiter> rateLimiterMap() {
        Map<RateLimitAlgorithm, RateLimiter> rateLimiterMap = new HashMap<>();
        rateLimiterMap.put(RateLimitAlgorithm.COUNTER, counterRateLimiter());
        rateLimiterMap.put(RateLimitAlgorithm.TOKEN_BUCKET, tokenRateLimiter());
        return Collections.unmodifiableMap(rateLimiterMap);
    }
}
