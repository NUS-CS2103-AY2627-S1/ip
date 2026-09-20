package harold;

import java.util.Objects;

/**
 * Represents one chatbot response and whether it ends the conversation.
 *
 * @param message Message to display to the user.
 * @param isExit Whether the interface should stop accepting commands.
 */
public record CommandResult(String message, boolean isExit) {
    /**
     * Creates a validated command result.
     */
    public CommandResult {
        Objects.requireNonNull(message);
    }

    /**
     * Creates a response that keeps the conversation active.
     *
     * @param message Message to display.
     * @return Non-exiting command result.
     */
    public static CommandResult continueWith(String message) {
        return new CommandResult(message, false);
    }

    /**
     * Creates a response that ends the conversation.
     *
     * @param message Message to display before exiting.
     * @return Exiting command result.
     */
    public static CommandResult exitWith(String message) {
        return new CommandResult(message, true);
    }
}
