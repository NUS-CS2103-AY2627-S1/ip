package bean.exception;

/** Indicates that a recognized command has invalid syntax. */
public class InvalidSyntaxException extends RuntimeException {

    private final String invalidSyntax;

    /** Creates an exception with a message and invalid input. */
    public InvalidSyntaxException(String message, String invalidSyntax) {
        super(message);
        this.invalidSyntax = invalidSyntax;
    }

    /** Creates an exception with the default invalid-syntax message. */
    public InvalidSyntaxException(String invalidSyntax) {
        super("Command not recognized by the chatbot.");
        this.invalidSyntax = invalidSyntax;
    }

    public String getInvalidCommand() {
        return invalidSyntax;
    }
}
