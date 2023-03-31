package lab.org.microservices.core.order.sagaparticipants;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import lab.org.api.core.consumer.ConsumerServiceChannels;
import lab.org.api.core.consumer.ValidateOrderByConsumer;

public class ConsumerServiceProxy {

    public final CommandEndpoint<ValidateOrderByConsumer> validateOrder = CommandEndpointBuilder
            .forCommand(ValidateOrderByConsumer.class)
            .withChannel(ConsumerServiceChannels.consumerServiceChannel)
            .withReply(Success.class)
            .build();
}
