package lab.org.microservices.core.product;

import lab.org.api.core.product.Product;
import lab.org.api.event.Event;
import lab.org.api.exceptions.InvalidInputException;
import lab.org.microservices.core.product.persistence.ProductEntity;
import lab.org.microservices.core.product.persistence.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static lab.org.api.event.Event.Type.CREATE;
import static lab.org.api.event.Event.Type.DELETE;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
        "eureka.client.enabled=false",
        "spring.cloud.config.enabled=false"})
class ProductServiceApplicationTests extends MongoDbTestBase {


    @Autowired
    private WebTestClient client;

    @Autowired
    private ProductRepository repository;

    @Autowired
    @Qualifier("messageProcessor")
    private Consumer<Event<Integer, Product>> messageProcessor;

    @BeforeEach
    void setupDb() {
        repository.deleteAll().block();
    }


    @Test
    void getProductById() {

        int productId = 1;
        assertNull(repository.findByProductId(productId).block());
        assertEquals(0, (long) repository.count().block());

        sendCreateProductEvent(productId);
        assertNotNull(repository.findByProductId(productId).block());
        assertEquals(1, (long)repository.count().block());
        getAndVerifyProduct(productId, OK).jsonPath("$.productId").isEqualTo(productId);

    }

    @Test
    void duplicateError() {
        int productId = 1;
        assertNull(repository.findByProductId(productId).block());
        sendCreateProductEvent(productId);
        assertNotNull(repository.findByProductId(productId).block());
        InvalidInputException thrown = assertThrows(
                InvalidInputException.class,
                () -> sendCreateProductEvent(productId),
                "Expected a InvalidInputException here!");
        assertEquals("Duplicate key, Product Id: " + productId, thrown.getMessage());
    }

    @Test
    void deleteProduct() {
        int productId = 1;
        sendCreateProductEvent(productId);
        assertNotNull(repository.findByProductId(productId).block());
        sendDeleteProductEvent(productId);
        assertNull(repository.findByProductId(productId).block());
        sendDeleteProductEvent(productId);
    }

    @Test
    void getProductInvalidParameterString() {
        getAndVerifyProduct("/no-integer", BAD_REQUEST)
                .jsonPath("$.path").isEqualTo("/product/no-integer")
                .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    void getProductNotFound() {

        int productIdNotFound = 13;
        getAndVerifyProduct(productIdNotFound, NOT_FOUND)
                .jsonPath("$.path").isEqualTo("/product/"+productIdNotFound)
                .jsonPath("$.message").isEqualTo("No product found for productId: " + productIdNotFound);

    }

    @Test
    void getProductInvalidParameterNegativeValue() {

        int productIdInvalid = -1;
        getAndVerifyProduct(productIdInvalid, UNPROCESSABLE_ENTITY)
                .jsonPath("$.path").isEqualTo("/product/"+productIdInvalid)
                .jsonPath("$.message").isEqualTo("Invalid productId: "+productIdInvalid);
    }


    private void sendDeleteProductEvent(int productId) {
        Event<Integer, Product> event = new Event<>(DELETE, productId, null);
        messageProcessor.accept(event);
    }

    private void sendCreateProductEvent(int productId) {
        Product product = new Product(productId, "Name " + productId, productId, "SA");
        Event<Integer, Product> event = new Event<>(CREATE, productId, product);
        messageProcessor.accept(event);
    }


    private WebTestClient.BodyContentSpec deleteAndVerifyProduct(int productId, HttpStatus ok) {
        return client.delete()
                .uri("/product/"+productId)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(ok)
                .expectBody();
    }

    private WebTestClient.BodyContentSpec getAndVerifyProduct(String productPath, HttpStatus expectedStatus) {
        return client.get()
                .uri("/product"+productPath)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();
    }

    private WebTestClient.BodyContentSpec getAndVerifyProduct(int productId, HttpStatus status) {
        return getAndVerifyProduct("/"+productId, status);

    }

    private WebTestClient.BodyContentSpec postAndVerifyProduct(int productId, HttpStatus status) {
        Product newProduct = new Product(productId, "Name "+productId, productId, "SA");

        return client.post()
                .uri("/product")
                .body(just(newProduct), Product.class)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();

    }




}
