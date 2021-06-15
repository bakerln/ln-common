package common.framework.cache;

import cn.hutool.cache.impl.LFUCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * <p>Description: LFU(least frequently used) 最少使用率缓存.
 *  使用时，需要在启动类扫描该包
 *  配置:
 * spring.cache.capacity
 * spring.cache.timeout  毫秒
 * service.util.cache
 * </p>
 *
 * @author linan
 * @date 2021-01-12
 */
@Component
@ConditionalOnProperty(value = "service.cache.enable",havingValue = "false",matchIfMissing = false)
public class LfuCache extends LFUCache {


    public LfuCache(){
        super(0);
    }

    public LfuCache(@Value("${service.cache.capacity}")int capacity) {
        super(capacity);
    }

    public LfuCache(@Value("${service.cache.capacity}")int capacity, @Value("${service.cache.timeout}")long timeout) {
        super(capacity, timeout);
    }
}
