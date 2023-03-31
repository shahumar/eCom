package lab.org.api.core.order.web;

public class CreateOrderResponse {

    private long orderId;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    private CreateOrderResponse() {
    }

    public CreateOrderResponse(long orderId) {
        this.orderId = orderId;
    }
}
