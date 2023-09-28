package lab.org.microservices.core.catalogue;

import lab.org.microservices.core.catalogue.persistence.repository.product.ProductRepository;
import lab.org.microservices.core.catalogue.persistence.entity.product.ProductEntity;
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
public class ProductPersistenceTest extends MongoDbTestBase {

    @Autowired
    private ProductRepository repository;

    private ProductEntity savedEntity;

    @BeforeEach
    void setupDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();

        ProductEntity entity = new ProductEntity(1, "n", 1);
        StepVerifier.create(repository.save(entity))
                .expectNextMatches(createdEntity -> {
                    savedEntity = createdEntity;
                    return areProductEqual(entity, savedEntity);
                })
                .verifyComplete();
    }

    @Test
    void create() {
        ProductEntity newEntity = new ProductEntity(2, "n", 2);

        StepVerifier.create(repository.save(newEntity))
                .expectNextMatches(
                        createdEntity -> newEntity.getProductId() == createdEntity.getProductId()
                )
                .verifyComplete();

        StepVerifier.create(repository.findById(newEntity.getId()))
                .expectNextMatches(foundEntity -> areProductEqual(newEntity, foundEntity))
                .verifyComplete();

        StepVerifier.create(repository.count()).expectNext(2L).verifyComplete();
    }

    @Test
    void update() {
        savedEntity.setName("n2");
        StepVerifier.create(repository.save(savedEntity))
                .expectNextMatches(updatedEntity -> updatedEntity.getName().equals("n2"))
                .verifyComplete();

        StepVerifier.create(repository.findById(savedEntity.getId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1
                                && foundEntity.getName().equals("n2"))
                .verifyComplete();

    }

    @Test
    void delete() {
        StepVerifier.create(repository.delete(savedEntity)).verifyComplete();
        StepVerifier.create(repository.existsById(savedEntity.getId())).expectNext(false).verifyComplete();
    }


    @Test
    void getByProductId() {
        StepVerifier.create(repository.findByProductId(savedEntity.getProductId()))
                .expectNextMatches(foundEntity -> areProductEqual(savedEntity, foundEntity))
                .verifyComplete();
    }

    @Test
    void duplicateError() {
        ProductEntity entity = new ProductEntity(savedEntity.getProductId(), "n", 1);
        StepVerifier.create(repository.save(entity)).expectError(DuplicateKeyException.class).verify();

    }

    @Test
    void optimisticLockError() {
        ProductEntity entity1 = repository.findById(savedEntity.getId()).block();
        ProductEntity entity2 = repository.findById(savedEntity.getId()).block();

        // Update the entity using the first entity object
        entity1.setName("n1");
        repository.save(entity1).block();

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        StepVerifier.create(repository.save(entity2)).expectError(OptimisticLockingFailureException.class).verify();

        // Get the updated entity from the database and verify its new sate
        StepVerifier.create(repository.findById(savedEntity.getId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1
                                && foundEntity.getName().equals("n1"))
                .verifyComplete();


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

    private boolean areProductEqual(ProductEntity expectedEntity, ProductEntity actualEntity) {
        boolean result =
                (expectedEntity.getId().equals(actualEntity.getId()))
                        && (expectedEntity.getVersion() == actualEntity.getVersion())
                        && (expectedEntity.getProductId() == actualEntity.getProductId())
                        && (expectedEntity.getName().equals(actualEntity.getName()))
                        && (expectedEntity.getWeight().equals(actualEntity.getWeight()));

        return result;
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
