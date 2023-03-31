package lab.org.microservices.core.order.sagaparticipants;


public class UndoBeginReviseOrderCommand extends OrderCommand {

    protected UndoBeginReviseOrderCommand() {}

    public UndoBeginReviseOrderCommand(Long orderId) {
        super(orderId);
    }
}
