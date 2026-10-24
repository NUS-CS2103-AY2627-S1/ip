package shannon.exception;

/** Thrown when {@code find} is typed on its own, with no keyword to search for. */
public class EmptyKeywordException extends ShannonException {

    public EmptyKeywordException() {
        super("Tell me what to look for, for example: find book");
    }
}
