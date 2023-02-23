package lab.org.microservices.core.product;

import lab.org.microservices.core.product.persistence.repository.product.ProductAvailabilityRepository;
import lab.org.microservices.core.product.persistence.repository.product.ProductDescriptionRepository;
import lab.org.microservices.core.product.persistence.repository.product.ProductRepository;
import lab.org.microservices.core.product.persistence.entity.product.ProductEntity;
import lab.org.microservices.core.product.persistence.entity.product.availability.ProductAvailability;
import lab.org.microservices.core.product.persistence.entity.product.description.ProductDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.index.ReactiveIndexOperations;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.logging.Level;

@SpringBootApplication
@ComponentScan("lab.org")
public class ProductServiceApplication implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceApplication.class);

    @Autowired
    ProductRepository repository;
    @Autowired
    ProductDescriptionRepository descriptionRepository;
    @Autowired
    ProductAvailabilityRepository availabilityRepository;


    public static void main(String[] args) {

        ConfigurableApplicationContext ctx = SpringApplication.run(ProductServiceApplication.class, args);
        String mongodDbHost = ctx.getEnvironment().getProperty("spring.data.mongodb.host");
        String mongodDbPort = ctx.getEnvironment().getProperty("spring.data.mongodb.port");
        LOG.info("Connected to MongoDb: " + mongodDbHost + ":" + mongodDbPort);
    }

    @Autowired
    ReactiveMongoOperations mongoTemplate;

    @EventListener(ContextRefreshedEvent.class)
    public void initIndicesAfterStartup() {

        MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongoTemplate.getConverter().getMappingContext();
        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);

        ReactiveIndexOperations indexOps = mongoTemplate.indexOps(ProductEntity.class);
        resolver.resolveIndexFor(ProductEntity.class).forEach(e -> indexOps.ensureIndex(e).block());
    }

    @Override
    public void run(String... args) throws Exception {
//        System.out.println("Insert product when start server");
//        Random rand = new Random();
//        ProductDescription description = new ProductDescription();
//        description.setMetaTagDescription("how are you");
//        description.setProductExternalDl("external DL");
//        description.setSeUrl("http://test.lab.com");
//        description.setMetaTagTitle("Example");
//        descriptionRepository.save(description).block();
//
//        ProductAvailability availability = new ProductAvailability();
//        availability.setAvailable(true);
//        availability.setDateAvailable(LocalDateTime.now());
//        availability.setOwner("shah");
//        availability.setRegion("Dubai");
//        availability.setStatus(true);
//        availability.setIsAlwaysFreeShipping(false);
//        availabilityRepository.save(availability).block();
//
//
////        ProductEntity entity = new ProductEntity();
////        entity.setAvailable(true);
////        entity.setProductId(9877);
////        entity.setMerchantId(1);
////        entity.setModifiedBy("xyz");
////        entity.setProductIsFree(false);
////        entity.setName("Initial product");
////        entity.setWeight(BigDecimal.ONE);
////        entity.setRefSku("123xyz123");
////        entity.setSku(rand.nextInt() + "");
////        entity.getDescriptions().add(description);
////        entity.getAvailabilities().add(availability);
////        repository.save(entity)
////                .log("test te the log", Level.FINE)
////                .block();


        System.out.println(
                "Row inserted into documents"
        );
    }
}
