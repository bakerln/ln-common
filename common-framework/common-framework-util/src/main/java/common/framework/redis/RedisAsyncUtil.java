package common.framework.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * <p>Description: 异步Redis Util</p>
 *
 * @author linan
 * @date 2021-01-15
 */
@Component
public class RedisAsyncUtil {

    private RedisAsyncCommands redisAsyncCommands;

    private int timeout;

    /**
     *  init redis
     * @param url  url
     * @param port port
     * @param timeout  expire time in seconds
     */
    public RedisAsyncUtil (@Value("${spring.redis.hosturl}")String url,
                          @Value("${spring.redis.ports}")int port,
                          @Value("${spring.redis.expire}")int timeout){
        RedisURI redisUri = RedisURI.builder()
                .withHost(url)
                .withPort(port)
                .build();
        //判断是否有过期时间
        if(0==timeout){
            redisUri.setTimeout(Duration.ZERO);
        }else {
            redisUri.setTimeout(Duration.ofSeconds(timeout, 0));
            this.timeout = timeout;
        }
        RedisClient redisClient = RedisClient.create(redisUri);
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        this.redisAsyncCommands = connection.async();
    }

    /**
     *  get key value
     * @param key
     * @return
     */
    public RedisFuture get(String key){
        return redisAsyncCommands.get(key);
    }

    /**
     * save with timeout
     * @param key   key
     * @param value values
     * @param timeout expire time in seconds
     */
    public RedisFuture<String> set(String key,Object value,int timeout){
        SetArgs setArgs = SetArgs.Builder.nx().ex(timeout);
        return redisAsyncCommands.set(key, value, setArgs);
    }

    /**
     *  save without outtime
     * @param key   key
     * @param value values
     */
    public RedisFuture<String> set(String key,Object value){
        if (0 != timeout){
            SetArgs setArgs = SetArgs.Builder.nx().ex(timeout);
            return redisAsyncCommands.set(key, value, setArgs);
        }else {
            return redisAsyncCommands.set(key, value);
        }

    }

    /**
     * delete key
     * @param keys keys
     */
    public RedisFuture<Long> delete(Object... keys){
        return redisAsyncCommands.del(keys);
    }

    /**
     * update key timeout
     * @param key key
     * @param timeout timeout in second
     */
    public RedisFuture<Boolean> expire(Object key,long timeout){
        return redisAsyncCommands.expire(key,timeout);
    }

    /**
     *  get all keys with pattern
     * @param pattern 正则表达式
     * @return list
     */
    public RedisFuture<List> getKeyList(String pattern){
        return redisAsyncCommands.keys(pattern);
    }

}
