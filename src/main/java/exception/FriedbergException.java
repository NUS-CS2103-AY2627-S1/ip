package exception;

/**
 * Generic Friedberg chatbot user error exception
 * FriedbergException
 */
public class FriedbergException extends Exception {
    public FriedbergException(String message) {
        super(String.format("Friedberg Exception|%s", message));
    }
}
