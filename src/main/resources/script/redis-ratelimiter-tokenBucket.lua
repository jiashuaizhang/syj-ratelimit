local key = KEYS[1];
local limit = tonumber(ARGV[1]);
local step = tonumber(ARGV[2]);
local interval = tonumber(ARGV[3]);
local nowTime = tonumber(ARGV[4]);

local lastClearTimeKey='syj-rateLimiter-lastClearTime'..'{'..key..'}'
local lastClearTime=redis.call('GET',lastClearTimeKey);
local hasKey = redis.call('EXISTS',key);
if hasKey == 1 then
    local diff = tonumber(nowTime)-tonumber(lastClearTime);
    local value = tonumber(redis.call('GET',key));
    if  diff >= interval then
            local maxValue = value+math.floor(diff/interval)*step;
            value = math.min(limit,maxValue);
            redis.call('SET',lastClearTimeKey,nowTime);
            redis.call('SET',key,value);
    end
    if value <= 0 then
        return '-1';
    else
        redis.call('DECR',key);
    end
else
    local setResult = redis.call('SET',key,limit - 1);
    redis.call('SET',lastClearTimeKey,nowTime);
end
return '1';
