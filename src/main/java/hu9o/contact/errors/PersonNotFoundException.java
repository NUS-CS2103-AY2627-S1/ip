package hu9o.contact.errors;

import hu9o.errors.Hu9oException;

/** Indicates that a select, link, or connections command named an unknown person. */
public class PersonNotFoundException extends Hu9oException {
    public PersonNotFoundException() {
        super("No person with that name! Check the list of people using \"people\"");
    }
}
