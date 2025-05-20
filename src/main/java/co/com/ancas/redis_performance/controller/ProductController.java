package co.com.ancas.redis_performance.controller;

import co.com.ancas.redis_performance.entity.ProductEntity;
import co.com.ancas.redis_performance.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;


    @GetMapping("/{id}")
    public Mono<ProductEntity> getProduct(@PathVariable("id") Long id){
        return productService.getProduct(id);
    }


    @PutMapping("/{id}")
    public Mono<ProductEntity> updateProduct(@PathVariable("id") Long id, @RequestBody Mono<ProductEntity> productMono){
        return productService.updateProduct(id, productMono);
    }
}
