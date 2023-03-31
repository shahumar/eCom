package lab.org.microservices.core.order.sagaparticipants;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import lab.org.api.core.kichenservice.*;

public class KitchenServiceProxy {

    public final CommandEndpoint<CreateTicket> create = CommandEndpointBuilder
            .forCommand(CreateTicket.class)
            .withChannel(KitchenServiceChannels.COMMAND_CHANNEL)
            .withReply(CreateTicketReply.class)
            .build();

    public final CommandEndpoint<ConfirmCreateTicket> confirmCreate = CommandEndpointBuilder
            .forCommand(ConfirmCreateTicket.class)
            .withChannel(KitchenServiceChannels.COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<CancelCreateTicket> cancel = CommandEndpointBuilder
            .forCommand(CancelCreateTicket.class)
            .withChannel(KitchenServiceChannels.COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();
}
