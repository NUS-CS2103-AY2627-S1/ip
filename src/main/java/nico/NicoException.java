package nico;

/**
 * Represents an error caused by invalid user input or task storage.
 */
public class NicoException extends Exception {
    /**
     * Creates an exception without a detail message.
     */
    public NicoException() {
        super();
    }

    /**
     * Creates an exception with a detail message.
     *
     * @param message Detail message for the user.
     */
    public NicoException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a user-facing message and its underlying cause.
     *
     * @param message detail message for the user.
     * @param cause underlying failure that caused this exception.
     */
    public NicoException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a consistently formatted message for invalid user input.
     *
     * @param problem concise explanation of what prevented the command from running.
     * @param suggestion command or value the user can enter next.
     * @return exception containing the problem and an actionable suggestion
     */
    public static NicoException invalidInput(String problem, String suggestion) {
        return new NicoException("Aiyoh! " + problem + System.lineSeparator() + "Try this lah: " + suggestion);
    }
}
