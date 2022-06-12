package lab.org.api.core.product;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public interface ProductService {

    @GetMapping(value = "/product/{productId}", produces = APPLICATION_JSON_VALUE)
    Mono<Product> getProduct(@PathVariable int productId);

    Mono<Product> createProduct(Product body);

    Mono<Void> deleteProduct(int productId);
}
