package lab.org.api.core.restaurant.orderservice;

import io.eventuate.tram.commands.common.Command;

public class BeginCancelRestaurantOrderCommand implements Command {

    private long restaurantId;
    private long orderId;

    private BeginCancelRestaurantOrderCommand() {
    }

    public BeginCancelRestaurantOrderCommand(Long orderId, long orderId1) {
        this.restaurantId = restaurantId;
        this.orderId = orderId;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
}
