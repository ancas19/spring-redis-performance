package co.com.ancas.redis_performance.service.util;

import co.com.ancas.redis_performance.entity.ProductEntity;
import co.com.ancas.redis_performance.repository.ProductRepository;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductLocalCacheTemplate extends CacheTemplate<Long, ProductEntity> {

    private final ProductRepository productRepository;
    private RLocalCachedMap<Long, ProductEntity> productMap;

    public ProductLocalCacheTemplate(ProductRepository productRepository, RedissonClient redissonReactiveClient) {
        this.productRepository = productRepository;
        LocalCachedMapOptions<Long, ProductEntity> mapOptions = LocalCachedMapOptions.<Long,ProductEntity>defaults()
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR);
        this.productMap = redissonReactiveClient.getLocalCachedMap("product", new TypedJsonJacksonCodec(Long.class, ProductEntity.class), mapOptions);
    }

    @Override
    protected Mono<ProductEntity> getFromSource(Long aLong) {
        return this.productRepository.findById(aLong);
    }

    @Override
    protected Mono<ProductEntity> getFromCache(Long aLong) {
        return Mono.justOrEmpty(this.productMap.get(aLong));
    }

    @Override
    protected Mono<ProductEntity> updateSource(Long aLong, ProductEntity productEntity) {
        return this.productRepository.findById(aLong)
                .switchIfEmpty(Mono.error(new RuntimeException("Product not found")))
                .doOnNext(p -> productEntity.setId(p.getId()))
                .flatMap(productRepository::save);
    }

    @Override
    protected Mono<ProductEntity> updateCache(Long aLong, ProductEntity productEntity) {
        return Mono.create(sink->{
                    this.productMap.fastPutAsync(aLong, productEntity)
                            .thenAccept(b-> sink.success(productEntity))
                            .exceptionally(ex->{
                                sink.error(ex);
                                return null;
                            });
                })
                .thenReturn(productEntity);
    }

    @Override
    protected Mono<Void> deleteFromSource(Long aLong) {
        return this.productRepository.deleteById(aLong);
    }

    @Override
    protected Mono<Void> deleteFromCache(Long aLong) {
        return Mono.create(sink->{
                    this.productMap.fastRemoveAsync(aLong)
                            .thenAccept(b-> sink.success())
                            .exceptionally(ex->{
                                sink.error(ex);
                                return null;
                            });
                });
    }
}
