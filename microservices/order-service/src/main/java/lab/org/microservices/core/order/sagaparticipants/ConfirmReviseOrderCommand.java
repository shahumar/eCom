package lab.org.microservices.core.order.sagaparticipants;

import io.eventuate.tram.commands.common.Command;
import lab.org.microservices.core.order.domain.OrderRevision;

public class ConfirmReviseOrderCommand extends OrderCommand {

    private ConfirmReviseOrderCommand() {
    }

    public ConfirmReviseOrderCommand(long orderId, OrderRevision revision) {
        super(orderId);
        this.revision = revision;
    }

    private OrderRevision revision;

    public OrderRevision getRevision() {
        return revision;
    }

}
