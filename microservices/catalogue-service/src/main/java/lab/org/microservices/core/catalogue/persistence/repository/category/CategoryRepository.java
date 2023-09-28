package lab.org.microservices.core.catalogue.persistence.repository.category;

import lab.org.microservices.core.catalogue.persistence.entity.category.CategoryEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CategoryRepository extends ReactiveCrudRepository<CategoryEntity, String> {

    Mono<CategoryEntity> findByCategoryId(int categoryId);
}
