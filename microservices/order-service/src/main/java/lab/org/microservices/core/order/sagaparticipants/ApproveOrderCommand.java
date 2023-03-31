package lab.org.microservices.core.order.sagaparticipants;


public class ApproveOrderCommand extends OrderCommand {
    private ApproveOrderCommand() {}

    public ApproveOrderCommand(long orderId) {
        super(orderId);
    }

}
