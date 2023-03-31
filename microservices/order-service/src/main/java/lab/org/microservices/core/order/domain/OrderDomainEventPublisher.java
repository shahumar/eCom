package lab.org.microservices.core.order.domain;

import io.eventuate.tram.events.aggregates.AbstractAggregateDomainEventPublisher;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import lab.org.api.core.order.events.OrderDomainEvent;

public class OrderDomainEventPublisher extends AbstractAggregateDomainEventPublisher<Order, OrderDomainEvent> {

    public OrderDomainEventPublisher(DomainEventPublisher eventPublisher) {
        super(eventPublisher, Order.class, Order::getId);
    }
}
