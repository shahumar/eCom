package lab.org.microservices.core.order.sagaparticipants;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import lab.org.api.core.account.AccountingServiceChannels;
import lab.org.api.core.account.AuthorizeCommand;

public class AccountingServiceProxy {

    public final CommandEndpoint<AuthorizeCommand> authorize= CommandEndpointBuilder
            .forCommand(AuthorizeCommand.class)
            .withChannel(AccountingServiceChannels.accountingServiceChannel)
            .withReply(Success.class)
            .build();
}
