//package common.gateway.filter;
//
//import common.framework.cache.TimeCache;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//
///**
// * <p>Description: IP FILTER</p>
// *
// * @author linan
// * @date 2021-01-22
// */
//@Component
//public class GatewayIPFilter implements GlobalFilter,Ordered {
//
//    @Autowired
//    private TimeCache timeCache;
//
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//
//        ServerHttpRequest request = exchange.getRequest();
//        InetSocketAddress remoteAddress = request.getRemoteAddress();
//        InetAddress address = remoteAddress.getAddress();
//        ServerHttpResponse response = exchange.getResponse();
//
//        //从cache获取
////        timeCache.
//        if ("/172.23.34.110:54643".equals(remoteAddress)){
////            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            return response.setComplete();
//
//        }
//        return chain.filter(exchange);
//    }
//
//    @Override
//    public int getOrder() {
//        return 1;
//    }
//}
