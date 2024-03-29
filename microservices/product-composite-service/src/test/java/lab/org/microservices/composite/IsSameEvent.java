package lab.org.microservices.composite;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lab.org.api.event.Event;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IsSameEvent extends TypeSafeMatcher<String> {

    public static final Logger LOG = LoggerFactory.getLogger(IsSameEvent.class);

    private ObjectMapper mapper = new ObjectMapper();

    private Event expectedEvent;

    private IsSameEvent(Event expectedEvent) {
        this.expectedEvent = expectedEvent;
    }


    @Override
    protected boolean matchesSafely(String eventAsJson) {
        if (expectedEvent == null)
            return false;
        LOG.trace("Convert the following json string to a map: {}", eventAsJson);
        Map mapEvent = convertJsonStringToMap(eventAsJson);
        System.out.println(eventAsJson);
        System.out.println("-------------------------");
        System.out.println(expectedEvent);

        mapEvent.remove("eventCreatedAt");
        Map mapExpectedEvent = null;
        try {
            mapExpectedEvent = convertJsonStringToMap(mapper.writeValueAsString(expectedEvent));
            mapExpectedEvent.remove("eventCreatedAt");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
        LOG.trace("Got the map: {}", mapEvent);
        LOG.trace("Compare to the expected map: {}", mapExpectedEvent);
        return mapEvent.equals(mapExpectedEvent);
    }


    @Override
    public void describeTo(Description description) {
        String expectedJson = convertObjectToJsonString(expectedEvent);
        description.appendText("expected to look like " + expectedJson);
    }


    public static Matcher<String> sameEventExceptCreatedAt(Event expectedEvent) {
        return new IsSameEvent(expectedEvent);
    }

    private Map convertJsonStringToMap(String eventAsJson) {
        try {
            return mapper.readValue(eventAsJson, new TypeReference<HashMap>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String convertObjectToJsonString(Object expectedEvent) {
        try {
            return mapper.writeValueAsString(expectedEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Map getMapWithoutCreatedAt(Event event) {
        Map mapEvent = convertObjectToMap(event);
        mapEvent.remove("eventCreatedAt");
        return mapEvent;
    }

    private Map convertObjectToMap(Object event) {
        JsonNode node = mapper.convertValue(event, JsonNode.class);
        return mapper.convertValue(node, Map.class);
    }
}
