package co.com.ancas.redis_performance.service;

import co.com.ancas.redis_performance.entity.ProductEntity;
import co.com.ancas.redis_performance.service.util.CacheTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceV2 {
    private final CacheTemplate<Long, ProductEntity> productCacheTemplate;
    private final ProductVisitService productVisitService;

    public ProductServiceV2(CacheTemplate<Long, ProductEntity> productCacheTemplate, ProductVisitService productVisitService) {
        this.productCacheTemplate = productCacheTemplate;
        this.productVisitService = productVisitService;
    }


    public Mono<ProductEntity> getProduct(Long id) {
        return productCacheTemplate.get(id).doFirst(() -> this.productVisitService.addVisit(id));
    }

    public Mono<ProductEntity> updateProduct(Long id, Mono<ProductEntity> productMono) {
        return productMono
                .flatMap(data -> productCacheTemplate.update(id, data));
    }

    public Mono<Void> deleteProduct(Long id) {
        return productCacheTemplate.delete(id);
    }
}
