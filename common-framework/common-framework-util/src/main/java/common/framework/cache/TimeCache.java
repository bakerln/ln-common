package common.framework.cache;

import cn.hutool.cache.impl.TimedCache;

import java.util.Map;

/**
 * <p>Description: 定时缓存.</p>
 *
 * @author linan
 * @date 2021-01-12
 */
public class TimeCache extends TimedCache{

    public TimeCache(long timeout) {
        super(timeout);
    }

    public TimeCache(long timeout, Map map) {
        super(timeout, map);
    }
}
