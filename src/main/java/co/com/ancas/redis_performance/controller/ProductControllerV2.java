package co.com.ancas.redis_performance.controller;

import co.com.ancas.redis_performance.entity.ProductEntity;
import co.com.ancas.redis_performance.service.ProductServiceV1;
import co.com.ancas.redis_performance.service.ProductServiceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products/v2")
public class ProductControllerV2 {
    private final ProductServiceV2 productServiceV1;


    @GetMapping("/{id}")
    public Mono<ProductEntity> getProduct(@PathVariable("id") Long id){
        return productServiceV1.getProduct(id);
    }


    @PutMapping("/{id}")
    public Mono<ProductEntity> updateProduct(@PathVariable("id") Long id, @RequestBody Mono<ProductEntity> productMono){
        return productServiceV1.updateProduct(id, productMono);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteProduct(@PathVariable("id") Long id){
        return productServiceV1.deleteProduct(id);
    }
}
