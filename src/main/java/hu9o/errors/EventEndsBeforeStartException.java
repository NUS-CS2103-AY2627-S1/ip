package hu9o.errors;

/** Indicates that an event command's /to time is earlier than its /from time. */
public class EventEndsBeforeStartException extends Hu9oException {
    public EventEndsBeforeStartException() {
        super("That event ends before it starts! Make sure /to is not earlier than /from.");
    }
}
