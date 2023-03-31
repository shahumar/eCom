package lab.org.microservices.core.order.sagaparticipants;

import io.eventuate.tram.commands.common.Command;

public class ConfirmCancelOrderCommand extends OrderCommand {

    private ConfirmCancelOrderCommand() {}

    public ConfirmCancelOrderCommand(long orderId) {
        super(orderId);
    }
}
