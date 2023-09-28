package lab.org.microservices.composite.category;

import lab.org.api.composite.category.CategoryAggregate;
import lab.org.api.core.product.Category;
import lab.org.api.event.Event;
import lab.org.microservices.composite.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.context.annotation.Import;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;


import static lab.org.api.event.Event.Type.CREATE;
import static lab.org.api.event.Event.Type.DELETE;
import static lab.org.microservices.composite.IsSameEvent.sameEventExceptCreatedAt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static reactor.core.publisher.Mono.just;

@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = {TestSecurityConfig.class},
        properties = {
                "spring.security.oauth2.resourceserver.jwt.issuer-uri=",
                "spring.main.allow-bean-definition-overriding=true",
                "spring.cloud.stream.defaultBinder=rabbit"
        })
@Import({TestChannelBinderConfiguration.class})
public class MessagingTests {

    private static final Logger LOG = LoggerFactory.getLogger(MessagingTests.class);

    @Autowired
    public WebTestClient client;

    @Autowired
    private OutputDestination target;

    @BeforeEach
    void setUp() {
        purgeMessages("categories");
    }

    @Test
    void createCategory() {

        CategoryAggregate category = new CategoryAggregate(1, "Category 1", null, null);
        postAndVerifyCategory(category, ACCEPTED);
        List<String> messages = getMessages("categories");
        assertEquals(1, messages.size());
        Event<Integer, Category> event = new Event<>(CREATE, category.getCategoryId(), new Category(category.getCategoryId(), category.getName(), null, null));
        assertThat(messages.get(0), is(sameEventExceptCreatedAt(event)));

    }

    @Test
    void createCategoryWithParentShouldRaiseErrorIfParentNotExists() {
        CategoryAggregate category = new CategoryAggregate(2, "Category 1", "1", null);
        postAndVerifyCategory(category, ACCEPTED);
        List<String> messages = getMessages("categories");
        assertEquals(1, messages.size());
    }

    @Test
    void deleteCategory() {
        deleteAndVerifyDeleteCategory(1, ACCEPTED);
        final List<String> categories = getMessages("categories");
        assertEquals(1, categories.size());
        Event<Integer, Category> expectedEvent = new Event<>(DELETE, 1, null);
        assertThat(categories.get(0), is(sameEventExceptCreatedAt(expectedEvent)));

    }

    private void deleteAndVerifyDeleteCategory(int categoryId, HttpStatus accepted) {
        client.delete()
                .uri("/api/category/" + categoryId)
                .exchange()
                .expectStatus().isEqualTo(accepted);
    }

    private void postAndVerifyCategory(CategoryAggregate category, HttpStatus status) {
        client.post()
                .uri("/api/category")
                .body(just(category), CategoryAggregate.class)
                .exchange()
                .expectStatus().isEqualTo(status);

    }

    private void purgeMessages(String binding) {
        getMessages(binding);

    }

    private List<String> getMessages(String binding) {
        var messages = new ArrayList<String>();
        boolean anyMoreMessages = true;

        while (anyMoreMessages) {
            Message<byte[]> message = getMessage(binding);
            if (message == null) {
                anyMoreMessages = false;
            } else
                messages.add(new String(message.getPayload()));
        }
        return messages;
    }

    private Message<byte[]> getMessage(String binding) {
        try {
            return target.receive(0, binding);
        } catch (NullPointerException ex) {
            LOG.error("getMessage() received a NPE with binding = {}", binding);
            return null;
        }
    }


}
