package lab.org.api.core.account;

import io.eventuate.tram.commands.common.Command;
import lab.org.api.common.Money;

public class AuthorizeCommand implements Command {

    private long consumerId;
    private long orderId;
    private Money amount;
    private Money orderTotal;

    public AuthorizeCommand(){}

    public AuthorizeCommand(long consumerId, long orderId, Money orderTotal) {
        this.consumerId = consumerId;
        this.orderId = orderId;
        this.orderTotal = orderTotal;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public long getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(long consumerId) {
        this.consumerId = consumerId;
    }

    public Money getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Money orderTotal) {
        this.orderTotal = orderTotal;
    }

    public Long getOrderId() {

        return orderId;

    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

}
