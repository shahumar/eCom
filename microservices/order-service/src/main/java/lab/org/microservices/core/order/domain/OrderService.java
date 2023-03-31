package lab.org.microservices.core.order.domain;

import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import lab.org.microservices.core.order.saga.cancelorder.CancelOrderSaga;
import lab.org.microservices.core.order.saga.createorder.CreateOrderSaga;
import lab.org.microservices.core.order.saga.reviseorder.ReviseOrderSaga;
import lab.org.microservices.core.order.sagaparticipants.ConsumerServiceProxy;
import lab.org.microservices.core.order.sagaparticipants.OrderServiceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.Optional;


@Transactional
public class OrderService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private SagaInstanceFactory sagaInstanceFactory;

    private OrderRepository orderRepository;

    private OrderDomainEventPublisher orderAggregateEventPublisher;

    private RestaurantRepository restaurantRepository;

    private CreateOrderSaga createOrderSaga;

    private CancelOrderSaga cancelOrderSaga;

    private ReviseOrderSaga reviseOrderSaga;

    private Optional<MeterRegistry> meterRegistry;

    public OrderService(SagaInstanceFactory sagaInstanceFactory,
                        OrderRepository orderRepository,
                        DomainEventPublisher eventPublisher,
                        RestaurantRepository restaurantRepository,
                        CreateOrderSaga createOrderSaga,
                        CancelOrderSaga cancelOrderSaga,
                        ReviseOrderSaga reviseOrderSaga,
                        OrderDomainEventPublisher orderAggregateEventPublisher,
                        Optional<MeterRegistry> meterRegistry) {

        this.sagaInstanceFactory = sagaInstanceFactory;
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.createOrderSaga = createOrderSaga;
        this.cancelOrderSaga = cancelOrderSaga;
        this.reviseOrderSaga = reviseOrderSaga;
        this.orderAggregateEventPublisher = orderAggregateEventPublisher;
        this.meterRegistry = meterRegistry;
    }

    @Bean
    public CreateOrderSaga createOrderSaga(OrderServiceProxy orderService, ConsumerServiceProxy consumerService, KitchenServiceProxy kitchenService, AccountingServiceProxy accountingService) {
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
