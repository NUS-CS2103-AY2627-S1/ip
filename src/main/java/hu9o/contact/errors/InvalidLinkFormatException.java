package hu9o.contact.errors;

import hu9o.errors.Hu9oException;

/** Indicates that a link command does not name two people. */
public class InvalidLinkFormatException extends Hu9oException {
    public InvalidLinkFormatException() {
        super("That link's missing a name! Try: link NAME1 /with NAME2");
    }
}
