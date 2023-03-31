package lab.org.microservices.core.order.saga.cancelorder;

import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import lab.org.api.core.account.AccountingServiceChannel;
import lab.org.api.core.account.ReverseAuthorizationCommand;
import lab.org.api.core.order.OrderServiceChannels;
import lab.org.api.core.restaurant.orderservice.BeginCancelRestaurantOrderCommand;
import lab.org.api.core.restaurant.orderservice.RestaurantOrderServiceChannels;
import lab.org.api.core.restaurant.orderservice.UndoBeginCancelRestaurantOrderCommand;
import lab.org.microservices.core.order.sagaparticipants.BeginCancelCommand;
import lab.org.microservices.core.order.sagaparticipants.ConfirmCancelOrderCommand;
import lab.org.microservices.core.order.sagaparticipants.UndoBeginCancelCommand;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

public class CancelOrderSaga implements SimpleSaga<CancelOrderSagaData> {

    private SagaDefinition<CancelOrderSagaData> sagaDefinition;

    @PostConstruct
    public void initializeSagaDefinition() {
        sagaDefinition = step()
                .invokeParticipant(this::beginCancel)
                .withCompensation(this::undoBeginCancel)
                .step()
                .invokeParticipant(this::beginCancelRestaurantOrder)
                .withCompensation(this::undoBeginCancelRestaurantOrder)
                .step()
                .invokeParticipant(this::reverseAuthorization)
                .step()
                .invokeParticipant(this::confirmRestaurantOrderCancel)
                .step()
                .invokeParticipant(this::confirmOrderCancel)
                .build();

    }

    private CommandWithDestination confirmOrderCancel(CancelOrderSagaData data) {
        return send(new ConfirmCancelOrderCommand(data.getOrderId()))
                .to(OrderServiceChannels.COMMAND_CHANNEL)
                .build();
    }

    private CommandWithDestination reverseAuthorization(CancelOrderSagaData data) {
        return send(new ReverseAuthorizationCommand(data.getConsumerId(), data.getOrderId(), data.getOrderTotal()))
                .to(AccountingServiceChannel.accountingServiceChannel)
                .build();
    }

    private CommandWithDestination undoBeginCancelRestaurantOrder(CancelOrderSagaData data) {
        return send(new UndoBeginCancelRestaurantOrderCommand(data.getRestaurantId(), data.getOrderId()))
                .to(RestaurantOrderServiceChannels.restaurantOrderServiceChannel)
                .build();
    }

    private CommandWithDestination beginCancelRestaurantOrder(CancelOrderSagaData data) {

        return send(new BeginCancelRestaurantOrderCommand(data.getOrderId(), (long) data.getOrderId()))
                .to(RestaurantOrderServiceChannels.restaurantOrderServiceChannel)
                .build();
    }

    private CommandWithDestination undoBeginCancel(CancelOrderSagaData data) {

        return send(new UndoBeginCancelCommand(data.getOrderId()))
                .to(OrderServiceChannels.COMMAND_CHANNEL)
                .build();
    }

    private CommandWithDestination beginCancel(CancelOrderSagaData data) {
        return send(new BeginCancelCommand(data.getOrderId()))
                .to(OrderServiceChannels.COMMAND_CHANNEL)
                .build();
    }

    private CommandWithDestination confirmRestaurantOrderCancel(CancelOrderSagaData data) {
        return send(new ConfirmCancelOrderCommand(data.getOrderId()))
                .to(OrderServiceChannels.COMMAND_CHANNEL)
                .build();
    }

    @Override
    public SagaDefinition<CancelOrderSagaData> getSagaDefinition() {
        Assert.notNull(sagaDefinition, "Saga definition must not be null");
        return sagaDefinition;
    }
}
