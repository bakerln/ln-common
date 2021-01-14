package common.framework.cache;

import cn.hutool.cache.impl.LFUCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <p>Description: LFU(least frequently used) 最少使用率缓存.</p>
 *
 * @author linan
 * @date 2021-01-12
 */
@Component
public class LfuCache extends LFUCache {


    public LfuCache(){
        super(0);
    }

    public LfuCache(@Value("${spring.cache.capacity}")int capacity) {
        super(capacity);
    }

    public LfuCache(@Value("${spring.cache.capacity}")int capacity, @Value("${spring.cache.timeout}")long timeout) {
        super(capacity, timeout);
    }
}
