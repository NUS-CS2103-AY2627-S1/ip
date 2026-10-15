package hu9o.contact.errors;

import hu9o.errors.Hu9oException;

/** Indicates that a select command has no name. */
public class InvalidSelectFormatException extends Hu9oException {
    public InvalidSelectFormatException() {
        super("Who do you want to select? Try: select NAME");
    }
}
