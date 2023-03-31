package lab.org.api.core.consumer;


import io.eventuate.tram.commands.common.Command;
import lab.org.api.common.Money;

public class ValidateOrderByConsumer implements Command {

    private long consumerId;
    private long orderId;
    private Money orderTotal;

    public ValidateOrderByConsumer() {
    }


    public ValidateOrderByConsumer(long consumerId, long orderId, Money orderTotal) {
        this.consumerId = consumerId;
        this.orderId = orderId;
        this.orderTotal = orderTotal;
    }

    public Long getOrderId() {
        return orderId;
    }

    public long getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(long consumerId) {
        this.consumerId = consumerId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public Money getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Money orderTotal) {
        this.orderTotal = orderTotal;
    }
}
