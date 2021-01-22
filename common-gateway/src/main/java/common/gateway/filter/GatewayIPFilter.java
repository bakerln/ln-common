package common.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * <p>Description: IP FILTER</p>
 *
 * @author linan
 * @date 2021-01-22
 */
@Component
public class GatewayIPFilter implements GlobalFilter,Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        System.out.println("经过第1个过滤器IpFilter");
        ServerHttpRequest request = exchange.getRequest();
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        InetAddress address = remoteAddress.getAddress();
        System.out.println("ip:"+remoteAddress.getAddress().getHostAddress());
        if ("/172.23.34.110:54643".equals(remoteAddress)){
            return null;
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
