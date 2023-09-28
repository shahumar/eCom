package lab.org.microservices.core.catalogue.config;

import lab.org.api.core.product.Category;
import lab.org.api.core.product.CategoryService;
import lab.org.api.core.product.Product;
import lab.org.api.core.product.ProductService;
import lab.org.api.event.Event;
import lab.org.api.exceptions.EventProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

import java.util.function.Consumer;

@Configuration
@EnableReactiveMongoAuditing
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final ProductService productService;
    private final CategoryService categoryService;

    @Autowired
    public MessageProcessorConfig(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @Bean
    public Consumer<Event<Integer, Category>> categoryMessageProcessor() {
        return event -> {
            LOG.info("process message created at {}...{}", event.getEventCreatedAt(), event.getEventType());
            switch (event.getEventType()) {
                case CREATE:
                    Category category = event.getData();
                    LOG.info("Create category with ID: {}", category.getCategoryId());
                    categoryService.createCategory(category).block();
                    break;
                case DELETE:
                    int categoryId = event.getKey();
                    LOG.info("Delete Category with ID {}", categoryId);
                    categoryService.deleteCategory(categoryId).block();
                    break;
                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected CREATE or DELETE";
                    LOG.warn(errorMessage);
                    throw new EventProcessingException(errorMessage);
            }
            LOG.info("Processing done");
        };
    }

    @Bean
    public Consumer<Event<Integer, Product>> messageProcessor() {
        return event -> {
            LOG.info("Process message created at {}...{}", event.getEventCreatedAt(), event.getEventType());
            switch (event.getEventType()) {

                case CREATE:
                    Product product = event.getData();
                    LOG.info("Create product with ID: {}", product.getProductId());
                    productService.createProduct(product).block();
                    break;

                case DELETE:
                    int productId = event.getKey();
                    LOG.info("Delete recommendations with ProductID: {}", productId);
                    productService.deleteProduct(productId).block();
                    break;

                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                    LOG.warn(errorMessage);
                    throw new EventProcessingException(errorMessage);
            }

            LOG.info("Message processing done!");
        };
    }
}
