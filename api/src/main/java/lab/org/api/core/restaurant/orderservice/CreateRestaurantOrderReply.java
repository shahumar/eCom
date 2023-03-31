package lab.org.api.core.restaurant.orderservice;

public class CreateRestaurantOrderReply {

    private long restaurantOrderId;

    private CreateRestaurantOrderReply() {
    }

    public void setRestaurantOrderId(long restaurantOrderId) {
        this.restaurantOrderId = restaurantOrderId;
    }

    public CreateRestaurantOrderReply(long restaurantOrderId) {

        this.restaurantOrderId = restaurantOrderId;
    }

    public long getRestaurantOrderId() {
        return restaurantOrderId;
    }
}
