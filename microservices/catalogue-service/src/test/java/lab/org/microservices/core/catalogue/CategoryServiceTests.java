package lab.org.microservices.core.catalogue;


import lab.org.api.core.product.Category;
import lab.org.api.event.Event;
import lab.org.api.exceptions.InvalidInputException;
import lab.org.microservices.core.catalogue.persistence.repository.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.function.Consumer;

import static lab.org.api.event.Event.Type.CREATE;
import static lab.org.api.event.Event.Type.DELETE;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CategoryServiceTests extends MongoDbTestBase{

    @Autowired
    WebTestClient client;

    @Autowired
    CategoryRepository repository;

    @Autowired
    @Qualifier("categoryMessageProcessor")
    private Consumer<Event<Integer, Category>> messageProcessor;

    @BeforeEach
    void setupDB() {repository.deleteAll().block();}

    @Test
    void getCategory() {
        int categoryId = 1;
        assertNull(repository.findByCategoryId(categoryId).block());
        assertEquals(0L, (long) repository.count().block());
        sendCreateCategoryEvent(categoryId);

        assertNotNull(repository.findByCategoryId(categoryId).block());
        assertEquals(1, (long) repository.count().block());
        getAndVerifyCategory(categoryId, OK).jsonPath("$.categoryId").isEqualTo(categoryId);

    }

    @Test
    void listCategory() {
        assertEquals(0, repository.count().block());
        sendCreateCategoryEvent(1);
        sendCreateCategoryEvent(2);
        assertEquals(2, repository.count().block());
    }

    @Test
    void duplicateCategoryError() {
        int categoryId = 1;
        assertNull(repository.findByCategoryId(categoryId).block());
        sendCreateCategoryEvent(categoryId);
        assertNotNull(repository.findByCategoryId(categoryId).block());
        InvalidInputException thrown = assertThrows(
                InvalidInputException.class,
                () -> sendCreateCategoryEvent(categoryId),
                "Expected a InvalidInputException here!"
        );
        assertEquals("Duplicate category ID "+ categoryId, thrown.getMessage());
    }


    @Test
    void deleteCategory() {
        int categoryId = 1;
        sendCreateCategoryEvent(categoryId);
        assertNotNull(repository.findByCategoryId(categoryId).block());
        sendDeleteCategoryEvent(categoryId);
        assertNull(repository.findByCategoryId(categoryId).block());
    }

    @Test
    void getCategoryInvalidParameterString() {
        getAndVerifyCategory("/category/no-integer", BAD_REQUEST)
                .jsonPath("$.path").isEqualTo("/category/no-integer")
                .jsonPath("$.message").isEqualTo("Type mismatch.");

    }

    @Test
    void getCategoryNotFound() {
        int categoryId = 15;
        getAndVerifyCategory(categoryId, NOT_FOUND)
                .jsonPath("$.path").isEqualTo("/category/"+categoryId)
                .jsonPath("$.message").isEqualTo("Category not found for ID: " + categoryId);
    }

    @Test
    void getCategoryErrorIfInvalidParameter() {
        int categoryId = -1;
        getAndVerifyCategory(categoryId, UNPROCESSABLE_ENTITY)
                .jsonPath("$.path").isEqualTo("/category/"+categoryId)
                .jsonPath("$.message").isEqualTo("Invalid Category ID: " + categoryId);
    }

    private void sendDeleteCategoryEvent(int categoryId) {
        Event<Integer, Category> event = new Event<>(DELETE, categoryId, null);
        messageProcessor.accept(event);
    }

    private void sendCreateCategoryEvent(int categoryId) {

        Category category = new Category(categoryId, "Cat 1", "cat-1", null);
        Event<Integer, Category> event = new Event<>(CREATE, categoryId, category);
        messageProcessor.accept(event);

    }

    private WebTestClient.BodyContentSpec getAndVerifyCategory(int categoryId, HttpStatus status) {
        return getAndVerifyCategory("/category/"+categoryId, status);
    }

    private WebTestClient.BodyContentSpec getAndVerifyCategory(String path, HttpStatus status) {
        return client.get()
                .uri(path)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();
    }


}
