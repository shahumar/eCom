package lab.org.microservices.composite.category;


import lab.org.api.core.product.Category;
import lab.org.api.exceptions.InvalidInputException;
import lab.org.api.exceptions.NotFoundException;
import lab.org.microservices.composite.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {TestSecurityConfig.class},
        properties = {
                "spring.security.oauth2.resourceserver.jwt.issuer-uri=",
                "spring.main.allow-bean-definition-overriding=true"})
public class CategoryCompositeServiceTests {

    private static final int CATEGORY_ID_OK = 1;
    private static final int CATEGORY_ID_NOT_FOUND = 2;
    private static final int CATEGORY_ID_INVALID = 3;


    @Autowired
    WebTestClient client;

    @MockBean
    private CategoryCompositeIntegration integration;

    @BeforeEach
    void setup() {
        when(integration.getCategory(any(), eq(CATEGORY_ID_OK)))
                .thenReturn(Mono.just(new Category(CATEGORY_ID_OK, "Cat 1", "cat-1-"+CATEGORY_ID_OK, null)));
        when(integration.getCategory(any(), eq(CATEGORY_ID_NOT_FOUND)))
                .thenThrow(new NotFoundException("NOT FOUND: " + CATEGORY_ID_NOT_FOUND));
        when(integration.getCategory(any(), eq(CATEGORY_ID_INVALID)))
                .thenThrow(new InvalidInputException("INVALID: " + CATEGORY_ID_INVALID));
        when(integration.listCategories())
                .thenReturn(Flux.fromIterable(singletonList(new Category(CATEGORY_ID_OK, "CAT !", null, null))));

    }


    @Test
    void getCategoryById() {
        getAndVerifyCategory(CATEGORY_ID_OK, OK)
                .jsonPath("$.categoryId").isEqualTo(CATEGORY_ID_OK)
                .jsonPath("$.slug").isEqualTo("cat-1-"+CATEGORY_ID_OK);
    }

    @Test
    void categoryNotFound() {
        getAndVerifyCategory(CATEGORY_ID_NOT_FOUND, NOT_FOUND)
                .jsonPath("$.path").isEqualTo("/api/category/" + CATEGORY_ID_NOT_FOUND)
                .jsonPath("$.message").isEqualTo("NOT FOUND: " + CATEGORY_ID_NOT_FOUND);
    }

    @Test
    void invalidCategory() {
        getAndVerifyCategory(CATEGORY_ID_INVALID, UNPROCESSABLE_ENTITY)
                .jsonPath("$.path").isEqualTo("/api/category/" + CATEGORY_ID_INVALID)
                .jsonPath("$.message").isEqualTo("INVALID: " + CATEGORY_ID_INVALID);
    }

    @Test
    void listCategories() {
        var categories = client.get()
                .uri("/api/category")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(OK)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1);

    }

    private WebTestClient.BodyContentSpec getAndVerifyCategory(int categoryId, HttpStatus status) {
        return client.get()
                .uri("/api/category/"+ categoryId)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();
    }
}
