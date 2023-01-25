package lab.org.microservices.core.product.persistence.repository.product;

import lab.org.microservices.core.product.persistence.entity.product.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;


public interface ProductRepository extends ReactiveCrudRepository<ProductEntity, String> {

    Mono<ProductEntity> findByProductId(int productId);
}
