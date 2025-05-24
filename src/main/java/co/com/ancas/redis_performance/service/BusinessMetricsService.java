package co.com.ancas.redis_performance.service;

import co.com.ancas.redis_performance.entity.ProductEntity;
import co.com.ancas.redis_performance.repository.ProductRepository;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.LongCodec;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ofPattern;

@Service
public class BusinessMetricsService {

    @Autowired
    private RedissonReactiveClient client;

    public Mono<Map<Long,Double>> topNProducts(){
        String format = ofPattern("yyyyMMdd").format(LocalDate.now());
        RScoredSortedSetReactive<Long> set = client.getScoredSortedSet("product:visit:" + format, LongCodec.INSTANCE);
        return set.entryRangeReversed(0,2)//List of scored entry
                .map(listSet->listSet.stream().collect(
                        Collectors.toMap(
                                ScoredEntry::getValue,
                                ScoredEntry::getScore,
                                (a,b)->a,
                                LinkedHashMap::new
                )));

    }
}
