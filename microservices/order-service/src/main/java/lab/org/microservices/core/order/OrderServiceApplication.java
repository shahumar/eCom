package lab.org.microservices.core.order;

import lab.org.microservices.core.order.web.OrderWebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({OrderWebConfiguration.class, OrderCommandHandlersConfiguration.class, OrderServiceMessagingConfiguration.class,
        TramJdbcKafkaConfiguration.class, CommonSwaggerConfiguration.class})
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
