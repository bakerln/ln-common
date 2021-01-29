//package common.gateway.filter;
//
//import common.framework.global.ReturnCode;
//import common.framework.util.JsonUtil;
//import common.framework.wrapper.ResultWrapper;
//import common.framework.wrapper.ResultWrapperUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.RequestPath;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
//
///**
// * <p>Description:  服务注册启动</p>
// *
// * @author linan
// * @date 2020-11-03
// */
//@Component
//public class GatewayFilter implements GlobalFilter,Ordered {
//
//    Logger logger = LoggerFactory.getLogger(GatewayFilter.class);
//
//    private final String header_type = "Grant_Type";
//
//    private final String header_token = "Authorization";
//
//    @Value("${spring.cloud.gateway.permitUrl}")
//    private String permitUrl;
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//        ServerHttpResponse response = exchange.getResponse();
//        HttpHeaders headers = request.getHeaders();
//        RequestPath path =request.getPath();
//
//        //get Header "Grant_Type"
//        if (null == headers.get(header_type) || headers.get(header_type).isEmpty()){
//            response.setStatusCode(HttpStatus.BAD_REQUEST);
//            ResultWrapper resultWrapper = ResultWrapperUtil.custom(ReturnCode.HTTP_FAILED_PARAMS_ERROR, "请求头缺失");
//            byte[] bits = JsonUtil.toJsonStr(resultWrapper).getBytes(StandardCharsets.UTF_8);
//            DataBuffer buffer = response.bufferFactory().wrap(bits);
//            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
//            return response.writeWith(Mono.just(buffer));
//        }
//        String grantType = headers.get(header_type).get(0);
//
//        //is permit
//        if (isPermit(request.getPath().contextPath().toString())){
//            return chain.filter(exchange);
//        }
//
//        //get Header "TOKEN"
//        try{
//            String token = headers.get(header_token).get(0);
////            ResultWrapper resultWrapper = authorizationFeign.checkToken(new AuthTokenDTO(grantType, token, servletPath));
////            if(!resultWrapper.getReturnCode().equals("00200")){
////                throw new RuntimeException("验权失败");
////            }
//            return chain.filter(exchange);
//        }catch (Exception e){
//            //UNAUTHORIZED
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            ResultWrapper resultWrapper = ResultWrapperUtil.custom(ReturnCode.STATE_INVALID_TOKEN, "TOKEN 验证失败");
//            byte[] bits = JsonUtil.toJsonStr(resultWrapper).getBytes(StandardCharsets.UTF_8);
//            DataBuffer buffer = response.bufferFactory().wrap(bits);
//            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
//            return response.writeWith(Mono.just(buffer));
//        }
//
//    }
//
//
//
//    @Override
//    public int getOrder() {
//        return Integer.MIN_VALUE;
//    }
//
//    /**
//     * 判断请求是否是直接路由请求
//     *
//     * @param url 请求url
//     * @return true：直接路由
//     */
//    private boolean isPermit(String url) {
//
//        return Arrays.stream(permitUrl.split(",")).anyMatch(url::equals);
//    }
//}
