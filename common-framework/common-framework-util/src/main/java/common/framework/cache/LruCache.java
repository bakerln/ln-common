package common.framework.cache;

import cn.hutool.cache.impl.LRUCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * <p>Description: LRU (least recently used)最近最久未使用缓存
 * 使用时，需要在启动类扫描该包
 * 配置:
 * spring.cache.capacity
 * spring.cache.timeout  毫秒
 * service.util.cache
 * </p>
 *
 * @author linan
 * @date 2021-01-12
 */
@Component
@ConditionalOnProperty(value = "service.cache.enable",havingValue = "true",matchIfMissing = false)
public class LruCache extends LRUCache {

    public LruCache(){
        super(0);
    }

    public LruCache(@Value("${service.cache.capacity}")int capacity) {
        super(capacity);
    }

    public LruCache(@Value("${service.cache.capacity}")int capacity, @Value("${service.cache.timeout}")long timeout) {
        super(capacity, timeout);
    }
}
