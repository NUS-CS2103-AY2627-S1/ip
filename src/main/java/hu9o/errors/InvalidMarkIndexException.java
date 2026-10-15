package hu9o.errors;

/** Indicates that mark was given an invalid task number. */
public class InvalidMarkIndexException extends Hu9oException {
    public InvalidMarkIndexException() {
        super("I can't find that task! Check the list of items using \"list\"");
    }
}
