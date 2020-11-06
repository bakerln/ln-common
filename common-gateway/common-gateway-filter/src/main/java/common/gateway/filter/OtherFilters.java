package common.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * <p>Description:  服务注册启动</p>
 *
 * @author linan
 * @date 2020-11-03
 */
@Component
public class OtherFilters {
    static Logger log = LoggerFactory.getLogger(OtherFilters.class);


    /**
     * 统计总执行时长
     * @return
     */
    @Bean
    @Order(-1)
    public GlobalFilter filter4useTime() {
        return (exchange, chain) -> {
            long begin = System.currentTimeMillis();
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                URI uri = exchange.getRequest().getURI();
                log.info("uTime:{},{}",System.currentTimeMillis()-begin,uri.getPath());
            }));
        };
    }
}
