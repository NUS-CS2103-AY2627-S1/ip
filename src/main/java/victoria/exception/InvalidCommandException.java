package victoria.exception;

/** Indicates that the user entered an unknown command. */
public class InvalidCommandException extends VictoriaException {
    /** Creates an invalid-command error. */
    public InvalidCommandException(String message) { super(message); }
}
