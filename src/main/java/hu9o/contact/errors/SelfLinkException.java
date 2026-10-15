package hu9o.contact.errors;

import hu9o.errors.Hu9oException;

/** Indicates that a link command named the same person twice. */
public class SelfLinkException extends Hu9oException {
    public SelfLinkException() {
        super("You can't link someone to themselves, silly!");
    }
}
