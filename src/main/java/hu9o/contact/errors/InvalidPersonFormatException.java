package hu9o.contact.errors;

import hu9o.errors.Hu9oException;

/** Indicates that a person command has no name, phone, or email. */
public class InvalidPersonFormatException extends Hu9oException {
    /** Creates the exception with the expected person command format. */
    public InvalidPersonFormatException() {
        super("That person's missing some details! Try: person NAME /phone PHONE /email EMAIL "
                + "[/dob DOB] [/notes NOTES]");
    }
}
