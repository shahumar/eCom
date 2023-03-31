package lab.org.microservices.core.order.sagaparticipants;


public class RejectOrderCommand extends OrderCommand {
    private RejectOrderCommand() {
    }

    public RejectOrderCommand(long orderId) {
        super(orderId);
    }
}
