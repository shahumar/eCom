package lab.org.microservices.core.catalogue.services;


import lab.org.api.core.product.Category;
import lab.org.api.core.product.CategoryService;
import lab.org.api.exceptions.InvalidInputException;
import lab.org.api.exceptions.NotFoundException;
import lab.org.microservices.core.catalogue.persistence.entity.category.CategoryEntity;
import lab.org.microservices.core.catalogue.persistence.repository.category.CategoryRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.logging.Level;


@Log4j2
@RestController
public class CategoryServiceImpl implements CategoryService {


    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    public CategoryServiceImpl(CategoryRepository repository, CategoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<Category> getCategory(HttpHeaders headers, int categoryId) {
        if (categoryId < 1) {
            throw new InvalidInputException("Invalid Category ID: " + categoryId);
        }
        return repository.findByCategoryId(categoryId)
                .switchIfEmpty(Mono.error(new NotFoundException("Category not found for ID: " + categoryId)))
                .log(log.getName(), Level.FINE)
                .map(mapper::entityToApi);

    }

    @Override
    public Mono<Category> createCategory(Category body) {
        if (body.getCategoryId() < 1) {
            throw new InvalidInputException("Invalid Category ID: " + body.getCategoryId());
        }
        CategoryEntity category = mapper.apiToEntity(body);
        if (category.getSlug() == null) {
            String slug = category.getName().toLowerCase().replaceAll(" ", "-") + "-" + category.getCategoryId();
            category.setSlug(slug);
        }

        if (category.getParentId() != null && repository.findByCategoryId(Integer.parseInt(category.getParentId())) == null) {
            throw new NotFoundException("Parent Category not found: " + category.getParentId());
        }
        return repository.save(category)
                .log(log.getName(), Level.FINE)
                .onErrorMap(DuplicateKeyException.class, ex -> new InvalidInputException("Duplicate category ID " + body.getCategoryId()))
                .map(mapper::entityToApi);

    }

    @Override
    public Mono<Void> deleteCategory(int categoryId) {
        if (categoryId < 1) {
            throw new InvalidInputException("Invalid Category ID: " + categoryId);
        }
        return repository.findByCategoryId(categoryId)
                .map(repository::delete)
                .log(log.getName(), Level.FINE)
                .flatMap(e -> e);
    }

    @Override
    public Flux<Category> listCategories() {
        return repository.findAll()
                .map(mapper::entityToApi);
    }
}
