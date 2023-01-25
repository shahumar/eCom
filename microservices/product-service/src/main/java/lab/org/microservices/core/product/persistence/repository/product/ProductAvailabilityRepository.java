package lab.org.microservices.core.product.persistence.repository.product;

import lab.org.microservices.core.product.persistence.entity.product.availability.ProductAvailability;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProductAvailabilityRepository extends ReactiveCrudRepository<ProductAvailability, String> {
}
