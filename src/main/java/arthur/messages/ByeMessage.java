package arthur.messages;

/**
 * Represents the final message displayed when the application exits.
 */
public class ByeMessage extends Message {
    private static final String MESSAGE = "So long, and thanks for all the fish.";

    @Override
    public String toString() {
        return MESSAGE;
    }

    @Override
    public boolean isFinal() {
        return true;
    }
}
