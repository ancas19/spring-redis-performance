package co.com.ancas.redis_performance.service;

import co.com.ancas.redis_performance.entity.ProductEntity;
import co.com.ancas.redis_performance.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class DataSetupService implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final R2dbcEntityTemplate entityTemplate;
    @Value("classpath:sql/schema.sql")
    private Resource resource;

    @Override
    public void run(String... args) throws Exception {
        String sql = StreamUtils.copyToString(resource.getInputStream(), java.nio.charset.StandardCharsets.UTF_8);
        System.out.println(sql);

        Mono<Void> mono = Flux.range(1, 1000)
                .map(i -> new ProductEntity(null, "product" + i, ThreadLocalRandom.current().nextDouble(50, 200)))
                .collectList()
                .flatMapMany(productRepository::saveAll)
                .then();

        this.entityTemplate.getDatabaseClient()
                .sql(sql)
                .then()
                .then(mono)
                .doFinally(s-> System.out.println("Data setup completed: "+s))
                .block();

    }
}
