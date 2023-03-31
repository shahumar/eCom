package lab.org.microservices.core.order.saga.createorder;

import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import lab.org.api.core.account.AccountingServiceChannel;
import lab.org.api.core.account.AuthorizeCommand;
import lab.org.api.core.consumer.ConsumerServiceChannels;
import lab.org.api.core.consumer.ValidateOrderByConsumer;
import lab.org.api.core.order.OrderServiceChannels;
import lab.org.api.core.order.events.OrderDetails;
import lab.org.api.core.restaurant.orderservice.*;
import lab.org.microservices.core.order.sagaparticipants.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

public class CreateOrderSaga implements SimpleSaga<CreateOrderSagaState> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private SagaDefinition<CreateOrderSagaState> sagaDefinition;

    public CreateOrderSaga(OrderServiceProxy orderService, ConsumerServiceProxy consumerService, KitchenServiceProxy kitchenService,
                           AccountingServiceProxy accountingService) {
        this.sagaDefinition =
                step()
                        .withCompensation(orderService.reject, CreateOrderSagaState)
                        .step()
                        .invokeParticipant(this::verifyConsumer)
                        .step()
                        .invokeParticipant(this::createRestaurantOrder)
                        .onReply(CreateRestaurantOrderReply.class, this::handleCreateRestaurantOrderReply)
                        .withCompensation(this::rejectRestaurantOrder)
                        .step()
                        .invokeParticipant(this::authorizeCard)
                        .step()
                        .invokeParticipant(this::approveOrder)
                        .step()
                        .invokeParticipant(this::approveRestaurantOrder)
                        .build();
    }

    private void handleCreateRestaurantOrderReply(CreateOrderSagaState data, CreateRestaurantOrderReply reply) {
        logger.debug("getRestaurantOrderId {}", reply.getRestaurantOrderId());
        data.setRestaurantOrderId(reply.getRestaurantOrderId());
    }

    private CommandWithDestination approveRestaurantOrder(CreateOrderSagaState data) {
        return send(new ConfirmCreateRestaurantOrder(data.getRestaurantOrderId()))
                .to(RestaurantOrderServiceChannels.restaurantOrderServiceChannel)
                .build();
    }

    private CommandWithDestination approveOrder(CreateOrderSagaState data) {
        return send(new ApproveOrderCommand(data.getOrderId()))
                .to(OrderServiceChannels.COMMAND_CHANNEL)
                .build();
    }

    private CommandWithDestination authorizeCard(CreateOrderSagaState data) {
        return send(new AuthorizeCommand(data.getOrderDetails().getConsumerId(),
                    data.getOrderId(), data.getOrderDetails().getOrderTotal()))
                .to(AccountingServiceChannel.accountingServiceChannel)
                .build();
    }

    private CommandWithDestination rejectRestaurantOrder(CreateOrderSagaState data) {
        return send(new CancelCreateRestaurantOrder(data.getOrderId()))
                .to(RestaurantOrderServiceChannels.restaurantOrderServiceChannel)
                .build();
    }

    private CommandWithDestination createRestaurantOrder(CreateOrderSagaState data) {
        return send(new CreateRestaurantOrder(data.getOrderDetails().getRestaurantId(),
                data.getOrderId(), makeRestaurantOrderDetails(data.getOrderDetails())))
                .to(RestaurantOrderServiceChannels.restaurantOrderServiceChannel)
                .build();
    }

    private RestaurantOrderDetails makeRestaurantOrderDetails(OrderDetails orderDetails) {
        return new RestaurantOrderDetails();
    }

    private CommandWithDestination verifyConsumer(CreateOrderSagaState data) {
        return send(new ValidateOrderByConsumer(
                data.getOrderDetails().getConsumerId(), data.getOrderId(), data.getOrderDetails().getOrderTotal()))
                .to(ConsumerServiceChannels.consumerServiceChannel)
                .build();
    }


    private CommandWithDestination rejectOrder(CreateOrderSagaState data) {
        return send(new RejectOrderCommand(data.getOrderId()))
                .to(OrderServiceChannels.COMMAND_CHANNEL)
                .build();
    }

    @Override
    public SagaDefinition<CreateOrderSagaState> getSagaDefinition() {
        return sagaDefinition;
    }


}
