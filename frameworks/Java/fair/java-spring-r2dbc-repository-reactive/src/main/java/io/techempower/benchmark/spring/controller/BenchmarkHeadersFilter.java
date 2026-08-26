package io.techempower.benchmark.spring.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public final class BenchmarkHeadersFilter implements WebFilter {

    private volatile String date;

    @PostConstruct
    void init() {
        this.updateDate();
    }

    @Scheduled(fixedRate = 1000)
    void updateDate() {
        this.date = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var headers = exchange.getResponse().getHeaders();
        headers.set(HttpHeaders.SERVER, "Spring");
        headers.set(HttpHeaders.DATE, this.date);
        return chain.filter(exchange);
    }
}
