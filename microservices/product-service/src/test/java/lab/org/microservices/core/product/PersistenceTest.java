package lab.org.microservices.core.product;

import lab.org.microservices.core.product.persistence.ProductEntity;
import lab.org.microservices.core.product.persistence.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.domain.Sort.Direction.ASC;

@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
public class PersistenceTest extends MongoDbTestBase {

    @Autowired
    private ProductRepository repository;

    private ProductEntity entity;

    @BeforeEach
    void setupDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();
        ProductEntity newEntity = new ProductEntity(1, "n", 1);
        StepVerifier.create(repository.save(newEntity))
                .expectNextMatches(createdEntity -> {
                    entity = createdEntity;
                    return areProductEqual(entity, newEntity);
                }).verifyComplete();
    }

    @Test
    void create() {
        ProductEntity newEntity = new ProductEntity(2, "n", 2);
        StepVerifier.create(repository.save(newEntity))
                .expectNextMatches(createdEntity -> newEntity.getProductId() == createdEntity.getProductId())
                .verifyComplete();

        StepVerifier.create(repository.findById(newEntity.getId()))
                .expectNextMatches(foundEntity -> areProductEqual(foundEntity, newEntity))
                .verifyComplete();
        StepVerifier.create(repository.count()).expectNext(2L).verifyComplete();
    }

    @Test
    void update() {
        entity.setName("n2");
        StepVerifier.create(repository.save(entity))
            .expectNextMatches(updatedEntity -> updatedEntity.getName().equals("n2"))
            .verifyComplete();

        StepVerifier.create(repository.findById(entity.getId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1 && foundEntity.getName().equals("n2")
                ).verifyComplete();

    }

    @Test
    void delete() {
        StepVerifier.create(repository.delete(entity)).verifyComplete();
        StepVerifier.create(repository.existsById(entity.getId())).expectNext(false).verifyComplete();
    }


    @Test
    void getByProductId() {
        StepVerifier.create(repository.findByProductId(entity.getProductId()))
                .expectNextMatches(foundEntity -> areProductEqual(foundEntity, entity))
                .verifyComplete();
    }

    @Test
    void duplicateError() {
        ProductEntity newEntity = new ProductEntity(entity.getProductId(), "n", 1);
        StepVerifier.create(repository.save(newEntity)).expectError(DuplicateKeyException.class).verify();

    }

    @Test
    void optimisticLockError() {
        ProductEntity entity1 = repository.findById(entity.getId()).block();
        ProductEntity entity2 = repository.findById(entity.getId()).block();

        entity1.setName("n1");
        repository.save(entity1).block();

        StepVerifier.create(repository.save(entity2)).expectError(
                OptimisticLockingFailureException.class).verify();

        StepVerifier.create(repository.findById(entity.getId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1
                        && foundEntity.getName().equals("n1")
                ).verifyComplete();


    }

//    @Test
//    void paging() {
//        repository.deleteAll();
//
//        List<ProductEntity> newProducts = rangeClosed(1001, 1010)
//                .mapToObj(i -> new ProductEntity(i, "name "+i, i))
//                .collect(Collectors.toList());
//        repository.saveAll(newProducts);
//
//        Pageable nextPage = PageRequest.of(0, 4, ASC, "productId");
//        nextPage = testNextPage(nextPage, "[1001, 1002, 1003, 1004]", true);
//        nextPage = testNextPage(nextPage, "[1005, 1006, 1007, 1008]", true);
//        nextPage = testNextPage(nextPage, "[1009, 1010]", false);
//    }

    private boolean areProductEqual(ProductEntity entity, ProductEntity newEntity) {
        return (entity.getId().equals(newEntity.getId()))
                && (entity.getVersion() == newEntity.getVersion())
                && (entity.getProductId() == newEntity.getProductId())
                && (entity.getName() == newEntity.getName())
                && (entity.getWeight() == newEntity.getWeight());
    }

//    private Pageable testNextPage(Pageable nextPage, String expectedId, boolean expectedNextPage) {
//        Page<ProductEntity> productPage = repository.findAll(nextPage);
//        assertEquals(expectedId, productPage.getContent().stream().map(p -> p.getProductId()).collect(Collectors.toList()).toString());
//        assertEquals(expectedNextPage, productPage.hasNext());
//        return productPage.nextPageable();
//    }
//
//    private void assertEqualsProduct(ProductEntity expectedEntity, ProductEntity actualEntity) {
//        assertEquals(expectedEntity.getId(), actualEntity.getId());
//        assertEquals(expectedEntity.getProductId(), actualEntity.getProductId());
//        assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
//        assertEquals(expectedEntity.getName(), actualEntity.getName());
//        assertEquals(expectedEntity.getWeight(), actualEntity.getWeight());
//    }
}
