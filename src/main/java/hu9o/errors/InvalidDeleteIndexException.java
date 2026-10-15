package hu9o.errors;

/** Indicates that delete was given an invalid task number. */
public class InvalidDeleteIndexException extends Hu9oException {
    public InvalidDeleteIndexException() {
        super("I can't find that task to delete! Check the list of items using \"list\"");
    }
}
