package lab.org.microservices.core.order.domain;

import lab.org.api.common.RevisedOrderLineItem;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrderRevision {

    private Optional<DeliveryInformation> deliveryInformation = Optional.empty();
    private List<RevisedOrderLineItem> revisedOrderLineItems;

    private OrderRevision() {
    }

    public OrderRevision(Optional<DeliveryInformation> deliveryInformation, List<RevisedOrderLineItem> revisedLineItemQuantities) {
        this.deliveryInformation = deliveryInformation;
        this.revisedOrderLineItems = revisedLineItemQuantities;
    }

    public void setDeliveryInformation(Optional<DeliveryInformation> deliveryInformation) {
        this.deliveryInformation = deliveryInformation;
    }

    public void setRevisedOrderLineItems(List<RevisedOrderLineItem> revisedLineItemQuantities) {
        this.revisedOrderLineItems = revisedLineItemQuantities;
    }

    public Optional<DeliveryInformation> getDeliveryInformation() {
        return deliveryInformation;
    }


    public List<RevisedOrderLineItem> getRevisedOrderLineItems() {
        return revisedOrderLineItems;
    }
}
