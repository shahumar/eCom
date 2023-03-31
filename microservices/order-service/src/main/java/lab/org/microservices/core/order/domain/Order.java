package lab.org.microservices.core.order.domain;


import io.eventuate.tram.events.common.DomainEvent;
import io.eventuate.tram.events.publisher.ResultWithEvents;
import lab.org.api.common.Money;
import lab.org.api.core.order.events.OrderCreatedEvent;
import lab.org.api.core.order.events.OrderDetails;
import lab.org.api.core.order.events.OrderLineItem;
import lab.org.api.core.order.events.OrderState;
import lab.org.util.http.UnsupportedStateTransitionException;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

import static lab.org.api.core.order.events.OrderState.*;

@Entity
@Table(name = "orders")
@Access(AccessType.FIELD)
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    private Long consumerId;
    private Long restaurantId;

    @Embedded
    private OrderLineItems orderLineItems;

    @Embedded
    private DeliveryInformation deliveryInformation;

    @Embedded
    private PaymentInformation paymentInformation;

    @Embedded
    private Money orderMinimum = new Money(Integer.MAX_VALUE);

    private Order(){}

    public Order(long consumerId, long restaurantId, List<OrderLineItem> orderLineItems) {
        this.consumerId = consumerId;
        this.restaurantId = restaurantId;
        this.orderLineItems = new OrderLineItems(orderLineItems);
        this.state = CREATE_PENDING;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<OrderLineItem> getLineItems() {
        return orderLineItems.getLineItems();
    }

    public static ResultWithEvents<Order> createOrder(long consumerId, long restaurantId, List<OrderLineItem> orderLineItems) {
        Order order = new Order(consumerId, restaurantId, orderLineItems);
        List<DomainEvent> events = Collections.singletonList(new OrderCreatedEvent(
                CREATE_PENDING, new OrderDetails(consumerId, restaurantId, orderLineItems, order.getOrderTotal())));
        return new ResultWithEvents<>(order, events);
    }

    private Money getOrderTotal() {
        return orderLineItems.orderTotal();
    }

    public List<DomainEvent> cancel() {
        return switch (state) {
            case AUTHORIZED -> {
                this.state = CANCEL_PENDING;
                yield Collections.emptyList();
            }
            default -> throw new UnsupportedStateTransitionException(state);
        };
    }

    public List<DomainEvent> undoPendingCancel() {
        return switch (state) {
            case CANCEL_PENDING -> {
                this.state = AUTHORIZED;
                yield Collections.emptyList();
            }
            default -> throw new UnsupportedStateTransitionException(state);
        };
    }

    public List<DomainEvent> noteCancelled() {
        return switch (state) {
            case CANCEL_PENDING -> {
                this.state = CANCELLED;
                yield Collections.singletonList(new OrderCancelled());
            }
            default -> throw new UnsupportedStateTransitionException(state);
        };
    }

    public List<DomainEvent> noteAuthorized() {
        switch (state) {
            case CREATE_PENDING:
                this.state = AUTHORIZED;
                return Collections.singletonList(new OrderAuthorized());
            default:
                throw new UnsupportedStateTransitionException(state);
        }

    }

    public List<DomainEvent> noteRejected() {
        switch (state) {
            case CREATE_PENDING:
                this.state = REJECTED;
                return Collections.singletonList(new OrderRejected());

            default:
                throw new UnsupportedStateTransitionException(state);
        }

    }

    public List<DomainEvent> noteReversingAuthorization() {
        return null;
    }

    public ResultWithEvents<LineItemQuantityChange> revise(OrderRevision orderRevision) {
        return switch (state) {
            case AUTHORIZED -> {
                LineItemQuantityChange change = orderLineItems.lineItemQuantityChange(orderRevision);
                if (change.newOrderTotal.isGreaterThanOrEqual(orderMinimum)) {
                    throw new OrderMinimumNotMetException();
                }
                this.state = REVISION_PENDING;
                yield  new ResultWithEvents<>(change, Collections.singletonList(
                        new OrderRevisionProposed(
                                orderRevision, change.currentOrderTotal, change.newOrderTotal)));
            }
            default -> throw new UnsupportedStateTransitionException(state);
        };
    }

    public List<DomainEvent> rejectRevision() {
        return switch (state) {
            case REVISION_PENDING -> {
                this.state = AUTHORIZED;
                yield Collections.emptyList();
            }
            default -> throw new UnsupportedStateTransitionException(state);
        };
    }

    public List<DomainEvent> confirmRevision(OrderRevision orderRevision) {
        return switch (state) {
            case REVISION_PENDING -> {
                LineItemQuantityChange licd = orderLineItems.lineItemQuantityChange(orderRevision);
                orderRevision.getDeliveryInformation().ifPresent(newDi -> this.deliveryInformation = newDi);
                if (!orderRevision.getRevisedLineItemQuantities().isEmpty()) {
                    orderLineItems.updateLineItems(orderRevision);
                }
                this.state = AUTHORIZED;
                yield Collections.singletonList(new OrderRevised(orderRevision, licd.currentOrderTotal, licd.newOrderTotal));
            }
            default -> throw new UnsupportedStateTransitionException(state);
        };
    }

    public Long getVersion() {
        return version;
    }

    public OrderState getState() {
        return state;
    }

    public long getRestaurantId() {
        return restaurantId;
    }


    public Long getConsumerId() {
        return consumerId;
    }

}
