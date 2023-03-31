package lab.org.microservices.core.order.web;

import lab.org.microservices.core.order.domain.OrderServiceConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@Import(OrderServiceConfiguration.class)
public class OrderWebConfiguration {
}
