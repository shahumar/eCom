package lab.org.microservices.composite.product;

import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.context.annotation.Import;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.messaging.Message;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true"})
@Import({TestChannelBinderConfiguration.class})
public class MessagingTests {
    private static final Logger LOG = LoggerFactory.getLogger(MessagingTests.class);

    @Autowired
    private WebTestClient client;

    @Autowired
    private OutputDestination target;

    @BeforeEach
    void setUp() {
        purgeMessages("products");
        purgeMessages("recommendations");
        purgeMessages("reviews");
    }

    private void purgeMessages(String bindingName) {
        getMessages(bindingName);
    }

    private List<String> getMessages(String bindingName) {
        List<String> messages = new ArrayList<>();
        boolean anyMoreMessages = true;

        while (anyMoreMessages) {
            Message<byte[]> message = getMessage(bindingName);
            if (message == null)
                anyMoreMessages = false
            else
                messages.add(new String(message.getPayload()));

        }
        return messages;
    }

    private Message<byte[]> getMessage(String bindingName) {
        try {
            return target.receive(0, bindingName);
        } catch (NullPointerException e) {
            LOG.error("getMessage() received a NPE with binding = {}", bindingName);
            return null;
        }

    }
}
