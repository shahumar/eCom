package lab.org.api.core.order.events;

public enum OrderState {
    CREATE_PENDING,
    AUTHORIZED,
    REJECTED,
    CANCEL_PENDING,
    CANCELLED,
    REVISION_PENDING,
}
