package cn.org.zhixiang.ratelimit;




public interface RateLimiter {

    /**
     * 消费一个流量单位,参见具体实现
     * @param key 限流key
     * @param limit
     * @param lrefreshInterval
     * @param tokenBucketStepNum
     * @param tokenBucketTimeInterval
     */
    void consume(String key, long limit, long lrefreshInterval, long tokenBucketStepNum, long tokenBucketTimeInterval);

}
