package com.b2cmall.gateway.filter;
import org.springframework.cloud.gateway.filter.*;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.*;

@Component
public class WhiteUrlFilter implements GlobalFilter, Ordered {
    private static final Set<String> POST=new HashSet<>(Arrays.asList(
        "/api/shop/register","/api/shop/v2/register","/api/shop/registration/status",
        "/api/employee/login","/api/employee/checkToken","/api/order/mock/callback"));
    private static final Set<String> GET=new HashSet<>(Arrays.asList(
        "/api/employee/health","/api/employee/avatar"));
    @Override public int getOrder(){return FilterPolicy.WHITE_ORDER;}
    @Override public Mono<Void> filter(ServerWebExchange exchange,GatewayFilterChain chain){
        String path=exchange.getRequest().getURI().getRawPath();
        String method=exchange.getRequest().getMethodValue();
        boolean white=("POST".equals(method)&&POST.contains(path))||("GET".equals(method)&&GET.contains(path));
        exchange.getAttributes().put(FilterPolicy.WHITE_URL,white);
        return chain.filter(exchange);
    }
}
