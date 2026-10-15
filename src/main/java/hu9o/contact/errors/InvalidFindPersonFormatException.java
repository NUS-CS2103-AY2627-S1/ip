package hu9o.contact.errors;

import hu9o.errors.Hu9oException;

/** Indicates that a findperson command has no search keyword. */
public class InvalidFindPersonFormatException extends Hu9oException {
    public InvalidFindPersonFormatException() {
        super("Who should I search for? Try: findperson KEYWORD");
    }
}
