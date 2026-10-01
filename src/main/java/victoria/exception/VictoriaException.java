package victoria.exception;

/** Represents an input error that can be explained to a Victoria user. */
public class VictoriaException extends RuntimeException {
    /** Creates an input error with a user-facing message. */
    public VictoriaException(String message) { super(message); }
}
