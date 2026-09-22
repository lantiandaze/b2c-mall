package com.b2cmall.gateway.filter;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/** Logs routed requests; this demonstration filter does not implement authentication. */
@Component
public class LoggingFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long started = System.nanoTime();
        String path = exchange.getRequest().getURI().getRawPath();
        return Mono.defer(() -> chain.filter(exchange)).doFinally(signal -> log.info(
                "{} {} status={} durationMs={} signal={}",
                exchange.getRequest().getMethod(), path,
                exchange.getResponse().getStatusCode()==null ? "unset" : exchange.getResponse().getStatusCode().value(),
                TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started), signal));
    }

    @Override
    public int getOrder() {
        return FilterPolicy.LOG_ORDER;
    }
}
