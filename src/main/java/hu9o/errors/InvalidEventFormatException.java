package hu9o.errors;

/** Indicates that an event command is missing its description, /from, or /to time. */
public class InvalidEventFormatException extends Hu9oException {
    public InvalidEventFormatException() {
        super("That event's missing something! Try: event DESCRIPTION /from START /to END");
    }
}
