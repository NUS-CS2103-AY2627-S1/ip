package hu9o.errors;

/** Base type for recoverable errors caused by a Hu9o command. */
public abstract class Hu9oException extends Exception {
    protected Hu9oException(String message) {
        super(message);
    }
}
