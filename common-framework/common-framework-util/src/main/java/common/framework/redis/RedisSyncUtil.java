package common.framework.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>Description: Sync Redis Util
 * 使用时，需要在启动类扫描该包
 *  配置:
 * spring.redis.hosturl  redis地址
 * spring.redis.ports     redis端口
 * spring.redis.expire   0为永不过期
 * </p>
 *
 * @author linan
 * @date 2021-01-14
 */
@Component
@ConditionalOnProperty(value = "service.redis.enable",havingValue = "false",matchIfMissing = false)
public class RedisSyncUtil {

    private RedisCommands redisCommands;

    private StatefulRedisConnection<String, String> statefulRedisConnection;

    private RedisClient redisClient;

    private int timeout;

    /**
     *  init redis
     * @param url  url
     * @param port port
     * @param timeout  expire time in seconds
     */
    public RedisSyncUtil (@Value("${service.redis.hosturl}")String url,
                                       @Value("${service.redis.ports}")int port,
                                       @Value("${service.redis.expire}")int timeout){
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
        this.redisClient = RedisClient.create(redisUri);
        this.statefulRedisConnection = redisClient.connect();
        this.redisCommands = statefulRedisConnection.sync();
    }

    /**
     *  get key value
     * @param key
     * @return
     */
    public Object  get(String key){
        return redisCommands.get(key);
    }

    /**
     * save with timeout
     * @param key   key
     * @param value values
     * @param timeout expire time in seconds
     */
    public void set(String key,Object value,int timeout){
        SetArgs setArgs = SetArgs.Builder.nx().ex(timeout);
        redisCommands.set(key, value, setArgs);
    }

    /**
     *  save without outtime
     * @param key   key
     * @param value values
     */
    public void set(String key,Object value){
        if (0 != timeout){
            SetArgs setArgs = SetArgs.Builder.nx().ex(timeout);
            redisCommands.set(key, value, setArgs);
        }else {
            redisCommands.set(key, value);
        }
    }

    /**
     * delete key
     * @param keys keys
     */
    public void delete(Object... keys){
        redisCommands.del(keys);
    }

    /**
     * update key timeout
     * @param key key
     * @param timeout timeout in second
     */
    public void expire(Object key,long timeout){
        redisCommands.expire(key,timeout);
    }

    /**
     *  get all keys with pattern
     * @param pattern 正则表达式
     * @return list
     */
    public List<String> getKeyList(String pattern){
        return redisCommands.keys(pattern);
    }

    /**
     * shutdown
     */
    public void shutdown(){
        this.statefulRedisConnection.close();
        this.redisClient.shutdown();
    }
}
