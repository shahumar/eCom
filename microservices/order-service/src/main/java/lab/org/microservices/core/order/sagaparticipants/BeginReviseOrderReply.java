package lab.org.microservices.core.order.sagaparticipants;

import lab.org.api.common.Money;

public class BeginReviseOrderReply {

    private Money revisedOrderTotal;

    public BeginReviseOrderReply(Money revisedOrderTotal) {
        this.revisedOrderTotal = revisedOrderTotal;
    }

    public BeginReviseOrderReply() {
    }

    public Money getRevisedOrderTotal() {
        return revisedOrderTotal;
    }

    public void setRevisedOrderTotal(Money revisedOrderTotal) {
        this.revisedOrderTotal = revisedOrderTotal;
    }
}
