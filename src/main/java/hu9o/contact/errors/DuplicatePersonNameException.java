package hu9o.contact.errors;

import hu9o.errors.Hu9oException;

/** Indicates that a person command's name matches someone already in the network. */
public class DuplicatePersonNameException extends Hu9oException {
    /** Creates the exception explaining that the person's name is already taken. */
    public DuplicatePersonNameException() {
        super("Someone with that name is already in my contacts! Try a different name, "
                + "or delete the existing one first.");
    }
}
