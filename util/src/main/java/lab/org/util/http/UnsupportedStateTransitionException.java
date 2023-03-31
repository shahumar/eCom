package lab.org.util.http;


public class UnsupportedStateTransitionException extends RuntimeException {
    public UnsupportedStateTransitionException(Enum state) {
        super("current state " + state);
    }
}
