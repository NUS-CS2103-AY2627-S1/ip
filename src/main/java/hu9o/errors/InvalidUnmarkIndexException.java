package hu9o.errors;

/** Indicates that unmark was given an invalid task number. */
public class InvalidUnmarkIndexException extends Hu9oException {
    public InvalidUnmarkIndexException() {
        super("I can't find that task! Check the list of items using \"list\"");
    }
}
