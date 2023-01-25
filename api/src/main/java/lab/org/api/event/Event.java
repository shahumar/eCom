package lab.org.api.event;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.key.ZonedDateTimeKeySerializer;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;


public class Event<K, T> {

    public enum Type {
        CREATE, DELETE
    }

    private final Type eventType;
    private final K key;
    private final T data;
    private final LocalDateTime eventCreatedAt;

    public Event() {
        this.eventType = null;
        this.key = null;
        this.data = null;
        this.eventCreatedAt = null;
    }

    public Event(Type eventType, K key, T data) {
        this.eventType = eventType;
        this.key = key;
        this.data = data;
        this.eventCreatedAt = LocalDateTime.now();
    }

    public Type getEventType() {
        return eventType;
    }

    public K getKey() {
        return key;
    }

    public T getData() {
        return data;
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getEventCreatedAt() {
        return eventCreatedAt;
    }
}
