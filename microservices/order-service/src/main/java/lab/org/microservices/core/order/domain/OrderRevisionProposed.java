package lab.org.microservices.core.order.domain;

import io.eventuate.tram.events.common.DomainEvent;
import lab.org.api.common.Money;

public class OrderRevisionProposed implements DomainEvent {

    private final OrderRevision orderRevision;
    private final Money currentOrderTotal;
    private final Money newOrderTotal;

    public OrderRevisionProposed(OrderRevision orderRevision, Money currentOrderTotal, Money newOrderTotal) {
        this.orderRevision = orderRevision;
        this.currentOrderTotal = currentOrderTotal;
        this.newOrderTotal = newOrderTotal;
    }

    public OrderRevision getOrderRevision() {
        return orderRevision;
    }

    public Money getCurrentOrderTotal() {
        return currentOrderTotal;
    }

    public Money getNewOrderTotal() {
        return newOrderTotal;
    }
}
