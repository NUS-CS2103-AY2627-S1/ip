package kevin;

/**
 * Exception unique to Kevin chatbot.
 * Error messages start with ???.
 */
public class KevinException extends Exception {
    public KevinException(String message) {
        super("??? " + message);
    }
}
