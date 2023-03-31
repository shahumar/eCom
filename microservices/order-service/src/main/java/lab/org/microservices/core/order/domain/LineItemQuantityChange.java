package lab.org.microservices.core.order.domain;

import lab.org.api.common.Money;

public class LineItemQuantityChange {

    final Money currentOrderTotal;
    final Money newOrderTotal;
    final Money delta;

    public LineItemQuantityChange(Money currentOrderTotal, Money newOrderTotal, Money delta) {
        this.currentOrderTotal = currentOrderTotal;
        this.newOrderTotal = newOrderTotal;
        this.delta = delta;
    }

    public Money getCurrentOrderTotal() {
        return currentOrderTotal;
    }

    public Money getNewOrderTotal() {
        return newOrderTotal;
    }

    public Money getDelta() {
        return delta;
    }
}
