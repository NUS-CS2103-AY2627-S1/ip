package hu9o.contact.errors;

import hu9o.errors.Hu9oException;

/** Indicates that a connections command has no name. */
public class InvalidConnectionsFormatException extends Hu9oException {
    public InvalidConnectionsFormatException() {
        super("Whose connections do you want to see? Try: connections NAME");
    }
}
