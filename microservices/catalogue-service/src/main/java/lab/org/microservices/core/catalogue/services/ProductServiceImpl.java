package lab.org.microservices.core.catalogue.services;

import lab.org.microservices.core.catalogue.persistence.repository.product.ProductRepository;
import lab.org.microservices.core.catalogue.persistence.entity.product.ProductEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;
import lab.org.api.core.product.ProductService;
import lab.org.api.core.product.Product;
import lab.org.util.http.ServiceUtil;
import lab.org.api.exceptions.InvalidInputException;
import lab.org.api.exceptions.NotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;

import static java.util.logging.Level.FINE;

@RestController
public class ProductServiceImpl implements ProductService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ServiceUtil serviceUtil;
    private final ProductRepository repository;
    private final ProductMapper mapper;

    private final Random randomGenerator = new Random();

    @Autowired
    public ProductServiceImpl(
            ProductRepository repository, ProductMapper mapper, ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<Product> createProduct(Product body) {
        if (body.getProductId() < 1) {
            throw new InvalidInputException("Invalid productId: " + body.getProductId());
        }
        ProductEntity entity = mapper.apiToEntity(body);
        return repository.save(entity)
                .log(LOG.getName(), FINE)
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Product Id: " + body.getProductId()))
                .map(mapper::entityToApi);

    }

    @Override
    public Flux<Product> listProducts() {
        return repository.findAll()
                .map(e -> mapper.entityToApi(e))
                .map(e -> setServiceAddress(e));
    }

    @Override
    public Mono<Product> getProduct(HttpHeaders headers, int productId, int delay, int faultPercent) {
        LOG.debug("/product return the found product for productId={}", productId);

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        LOG.info("Will get product info for id={}", productId);

        return repository.findByProductId(productId)
                .map(e -> throwErrorIfBadLuck(e, faultPercent))
                .delayElement(Duration.ofSeconds(delay))
                .switchIfEmpty(Mono.error(new NotFoundException("No product found for productId: " + productId)))
                .log(LOG.getName(), FINE)
                .map(e -> mapper.entityToApi(e))
                .map(e -> setServiceAddress(e));

    }

    private Product setServiceAddress(Product product) {
        product.setServiceAddress(serviceUtil.getServiceAddress());
        return product;
    }

    @Override
    public Mono<Void> deleteProduct(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId);

        return repository.findByProductId(productId).log(LOG.getName(), FINE).map(e -> repository.delete(e)).flatMap(e -> e);
    }

    private ProductEntity throwErrorIfBadLuck(ProductEntity entity, int faultPercent) {

        if (faultPercent == 0) {
            return entity;
        }

        int randomThreshold = getRandomNumber(1, 100);

        if (faultPercent < randomThreshold) {
            LOG.debug("We got lucky, no error occurred, {} < {}", faultPercent, randomThreshold);
        } else {
            LOG.debug("Bad luck, an error occurred, {} >= {}", faultPercent, randomThreshold);
            throw new RuntimeException("Something went wrong...");
        }

        return entity;
    }

    private int getRandomNumber(int min, int max) {

        if (max < min) {
            throw new IllegalArgumentException("Max must be greater than min");
        }

        return randomGenerator.nextInt((max - min) + 1) + min;
    }

}
