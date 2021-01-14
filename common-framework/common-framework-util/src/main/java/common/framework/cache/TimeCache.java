package common.framework.cache;

import cn.hutool.cache.impl.TimedCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 定时缓存.</p>
 *
 * @author linan
 * @date 2021-01-12
 */
@Component
public class TimeCache extends TimedCache{


    public TimeCache(@Value("${spring.cache.timeout}")long timeout) {
        super(timeout);
    }
}
