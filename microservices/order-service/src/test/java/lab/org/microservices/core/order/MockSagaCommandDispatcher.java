package lab.org.microservices.core.order;

import io.eventuate.tram.commands.common.CommandNameMapping;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.messaging.consumer.MessageConsumer;
import io.eventuate.tram.messaging.producer.MessageProducer;
import io.eventuate.tram.sagas.common.SagaLockManager;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcher;

public class MockSagaCommandDispatcher extends SagaCommandDispatcher {

    public MockSagaCommandDispatcher(String commandDispatcherId, CommandHandlers target, MessageConsumer messageConsumer, MessageProducer messageProducer, SagaLockManager sagaLockManager, CommandNameMapping commandNameMapping) {
        super(commandDispatcherId, target, messageConsumer, messageProducer, sagaLockManager, commandNameMapping);
    }
}
