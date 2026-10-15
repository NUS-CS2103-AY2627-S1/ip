package cxception;

/**
 * Signals that a task creation command was missing required task description.
 */
public class EmptyEventException extends Exception {
    private String message = "Please key in an event!";

    /**
     * Constructs an {@code EmptyEventException} with no detail message.
     */
    public EmptyEventException() { }

    public String getMessage() {
        return message;
    }
}
