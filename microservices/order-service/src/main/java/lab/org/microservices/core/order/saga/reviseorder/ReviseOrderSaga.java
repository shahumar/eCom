package lab.org.microservices.core.order.saga.reviseorder;

import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import lab.org.api.core.account.AccountingServiceChannel;
import lab.org.api.core.account.ReviseAuthorization;
import lab.org.api.core.kichenservice.BeginReviseTicketCommand;
import lab.org.api.core.kichenservice.ConfirmReviseTicketCommand;
import lab.org.api.core.kichenservice.KitchenServiceChannels;
import lab.org.api.core.kichenservice.UndoBeginReviseTicketCommand;
import lab.org.api.core.order.OrderServiceChannels;
import lab.org.microservices.core.order.sagaparticipants.BeginReviseOrderCommand;
import lab.org.microservices.core.order.sagaparticipants.BeginReviseOrderReply;
import lab.org.microservices.core.order.sagaparticipants.ConfirmReviseOrderCommand;
import lab.org.microservices.core.order.sagaparticipants.UndoBeginReviseOrderCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

public class ReviseOrderSaga implements SimpleSaga<ReviseOrderSagaData> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private SagaDefinition<ReviseOrderSagaData> sagaDefinition;

    @PostConstruct
    public void initializeSagaDefinition() {
        sagaDefinition = step()
                .invokeParticipant(this::beginReviseOrder)
                .onReply(BeginReviseOrderReply.class, this::handleBeginReviseOrderReply)
                .withCompensation(this::undoBeginReviseOrder)
                .step()
                .invokeParticipant(this::beginReviseTicket)
                .withCompensation(this::undoBeginReviseTicket)
                .step()
                .invokeParticipant(this::reviseAuthorization)
                .step()
                .invokeParticipant(this::confirmTicketRevision)
                .step()
                .invokeParticipant(this::confirmOrderRevision)
                .build();

    }

    private CommandWithDestination beginReviseOrder(ReviseOrderSagaData data) {
        return send(new BeginReviseOrderCommand(data.getOrderId(), data.getOrderRevision()))
                .to(OrderServiceChannels.COMMAND_CHANNEL)
                .build();
    }

    private CommandWithDestination undoBeginReviseOrder(ReviseOrderSagaData data) {
        return send(new UndoBeginReviseOrderCommand(data.getOrderId()))
                .to(OrderServiceChannels.COMMAND_CHANNEL)
                .build();
    }

    private CommandWithDestination beginReviseTicket(ReviseOrderSagaData data) {
        return send(new BeginReviseTicketCommand(data.getRestaurantId(), data.getOrderId(), data.getOrderRevision().getRevisedOrderLineItems()))
                .to(KitchenServiceChannels.COMMAND_CHANNEL)
                .build();
    }

    private CommandWithDestination undoBeginReviseTicket(ReviseOrderSagaData data) {
        return send(new UndoBeginReviseTicketCommand(data.getRestaurantId(), data.getOrderId()))
                .to(KitchenServiceChannels.COMMAND_CHANNEL)
                .build();
    }

    private CommandWithDestination reviseAuthorization(ReviseOrderSagaData data) {
        return send(new ReviseAuthorization(data.getConsumerId(), data.getOrderId(), data.getRevisedOrderTotal()))
                .to(AccountingServiceChannel.accountingServiceChannel)
                .build();

    }

    private CommandWithDestination confirmTicketRevision(ReviseOrderSagaData data) {
        return send(new ConfirmReviseTicketCommand(data.getRestaurantId(), data.getOrderId(), data.getOrderRevision().getRevisedOrderLineItems()))
                .to(KitchenServiceChannels.COMMAND_CHANNEL)
                .build();
    }

    private CommandWithDestination confirmOrderRevision(ReviseOrderSagaData data) {
        return send(new ConfirmReviseOrderCommand(data.getOrderId(), data.getOrderRevision()))
                .to(OrderServiceChannels.COMMAND_CHANNEL)
                .build();
    }

    private void handleBeginReviseOrderReply(ReviseOrderSagaData data, BeginReviseOrderReply reply) {
        logger.info("order total : {}", reply.getRevisedOrderTotal());
        data.setRevisedOrderTotal(reply.getRevisedOrderTotal());
    }

    @Override
    public SagaDefinition<ReviseOrderSagaData> getSagaDefinition() {
        return sagaDefinition;
    }
}
