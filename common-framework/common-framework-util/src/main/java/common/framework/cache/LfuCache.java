package common.framework.cache;

import cn.hutool.cache.impl.LFUCache;

/**
 * <p>Description: LFU(least frequently used) 最少使用率缓存.</p>
 *
 * @author linan
 * @date 2021-01-12
 */
public class LfuCache extends LFUCache {

    public LfuCache(int capacity) {
        super(capacity);
    }

    public LfuCache(int capacity, long timeout) {
        super(capacity, timeout);
    }
}
