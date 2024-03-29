package lab.org.api.core.product;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public interface ProductService {

    @GetMapping(value = "/product/{productId}", produces = APPLICATION_JSON_VALUE)
    Mono<Product> getProduct(
            @RequestHeader HttpHeaders headers,
            @PathVariable int productId,
            @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
            @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent);

    Mono<Product> createProduct(Product body);

    Mono<Void> deleteProduct(int productId);

    @GetMapping(value = "/product/", produces = APPLICATION_JSON_VALUE)
    Flux<Product> listProducts();
}
