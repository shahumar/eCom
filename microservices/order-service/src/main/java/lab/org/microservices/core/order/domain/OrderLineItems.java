package lab.org.microservices.core.order.domain;


import lab.org.api.common.Money;
import lab.org.api.core.order.events.OrderLineItem;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Embeddable
public class OrderLineItems {

    @ElementCollection
    @CollectionTable(name = "order_line_items")
    private List<OrderLineItem> lineItems;

    private OrderLineItems() {
    }

    public OrderLineItems(List<OrderLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public List<OrderLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<OrderLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    OrderLineItem findOrderLineItem(String lineItemId) {
        return lineItems.stream().filter(li -> li.getMenuItemId().equals(lineItemId)).findFirst().get();
    }

    Money orderTotal() {
        return lineItems.stream().map(OrderLineItem::getTotal).reduce(Money.ZERO, Money::add);
    }

    void updateLineItems(OrderRevision orderRevision) {
        getLineItems().stream().forEach(li -> {
            Integer revised = orderRevision.getRevisedLineItemQuantities().get(li.getMenuItemId());
            li.setQuantity(revised);
        });
    }

    public LineItemQuantityChange lineItemQuantityChange(OrderRevision orderRevision) {
        Money currentOrderTotal = orderTotal();
        Money delta = changeToOrderTotal(orderRevision);
        Money newOrderTotal = currentOrderTotal.add(delta);
        return new LineItemQuantityChange(currentOrderTotal, newOrderTotal, delta);
    }

    Money changeToOrderTotal(OrderRevision orderRevision) {
        AtomicReference<Money> delta = new AtomicReference<>(Money.ZERO);
        orderRevision.getRevisedLineItemQuantities().forEach((lineItemId, newQuantity) -> {
            OrderLineItem lineItem = findOrderLineItem(lineItemId);
            delta.set(delta.get().add(lineItem.deltaForChangedQuantity(newQuantity)));
        });
        return delta.get();
    }
}
