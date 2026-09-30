package bean.exception;

/** Indicates that the user entered a command that the application does not support. */
public class UnknownCommandException extends RuntimeException {

    private final String invalidCommand;

    /** Creates an exception with a message and invalid command. */
    public UnknownCommandException(String message, String invalidCommand) {
        super(message);
        this.invalidCommand = invalidCommand;
    }

    /** Creates an exception with the default unknown-command message. */
    public UnknownCommandException(String invalidCommand) {
        super("Command not recognized by the chatbot.");
        this.invalidCommand = invalidCommand;
    }

    public String getInvalidCommand() {
        return invalidCommand;
    }
}
