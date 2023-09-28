package lab.org.microservices.composite.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import lab.org.api.core.product.Category;
import lab.org.api.core.product.CategoryService;
import lab.org.api.event.Event;
import lab.org.api.exceptions.InvalidInputException;
import lab.org.api.exceptions.NotFoundException;
import lab.org.util.http.HttpErrorInfo;
import lab.org.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;

import static lab.org.api.event.Event.Type.CREATE;
import static lab.org.api.event.Event.Type.DELETE;

@Component
public class CategoryCompositeIntegration implements CategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryCompositeIntegration.class);

    private WebClient client;
    private Scheduler scheduler;
    private ObjectMapper mapper;
    private StreamBridge streamBridge;
    private ServiceUtil serviceUtil;

    private static final String CATALOGUE_SERVICE_URL = "http://catalogue";

    @Autowired
    public CategoryCompositeIntegration(@Qualifier("publishEventScheduler") Scheduler scheduler, WebClient.Builder client, ObjectMapper mapper, StreamBridge streamBridge, ServiceUtil serviceUtil) {
        this.client = client.build();
        this.scheduler = scheduler;
        this.mapper = mapper;
        this.streamBridge = streamBridge;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Mono<Category> getCategory(HttpHeaders headers, int categoryId) {
        URI uri = UriComponentsBuilder.fromUriString(
                CATALOGUE_SERVICE_URL + "/category/{categoryId}").build(categoryId);
        LOG.debug("Getting category by ID: {}", categoryId);
        return client.get()
                .uri(uri)
                .headers(h -> h.addAll(headers))
                .retrieve()
                .bodyToMono(Category.class)
                .log(LOG.getName(), Level.FINE)
                .onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
    }

    @Override
    public Mono<Category> createCategory(Category body) {
        return Mono.fromCallable(() -> {
            sendMessage("categories-out-0", new Event(CREATE, body.getCategoryId(), body));
            return body;
        }).subscribeOn(scheduler);

    }

    @Override
    public Flux<Category> listCategories() {
        URI uri = UriComponentsBuilder.fromUriString(CATALOGUE_SERVICE_URL + "/category").build().toUri();

        return client.get()
                .uri(uri)
                .retrieve()
                .bodyToFlux(Category.class)
                .log(LOG.getName(), Level.FINE)
                .onErrorMap(WebClientResponseException.class, ex -> handleException(ex));


    }

    private Throwable handleException(Throwable ex) {
        if (!(ex instanceof WebClientResponseException)) {
            LOG.warn("Got unexpected exception: {} will rethrow it", ex.toString());
            return ex;
        }
        WebClientResponseException wcre = (WebClientResponseException) ex;
        return switch (wcre.getStatusCode()) {
            case NOT_FOUND -> new NotFoundException(getErrorMessage(wcre));
            case UNPROCESSABLE_ENTITY -> new InvalidInputException(getErrorMessage(wcre));
            default -> {
                LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
                LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
                yield ex;
            }
        };

    }

    private String getErrorMessage(WebClientResponseException wcre) {
        try {
            return mapper.readValue(wcre.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    private void sendMessage(String bindingName, Event event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getKey())
                .build();
        streamBridge.send(bindingName, message);
    }

    @Override
    public Mono<Void> deleteCategory(int categoryId) {
        return Mono.fromRunnable(() -> sendMessage("categories-out-0", new Event(DELETE, categoryId, null)))
                .subscribeOn(scheduler)
                .then();

    }
}
