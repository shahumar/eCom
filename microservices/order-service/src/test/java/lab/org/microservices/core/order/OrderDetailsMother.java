package lab.org.microservices.core.order;

import lab.org.api.common.Money;
import lab.org.api.core.order.events.OrderDetails;
import lab.org.api.core.order.events.OrderLineItem;

import java.util.Collections;

public class OrderDetailsMother {

    static OrderDetails makeOrderDetails(long consumerId, long restaurantId, Money orderTotal) {
        return new OrderDetails(consumerId, restaurantId, Collections.singletonList(
                new OrderLineItem("samosas", "Samosas", new Money("2.50"), 3)),
                orderTotal);
    }
}
