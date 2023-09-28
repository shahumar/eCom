package lab.org.microservices.core.catalogue.persistence.repository.product;

import lab.org.microservices.core.catalogue.persistence.entity.product.availability.ProductAvailability;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProductAvailabilityRepository extends ReactiveCrudRepository<ProductAvailability, String> {
}
