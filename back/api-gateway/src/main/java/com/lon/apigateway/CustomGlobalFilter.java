package com.lon.apigateway;

import apicommon.model.entity.InterfaceInfo;
import apicommon.model.entity.User;
import apicommon.service.InnerInterfaceInfoService;
import apicommon.service.InnerUserInterfaceInfoService;
import apicommon.service.InnerUserService;
import com.lon.clientsdk.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class CustomGlobalFilter implements GlobalFilter, Ordered {
    private static final List<String> WHITE_LIST = Arrays.asList("127.0.0.1");
    @DubboReference
    private InnerUserService innerUserService;
    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;
    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求id" + request.getId());
        log.info("请求路径" + request.getPath().value());
        log.info("请求方法" + request.getMethod());
        log.info("请求参数" + request.getBody());
        log.info("请求地址" + request.getRemoteAddress());
        ServerHttpResponse response = exchange.getResponse();
//        if (WHITE_LIST.contains(request.getLocalAddress().getHostString())){
//            response.setStatusCode(HttpStatus.FORBIDDEN);
//            return response.setComplete();
//        }
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        String path = headers.getFirst("path");
        String uri = headers.getFirst("uri");
        String host = "http://localhost:8123";
        String url = host +  request.getPath().toString();

        User invokeUser = null;
        try{
            invokeUser = innerUserService.getInvokeUser(accessKey);
        }catch (Exception e){
            log.error("getInvokeUser error",e);
        }
        if(invokeUser == null){
            return handleNoAuth(response);
        }

        //判断access
        if(Long.parseLong(nonce) > 10000){
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        Long l = System.currentTimeMillis()/1000;
        if((l - Long.parseLong(timestamp)) >= 5*60){
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        String secret = invokeUser.getSecretKey();
        String serverSign = SignUtils.genSign(body,secret);
        if (!sign.equals(serverSign)){
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        InterfaceInfo interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(url, request.getMethod().toString());
        if(interfaceInfo == null){
            return handleNoAuth(response);
        }
        // TODO: 2024/5/28 是否有调用次数 
//        Mono<Void> filter = chain.filter(exchange);
//        log.info("响应" + response.getStatusCode());
//        if(response.getStatusCode() != HttpStatus.OK){
//            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
//            return response.setComplete();
//        }
        return handleResponse(exchange,chain,interfaceInfo.getId(),invokeUser.getId());
//        return filter;
    }
    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        // 7. 调用成功，接口调用次数 + 1 invokeCount
//                                        try {
//                                            innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
//                                        } catch (Exception e) {
//                                            log.error("invokeCount error", e);
//                                        }
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8); //data
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应结果：" + data);
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }

}
