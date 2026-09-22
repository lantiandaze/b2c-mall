package com.b2cmall.gateway.filter;

import com.b2cmall.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.gateway.filter.*;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Component
public class AuthFilter implements GlobalFilter, Ordered {
    private static final org.slf4j.Logger log=org.slf4j.LoggerFactory.getLogger(AuthFilter.class);
    private final WebClient employee;
    private final String gatewayToken;
    @org.springframework.beans.factory.annotation.Autowired
    public AuthFilter(ReactorLoadBalancerExchangeFilterFunction loadBalancer,
                       @Value("${employee.auth-url:}") String directUrl,
                       @Value("${internal.token}") String gatewayToken) {
        this.gatewayToken=gatewayToken;
        WebClient.Builder builder=WebClient.builder();
        if(directUrl.isEmpty())builder.filter(loadBalancer);
        employee=builder.baseUrl(directUrl.isEmpty() ? "http://employee-service" : directUrl).build();
    }
    AuthFilter(WebClient employee,String gatewayToken){this.employee=employee;this.gatewayToken=gatewayToken;}
    @Override public int getOrder(){return FilterPolicy.AUTH_ORDER;}
    @Override public Mono<Void> filter(ServerWebExchange exchange,GatewayFilterChain chain) {
        String path=exchange.getRequest().getURI().getRawPath();
        if(path.equals("/api/employee/save"))return reject(exchange,403,"仅允许内部服务调用");
        if(Boolean.TRUE.equals(exchange.getAttribute(FilterPolicy.WHITE_URL))) {
            if(path.equals("/api/employee/login")) {
                String ip=exchange.getRequest().getRemoteAddress()==null ? "unknown" :
                    exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
                return chain.filter(exchange.mutate().request(exchange.getRequest().mutate()
                    .headers(h -> {h.set("X-Gateway-Token",gatewayToken);h.set("X-Login-IP",ip);})
                    .build()).build());
            }
            return chain.filter(exchange);
        }
        List<String> headers=exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if(headers==null||headers.size()!=1||headers.get(0).trim().isEmpty())
            return reject(exchange,401,"请在Authorization请求头中提供token");
        String token=headers.get(0);
        // Only the authentication request is mapped to 503; downstream failures retain their own status.
        return employee.post().uri("/employee/checkToken").header(HttpHeaders.AUTHORIZATION,token)
            .retrieve().bodyToMono(Result.class).timeout(Duration.ofSeconds(5))
            .map(result -> result.status==200 && Boolean.TRUE.equals(result.data) ? 200 : 401)
            .defaultIfEmpty(503).onErrorReturn(503)
            .flatMap(status -> status==200 ? chain.filter(exchange) :
                reject(exchange,status,status==401 ? "登录已失效，请重新登录" : "登录校验服务暂不可用"));
    }
    private Mono<Void> reject(ServerWebExchange exchange,int code,String message) {
        log.info("authentication rejected method={} path={} status={}",exchange.getRequest().getMethod(),
            exchange.getRequest().getURI().getRawPath(),code);
        exchange.getResponse().setStatusCode(HttpStatus.valueOf(code));
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes=("{\"status\":"+code+",\"message\":\""+message+"\",\"data\":null}").getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }
}
