package common.framework.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * <p>Description: 同步Redis工具</p>
 *
 * @author linan
 * @date 2021-01-14
 */
@Component
public class RedisSyncUtil {

    private RedisCommands redisCommands;

    private int timeout;

    /**
     *  初始化redis
     * @param url 地址
     * @param port 端口
     * @param timeout  expire time in seconds
     */
    public RedisSyncUtil (@Value("${spring.redis.hosturl}")String url,
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
        this.redisCommands = connection.sync();
    }

    /**
     *  获取key 值
     * @param key
     * @return
     */
    public Object  get(String key){
        if (null != redisCommands.get(key)){
                return redisCommands.get(key);
        }else {
            return redisCommands.get(key);
        }
    }

    /**
     * 保存一个对象
     * @param key   key
     * @param value values
     * @param timeout expire time in seconds
     */
    public void set(String key,Object value,int timeout){
        SetArgs setArgs = SetArgs.Builder.nx().ex(timeout);
        redisCommands.set(key, value, setArgs);
    }

    /**
     * 保存一个对象
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
     * 删除key
     * @param keys key
     */
    public void delete(Object... keys){
        redisCommands.del(keys);
    }
}
