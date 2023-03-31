package lab.org.microservices.core.order.saga.createorder;

import lab.org.api.core.account.AuthorizeCommand;
import lab.org.api.core.consumer.ValidateOrderByConsumer;
import lab.org.api.core.kichenservice.*;
import lab.org.api.core.order.events.OrderDetails;
import lab.org.api.core.order.events.OrderLineItem;
import lab.org.microservices.core.order.sagaparticipants.ApproveOrderCommand;
import lab.org.microservices.core.order.sagaparticipants.RejectOrderCommand;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class CreateOrderSagaState {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Long orderId;

    private OrderDetails orderDetails;
    private long ticketId;

    public Long getOrderId() {
        return orderId;
    }

    private CreateOrderSagaState() {
    }

    public CreateOrderSagaState(Long orderId, OrderDetails orderDetails) {
        this.orderId = orderId;
        this.orderDetails = orderDetails;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }

    public long getTicketId() {
        return ticketId;
    }

    CreateTicket makeCreateTicketCommand() {
        return new CreateTicket(getOrderDetails().getRestaurantId(), getOrderId(), makeTicketDetails(getOrderDetails()));
    }

    private TicketDetails makeTicketDetails(OrderDetails orderDetails) {
        // TODO FIXME
        return new TicketDetails(makeTicketLineItems(orderDetails.getLineItems()));
    }

    private List<TicketLineItem> makeTicketLineItems(List<OrderLineItem> lineItems) {
        return lineItems.stream().map(this::makeTicketLineItem).collect(Collectors.toList());
    }

    private TicketLineItem makeTicketLineItem(OrderLineItem orderLineItem) {
        return new TicketLineItem(orderLineItem.getMenuItemId(), orderLineItem.getName(), orderLineItem.getQuantity());
    }

    void handleCreateTicketReply(CreateTicketReply reply) {
        logger.debug("getTicketId {}", reply.getTicketId());
        setTicketId(reply.getTicketId());
    }

    CancelCreateTicket makeCancelCreateTicketCommand() {
        return new CancelCreateTicket(getOrderId());
    }

    RejectOrderCommand makeRejectOrderCommand() {
        return new RejectOrderCommand(getOrderId());
    }

    ValidateOrderByConsumer makeValidateOrderByConsumerCommand() {
        ValidateOrderByConsumer x = new ValidateOrderByConsumer();
        x.setConsumerId(getOrderDetails().getConsumerId());
        x.setOrderId(getOrderId());
        x.setOrderTotal(getOrderDetails().getOrderTotal());
        return x;
    }

    AuthorizeCommand makeAuthorizeCommand() {
        return new AuthorizeCommand().withConsumerId(getOrderDetails().getConsumerId()).withOrderId(getOrderId()).withOrderTotal(getOrderDetails().getOrderTotal().asString());
    }

    ApproveOrderCommand makeApproveOrderCommand() {
        return new ApproveOrderCommand(getOrderId());
    }

    ConfirmCreateTicket makeConfirmCreateTicketCommand() {
        return new ConfirmCreateTicket(getTicketId());

    }
}
