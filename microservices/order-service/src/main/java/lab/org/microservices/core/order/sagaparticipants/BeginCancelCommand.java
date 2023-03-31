package lab.org.microservices.core.order.sagaparticipants;

public class BeginCancelCommand extends OrderCommand {

    private BeginCancelCommand() {}

    public BeginCancelCommand(long orderId) {
        super(orderId);
    }
}
