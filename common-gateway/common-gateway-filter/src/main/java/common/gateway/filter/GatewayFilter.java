package common.gateway.filter;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * <p>Description:  服务注册启动</p>
 *
 * @author linan
 * @date 2020-11-03
 */

@Component
public class GatewayFilter implements GlobalFilter,Ordered {

    Logger logger = LoggerFactory.getLogger(GatewayFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestUrl = exchange.getRequest().getPath().pathWithinApplication().toString();
        logger.error("gateway 转发url {}",requestUrl);
        return chain.filter(exchange);
    }



    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

}
