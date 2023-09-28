package lab.org.microservices.core.catalogue;

import lab.org.microservices.core.catalogue.persistence.entity.category.CategoryEntity;
import lab.org.microservices.core.catalogue.persistence.repository.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import reactor.test.StepVerifier;

@DataMongoTest(
        excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class
)
public class CategoryPersistenceTest extends MongoDbTestBase {

    @Autowired
    CategoryRepository repository;

    CategoryEntity savedEntity;

    @BeforeEach
    void setupDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();

        var newEntity = new CategoryEntity(1, "category 1", null, "category-1");

        StepVerifier.create(repository.save(newEntity))
                .expectNextMatches( entity -> {
                    savedEntity = entity;
                    return areCategoryEqual(entity, newEntity);
                }).verifyComplete();
    }

    @Test
    void create() {
        var newEntity = new CategoryEntity(2, "parent 2", null, "parent-2");

        StepVerifier.create(repository.save(newEntity))
                .expectNextMatches(
                        entity -> entity.getCategoryId() == newEntity.getCategoryId()
                ).verifyComplete();

        StepVerifier.create(repository.findByCategoryId(2))
                .expectNextMatches(foundEntity -> {
                    return areCategoryEqual(foundEntity, newEntity);
                }).verifyComplete();

        StepVerifier.create(repository.count()).expectNext(2L).verifyComplete();
    }

    @Test
    void createWithParent() {
        var newEntity = new CategoryEntity(3, "Category 3", savedEntity.getId(), "category-3");

        StepVerifier.create(repository.save(newEntity))
                .expectNextMatches(
                        entity -> entity.getCategoryId() == newEntity.getCategoryId() && entity.getParentId().equals(savedEntity.getId())
                ).verifyComplete();

    }

    @Test
    void update() {
        savedEntity.setName("Category 1");
        StepVerifier.create(repository.save(savedEntity))
                .expectNextMatches(
                        entity -> entity.getName().equals("Category 1")
                ).verifyComplete();

        StepVerifier.create(repository.findByCategoryId(savedEntity.getCategoryId()))
                .expectNextMatches(entity -> entity.getVersion() == 1 && entity.getName().equals("Category 1"))
                .verifyComplete();
    }

    @Test
    void delete() {
        StepVerifier.create(repository.delete(savedEntity))
                .verifyComplete();
        StepVerifier.create(repository.existsById(savedEntity.getId()))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void dulicateShouldRaiseError() {
        CategoryEntity entity = new CategoryEntity(savedEntity.getCategoryId(), "category 1", null, "category-1");
        StepVerifier.create(repository.save(entity)).expectError(DuplicateKeyException.class).verify();

    }

    @Test
    void optimisticLockError() {
        var entity1 = repository.findByCategoryId(savedEntity.getCategoryId()).block();
        var entity2 = repository.findByCategoryId(savedEntity.getCategoryId()).block();

        entity2.setName("Cat 2");
        repository.save(entity2).block();

        StepVerifier.create(repository.save(entity1)).expectError(OptimisticLockingFailureException.class).verify();

        StepVerifier.create(repository.findByCategoryId(savedEntity.getCategoryId()))
                .expectNextMatches( entity -> {
                    return entity.getVersion() == 1 && entity.getName().equals("Cat 2");
                })
                .verifyComplete();
    }

    private boolean areCategoryEqual(CategoryEntity saved, CategoryEntity newEntity) {
        boolean result = (saved.getId().equals(newEntity.getId())
                && saved.getName().equals(newEntity.getName())
                && saved.getCategoryId() == newEntity.getCategoryId()
                && saved.getVersion().equals(newEntity.getVersion())
                && saved.getSlug().equals(newEntity.getSlug()));
        return result;
    }


}
