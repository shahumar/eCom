package lab.org.api.core.restaurant.orderservice;

import java.util.List;

public class RestaurantOrderDetails {

    private List<RestaurantOrderLineItem> lineItems;

    public List<RestaurantOrderLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<RestaurantOrderLineItem> lineItems) {
        this.lineItems = lineItems;
    }
}
