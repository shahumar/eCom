package lab.org.microservices.composite.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lab.org.api.core.product.Product;
import lab.org.api.event.Event;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static lab.org.api.event.Event.Type.CREATE;
import static lab.org.api.event.Event.Type.DELETE;
import static lab.org.microservices.composite.product.IsSameEvent.sameEventExceptCreatedAt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class IsSameEventTests {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void testEventObjectCompare() throws JsonProcessingException {
        Event<Integer, Product> event1 = new Event<>(CREATE, 1, new Product(1, "name", 1, null));
        Event<Integer, Product> event2 = new Event<>(CREATE, 1, new Product(1, "name", 1, null));
        Event<Integer, Product> event3 = new Event<>(DELETE, 1, null);
        Event<Integer, Product> event4 = new Event<>(CREATE, 1, new Product(2, "name", 1, null));

        String event1Json = mapper.writeValueAsString(event1);

        assertThat(event1Json, is(sameEventExceptCreatedAt(event2)));
        assertThat(event1Json, not(sameEventExceptCreatedAt(event3)));
        assertThat(event1Json, not(sameEventExceptCreatedAt(event4)));


    }
}
