package lab.org.microservices.core.product.persistence.repository.product;

import lab.org.microservices.core.product.persistence.entity.product.description.ProductDescription;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProductDescriptionRepository extends ReactiveCrudRepository<ProductDescription, String> {
}
