package lab.org.microservices.composite.product;

import lab.org.api.composite.product.ProductAggregate;
import lab.org.api.composite.product.RecommendationSummary;
import lab.org.api.composite.product.ReviewSummary;
import lab.org.microservices.composite.product.services.ProductCompositeIntegration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

import lab.org.api.core.product.Product;
import lab.org.api.core.recommendation.Recommendation;
import lab.org.api.core.review.Review;
import lab.org.api.exceptions.InvalidInputException;
import lab.org.api.exceptions.NotFoundException;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductCompositeServiceApplicationTests {

    public static final int PRODUCT_ID_OK = 1;
    public static final int PRODUCT_ID_NOT_FOUND = 2;
    public static final int PRODUCT_ID_INVALID = 3;

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ProductCompositeIntegration compositeIntegration;

    @BeforeEach
    public void setup() {
        when(compositeIntegration.getProduct(PRODUCT_ID_OK))
                .thenReturn(new Product(PRODUCT_ID_OK, "name", 1, "mock-address"));
        when(compositeIntegration.getRecommendations(PRODUCT_ID_OK))
                .thenReturn(singletonList(new Recommendation(PRODUCT_ID_OK, 1, "author", 1, "content", "mock address")));
        when(compositeIntegration.getReviews(PRODUCT_ID_OK))
                .thenReturn(singletonList(new Review(PRODUCT_ID_OK, 1, "author", "subject", "content", "mock address")));

        when(compositeIntegration.getProduct(PRODUCT_ID_NOT_FOUND))
                .thenThrow(new NotFoundException("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));

        when(compositeIntegration.getProduct(PRODUCT_ID_INVALID))
                .thenThrow(new InvalidInputException("INVALID: " + PRODUCT_ID_INVALID));
    }

    @Test
    void contextLoads() {
    }

    @Test
    void createCompositeProduct1() {
//        ProductAggregate compositeProduct = new ProductAggregate(1, "name", 1, null, null, null);
//        postAndVerifyProduct(compositeProduct, OK);
    }

    @Test
    void createCompositeProduct2(){
//        ProductAggregate compositeProduct = new ProductAggregate(
//                1,
//                "name",
//                1,
//                singletonList(new RecommendationSummary(1, "a", 1, "c")),
//                singletonList(new ReviewSummary(1, "a", "s", "c")),
//                null);
//        postAndVerifyProduct(compositeProduct, OK);
    }

    private void postAndVerifyProduct(ProductAggregate compositeProduct, HttpStatus expectedStatus) {
        webClient.post()
                .uri("/product-composite")
                .body(just(compositeProduct), ProductAggregate.class)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }

    @Test
    void deleteCompositeProduct() {
//        ProductAggregate compositeProduct = new ProductAggregate(1, "name", 1,
//                singletonList(new RecommendationSummary(1, "a", 1, "c")),
//                singletonList(new ReviewSummary(1, "a", "s", "c")), null);
//        postAndVerifyProduct(compositeProduct, OK);
//        deleteAndVerifyProduct(compositeProduct.getProductId(), OK);
//        deleteAndVerifyProduct(compositeProduct.getProductId(), OK);
    }

    private void deleteAndVerifyProduct(int productId, HttpStatus ok) {
        webClient.delete()
                .uri("/product-composite/" + productId)
                .exchange()
                .expectStatus().isEqualTo(ok);
    }

    @Test
    void getProductById() {

        getAndVerifyProduct(PRODUCT_ID_OK, OK)
                .jsonPath("$.productId").isEqualTo(PRODUCT_ID_OK)
                .jsonPath("$.recommendations.length()").isEqualTo(1)
                .jsonPath("$.reviews.length()").isEqualTo(1);

    }

    private WebTestClient.BodyContentSpec getAndVerifyProduct(int productIdOk, HttpStatus ok) {
        return webClient.get()
                .uri("/product-composite/" + productIdOk)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(ok)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();
    }

    @Test
    void getProductNotFound() {
        getAndVerifyProduct(PRODUCT_ID_NOT_FOUND, NOT_FOUND)
                .jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_NOT_FOUND)
                .jsonPath("$.message").isEqualTo("NOT FOUND: " + PRODUCT_ID_NOT_FOUND);

    }

    @Test
    void getProductInvalidInput() {

        getAndVerifyProduct(PRODUCT_ID_INVALID, UNPROCESSABLE_ENTITY)
                .jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_INVALID)
                .jsonPath("$.message").isEqualTo("INVALID: " + PRODUCT_ID_INVALID);
    }

}
