package lab.org.microservices.core.order.sagaparticipants;

import io.eventuate.tram.commands.common.Command;
import lab.org.microservices.core.order.domain.OrderRevision;

public class BeginReviseOrderCommand extends OrderCommand {

    OrderRevision revision;

    private BeginReviseOrderCommand() {}


    public BeginReviseOrderCommand(Long orderId, OrderRevision orderRevision) {
        super(orderId);
        this.revision = orderRevision;
    }

    public OrderRevision getRevision() {
        return revision;
    }

    public void setRevision(OrderRevision revision) {
        this.revision = revision;
    }
}
