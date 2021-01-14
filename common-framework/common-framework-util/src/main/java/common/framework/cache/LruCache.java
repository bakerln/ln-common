package common.framework.cache;

import cn.hutool.cache.impl.LRUCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <p>Description: LRU (least recently used)最近最久未使用缓存</p>
 *
 * @author linan
 * @date 2021-01-12
 */
@Component
public class LruCache extends LRUCache {

    public LruCache(){
        super(0);
    }

    public LruCache(@Value("${spring.cache.capacity}")int capacity) {
        super(capacity);
    }

    public LruCache(@Value("${spring.cache.capacity}")int capacity, @Value("${spring.cache.timeout}")long timeout) {
        super(capacity, timeout);
    }
}
