package echo.exception;

/**
 *  * A {@link EchoException} that signals that the find command was entered without a keyword.
 */
public class FindFormatException extends EchoException {
    public FindFormatException() {
        super("I need a search term. Try: find <keyword>");
    }
}
