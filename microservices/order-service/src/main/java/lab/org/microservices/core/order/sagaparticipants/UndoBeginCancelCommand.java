package lab.org.microservices.core.order.sagaparticipants;


public class UndoBeginCancelCommand extends OrderCommand {


    public UndoBeginCancelCommand(long orderId) {
        super(orderId);
    }
}
