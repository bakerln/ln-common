package common.framework.cache;

import cn.hutool.cache.impl.TimedCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * <p>Description: 定时缓存.
 * 使用时，需要在启动类扫描该包
 * 配置:
 * spring.cache.timeout  毫秒
 * service.util.cache
 * </p>
 *
 * @author linan
 * @date 2021-01-12
 */
@Component
@ConditionalOnProperty(value = "service.cache.enable",havingValue = "true",matchIfMissing = false)
public class TimeCache extends TimedCache{


    public TimeCache(@Value("${service.cache.timeout}")long timeout) {
        super(timeout);
    }
}
