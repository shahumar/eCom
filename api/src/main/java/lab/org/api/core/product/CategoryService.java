package lab.org.api.core.product;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public interface CategoryService {

    @GetMapping(value = "/category/{categoryId}", produces = APPLICATION_JSON_VALUE)
    Mono<Category> getCategory(@RequestHeader HttpHeaders headers, @PathVariable int categoryId);

    Mono<Category> createCategory(Category body);

    Mono<Void> deleteCategory(int categoryId );

    @GetMapping(value = "/category", produces = APPLICATION_JSON_VALUE)
    Flux<Category> listCategories();
}
