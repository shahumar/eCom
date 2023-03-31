package lab.org.api.core.restaurant.orderservice;

import io.eventuate.tram.commands.common.Command;

public class UndoBeginCancelRestaurantOrderCommand implements Command {

    private long restaurantId;
    private long orderId;

    private UndoBeginCancelRestaurantOrderCommand() {
    }

    public UndoBeginCancelRestaurantOrderCommand(long restaurantId, Long orderId) {
        this.restaurantId = restaurantId;
        this.orderId = orderId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }
}
