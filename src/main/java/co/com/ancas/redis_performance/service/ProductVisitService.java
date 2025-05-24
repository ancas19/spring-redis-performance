package co.com.ancas.redis_performance.service;

import jakarta.annotation.PostConstruct;
import org.redisson.api.*;
import org.redisson.client.codec.LongCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class ProductVisitService {

    @Autowired
    private RedissonReactiveClient client;
    private Sinks.Many<Long> sink;

    public ProductVisitService() {
        this.sink = Sinks.many().unicast().onBackpressureBuffer();
    }

    @PostConstruct
    public void init() {
        this.sink.asFlux()
                .buffer(Duration.ofSeconds(3))
                .map(l -> l.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())))

                .flatMap(this::updateBatch)
                .subscribe();
    }

    private Mono<Void> updateBatch(Map<Long, Long> map) {
        RBatchReactive batch = this.client.createBatch(BatchOptions.defaults());
        String format = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        RScoredSortedSetReactive<Long> set = batch.getScoredSortedSet("product:visit:" + format, LongCodec.INSTANCE);
        return Flux.fromIterable(map.entrySet())
                .map(e -> set.addScore(e.getKey(), e.getValue()))
                .then(batch.execute())
                .then();
    }

    public void addVisit(Long id) {
        this.sink.tryEmitNext(id);
    }
}
