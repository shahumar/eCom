package lab.org.microservices.core.order.domain;

import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import io.eventuate.tram.sagas.spring.orchestration.SagaOrchestratorConfiguration;
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration;
import lab.org.microservices.core.order.saga.cancelorder.CancelOrderSaga;
import lab.org.microservices.core.order.saga.createorder.CreateOrderSaga;
import lab.org.microservices.core.order.saga.reviseorder.ReviseOrderSaga;
import lab.org.microservices.core.order.sagaparticipants.AccountingServiceProxy;
import lab.org.microservices.core.order.sagaparticipants.ConsumerServiceProxy;
import lab.org.microservices.core.order.sagaparticipants.KitchenServiceProxy;
import lab.org.microservices.core.order.sagaparticipants.OrderServiceProxy;
import lab.org.util.common.CommonConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

@Configuration
@EnableJpaRepositories
@EnableAutoConfiguration
@Import({TramEventsPublisherConfiguration.class, SagaOrchestratorConfiguration.class, CommonConfiguration.class})
@ComponentScan
public class OrderServiceConfiguration {

    @Bean
    public OrderService orderService(SagaInstanceFactory sagaInstanceFactory,
                                     RestaurantRepository restaurantRepository,
                                     OrderRepository orderRepository,
                                     DomainEventPublisher domainEventPublisher,
                                     CreateOrderSaga createOrderSaga,
                                     CancelOrderSaga cancelOrderSaga,
                                     ReviseOrderSaga reviseOrderSaga,
                                     OrderDomainEventPublisher orderDomainEventPublisher,
                                     Optional<io.micrometer.core.instrument.MeterRegistry> meterRegistry) {
        return new OrderService(sagaInstanceFactory, orderRepository, domainEventPublisher, restaurantRepository,
                createOrderSaga, cancelOrderSaga, reviseOrderSaga, orderDomainEventPublisher, meterRegistry);
    }

    @Bean
    public CreateOrderSaga createOrderSaga(OrderServiceProxy orderService,
                                           ConsumerServiceProxy consumerService,
                                           KitchenServiceProxy kitchenServiceProxy,
                                           AccountingServiceProxy accountingService) {
        return new CreateOrderSaga(orderService, consumerService, kitchenServiceProxy, accountingService);
    }


    @Bean
    public CancelOrderSaga cancelOrderSaga() {
        return new CancelOrderSaga();
    }


    @Bean
    public ReviseOrderSaga reviseOrderSaga() {
        return new ReviseOrderSaga();
    }

    @Bean
    public KitchenServiceProxy kitchenServiceProxy() {
        return new KitchenServiceProxy();
    }

    @Bean
    public OrderServiceProxy orderServiceProxy() {
        return new OrderServiceProxy();
    }

    @Bean
    public ConsumerServiceProxy consumerServiceProxy() {
        return new ConsumerServiceProxy();
    }

    @Bean
    public AccountingServiceProxy accountingServiceProxy() {
        return new AccountingServiceProxy();
    }

    @Bean
    public OrderDomainEventPublisher orderAggregateEventPublisher(DomainEventPublisher eventPublisher) {
        return new OrderDomainEventPublisher(eventPublisher);
    }

    @Bean
    public MeterRegistryCustomizer meterRegistryCustomizer(@Value("${spring.application.name}") String serviceName) {
        return registry -> registry.config().commonTags("service", serviceName);
    }

}
