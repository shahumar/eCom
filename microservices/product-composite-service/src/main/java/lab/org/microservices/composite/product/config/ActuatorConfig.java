package lab.org.microservices.composite.product.config;


import lab.org.microservices.composite.product.services.ProductCompositeIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActuatorConfig {

    @Autowired
    ProductCompositeIntegration integration;

//    @Bean
//    ReactiveHealthContributor coreServices() {
//        final Map<String, ReactiveHealthIndicator> registry = new LinkedHashMap<>();
//        registry.put("product", () -> integration.getProductHealth());
//        registry.put("recommendation", () -> integration.getRecommendationHealth());
//        registry.put("review", () -> integration.getReviewHealth());
//        return CompositeReactiveHealthContributor.fromMap(registry);
//    }
}
