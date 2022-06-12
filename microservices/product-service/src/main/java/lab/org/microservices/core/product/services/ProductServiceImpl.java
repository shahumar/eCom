package lab.org.microservices.core.product.services;

import lab.org.microservices.core.product.persistence.ProductEntity;
import lab.org.microservices.core.product.persistence.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import lab.org.api.core.product.ProductService;
import lab.org.api.core.product.Product;
import lab.org.util.http.ServiceUtil;
import lab.org.api.exceptions.InvalidInputException;
import lab.org.api.exceptions.NotFoundException;
import reactor.core.publisher.Mono;

import java.util.logging.Level;

@RestController
public class ProductServiceImpl implements ProductService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ServiceUtil serviceUtil;
    private final ProductRepository repository;
    private final ProductMapper mapper;

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
        Mono<Product> newEntity = repository.save(entity)
                .log(LOG.getName(), Level.FINE)
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Product Id: " + body.getProductId()))
                .map(e -> mapper.entityToApi(e));
        return newEntity;

    }

    @Override
    public Mono<Product> getProduct(int productId) {
        LOG.debug("/product return the found product for productId={}", productId);

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        LOG.info("Will get product info for id={}", productId);
        return repository.findByProductId(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("No product found for productId: " +productId)))
                .log(LOG.getName(), Level.FINE)
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

        return repository.findByProductId(productId).log(LOG.getName(), Level.FINE).map(e -> repository.delete(e)).flatMap(e -> e);
    }

}
