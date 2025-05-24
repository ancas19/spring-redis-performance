package co.com.ancas.redis_performance.controller;

import co.com.ancas.redis_performance.service.BusinessMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products/metrics")
public class BusinessMetricsController {

    @Autowired
    private BusinessMetricsService businessMetricsService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<Long,Double>> getMetrics() {
        return businessMetricsService.topNProducts()
                .repeatWhen(l->Flux.interval(Duration.ofSeconds(3)));
    }
}
