package lab.org.microservices.composite.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lab.org.api.core.product.Category;
import lab.org.api.core.product.Product;
import lab.org.api.event.Event;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static lab.org.api.event.Event.Type.CREATE;
import static lab.org.api.event.Event.Type.DELETE;
import static lab.org.microservices.composite.IsSameEvent.sameEventExceptCreatedAt;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class IsSameEventTests {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void testEventObject() throws JsonProcessingException {
        Event<Integer, Category> event1 = new Event<>(CREATE, 1, new Category(1, "name", "cate-1", null));
        Event<Integer, Category> event2 = new Event<>(CREATE, 1, new Category(1, "name", "cate-1", null));
        Event<Integer, Category> event4 = new Event<>(CREATE, 1, new Category(2, "name", "cate-2", null));

        Event<Integer, Category> event3 = new Event<>(DELETE, 1, null);

        String str = mapper.writeValueAsString(event1);
        assertThat(str, is(sameEventExceptCreatedAt(event2)));
        assertThat(str, not(sameEventExceptCreatedAt(event3)));
        assertThat(str, not(sameEventExceptCreatedAt(event4)));

    }
}
