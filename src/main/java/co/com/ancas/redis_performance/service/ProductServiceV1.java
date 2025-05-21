package co.com.ancas.redis_performance.service;

import co.com.ancas.redis_performance.entity.ProductEntity;
import co.com.ancas.redis_performance.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductServiceV1 {

    private final ProductRepository productRepository;


    public Mono<ProductEntity> getProduct(Long id){
        return productRepository.findById(id);
    }

    public Mono<ProductEntity> updateProduct(Long id, Mono<ProductEntity> productMono){
        return this.productRepository.findById(id)
                .flatMap(p->productMono.doOnNext(pr->pr.setId(p.getId())))
                .flatMap(productRepository::save);
    }
}
