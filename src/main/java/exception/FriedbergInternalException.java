package exception;

/**
 * Represents an error caused by an internal Friedberg operation.
 */
public class FriedbergInternalException extends FriedbergException {
    public FriedbergInternalException(String message) {
        super(String.format("internal_error|%s", message));
    }

}
