package common.framework.cache;

import cn.hutool.cache.impl.LRUCache;

/**
 * <p>Description: LRU (least recently used)最近最久未使用缓存</p>
 *
 * @author linan
 * @date 2021-01-12
 */
public class LruCache extends LRUCache {

    public LruCache(int capacity) {
        super(capacity);
    }

    public LruCache(int capacity, long timeout) {
        super(capacity, timeout);
    }
}
