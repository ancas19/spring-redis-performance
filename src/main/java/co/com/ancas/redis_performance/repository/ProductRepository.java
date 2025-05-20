package co.com.ancas.redis_performance.repository;

import co.com.ancas.redis_performance.entity.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<ProductEntity,Long> {
}
