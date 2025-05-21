package co.com.ancas.redis_performance.service.util;

import co.com.ancas.redis_performance.entity.ProductEntity;
import co.com.ancas.redis_performance.repository.ProductRepository;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

//@Service
public class ProductCacheTemplate extends CacheTemplate<Long, ProductEntity> {

    private final ProductRepository productRepository;
    private  RMapReactive<Long, ProductEntity> productMap;

    public ProductCacheTemplate(ProductRepository productRepository, RedissonReactiveClient redissonReactiveClient) {
        this.productRepository = productRepository;
        this.productMap=redissonReactiveClient.getMap("product", new TypedJsonJacksonCodec(Long.class,ProductEntity.class));
    }

    @Override
    protected Mono<ProductEntity> getFromSource(Long aLong) {
        return productRepository.findById(aLong);
    }

    @Override
    protected Mono<ProductEntity> getFromCache(Long aLong) {
        return this.productMap.get(aLong);
    }

    @Override
    protected Mono<ProductEntity> updateSource(Long aLong, ProductEntity productEntity) {
        return this.productRepository.findById(aLong)
                .switchIfEmpty(Mono.error(new RuntimeException("Product not found")))
                .doOnNext(p-> productEntity.setId(p.getId()))
                .flatMap(productRepository::save);
    }

    @Override
    protected Mono<ProductEntity> updateCache(Long aLong, ProductEntity productEntity) {
        return this.productMap.fastPut(aLong, productEntity)
                .thenReturn(productEntity);
    }

    @Override
    protected Mono<Void> deleteFromSource(Long aLong) {
        return this.productRepository.deleteById(aLong);
    }

    @Override
    protected Mono<Void> deleteFromCache(Long aLong) {
        return this.productMap.fastRemove(aLong).then();
    }
}
