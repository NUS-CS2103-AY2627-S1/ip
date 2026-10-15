package slotbot;

/**
 * Represents an error caused by an invalid SlotBot command.
 */
public class SlotBotException extends Exception {
    /**
     * Creates an exception with a user-facing message.
     *
     * @param message Error message.
     */
    public SlotBotException(String message) {
        super(message);
    }
}
