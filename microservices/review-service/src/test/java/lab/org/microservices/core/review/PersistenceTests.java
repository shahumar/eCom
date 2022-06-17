package lab.org.microservices.core.review;

import lab.org.microservices.core.review.persistence.ReviewEntity;
import lab.org.microservices.core.review.persistence.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@DataJpaTest(properties = {"spring.jpa.hibernate.ddl-auto=update", "spring.cloud.config.enabled=false"})
@Transactional(propagation = NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = NONE)
public class PersistenceTests extends MySqlTestBase {

    @Autowired
    private ReviewRepository reviewRepository;

    private ReviewEntity reviewEntity;

    @BeforeEach
    void setupDB() {
        reviewRepository.deleteAll();
        ReviewEntity entity = new ReviewEntity(1,2,"a", "s", "c");
        reviewEntity = reviewRepository.save(entity);
        assertEqualsReview(entity, reviewEntity);
    }

    @Test
    void create() {
        ReviewEntity newEntity = new ReviewEntity(1, 3, "a", "s", "c");
        reviewRepository.save(newEntity);

        ReviewEntity foundEntity = reviewRepository.findById(newEntity.getId()).get();
        assertEqualsReview(newEntity, foundEntity);
        assertEquals(2, reviewRepository.count());
    }

    @Test
    void update() {
        reviewEntity.setAuthor("a2");
        reviewRepository.save(reviewEntity);

        ReviewEntity foundEntity = reviewRepository.findById(reviewEntity.getId()).get();
        assertEquals(1, (long)foundEntity.getVersion());
        assertEquals("a2", foundEntity.getAuthor());
    }

    @Test
    void delete() {
        reviewRepository.delete(reviewEntity);
        assertFalse(reviewRepository.existsById(reviewEntity.getId()));
    }

    @Test
    void getByProductId() {
        List<ReviewEntity> entityList = reviewRepository.findByProductId(reviewEntity.getProductId());
        assertThat(entityList, hasSize(1));
        assertEqualsReview(reviewEntity, entityList.get(0));
    }

    @Test
    void duplicateError() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            ReviewEntity entity = new ReviewEntity(1,2,"a", "s", "c");
            reviewRepository.save(entity);
        });
    }

    @Test
    void optimisticLockError() {
        ReviewEntity entity1 = reviewRepository.findById(reviewEntity.getId()).get();
        ReviewEntity entity2 = reviewRepository.findById(reviewEntity.getId()).get();
        entity1.setAuthor("a1");
        reviewRepository.save(entity1);
        assertThrows(OptimisticLockingFailureException.class, () -> {
           entity2.setAuthor("a2");
           reviewRepository.save(entity2);
        });

        ReviewEntity updatedEntity = reviewRepository.findById(reviewEntity.getId()).get();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("a1", updatedEntity.getAuthor());
    }

    private void assertEqualsReview(ReviewEntity entity, ReviewEntity reviewEntity) {
        assertEquals(entity.getId(), reviewEntity.getId());
        assertEquals(entity.getVersion(), reviewEntity.getVersion());
        assertEquals(entity.getProductId(), reviewEntity.getProductId());
        assertEquals(entity.getReviewId(), reviewEntity.getReviewId());
        assertEquals(entity.getAuthor(), reviewEntity.getAuthor());
        assertEquals(entity.getSubject(), reviewEntity.getSubject());
        assertEquals(entity.getContent(), reviewEntity.getContent());
    }

}
