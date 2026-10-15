package esther.command;

import java.util.List;

/**
 * Represents the result of processing one Esther command.
 *
 * @param messages Messages to display in order.
 * @param shouldExit Whether the application should exit after displaying the messages.
 * @param isError Whether the messages describe an error.
 */
public record CommandResult(List<String> messages, boolean shouldExit,
                            boolean isError) {
    /**
     * Creates a command result with an immutable message list.
     *
     * @param messages Messages to display in order.
     * @param shouldExit Whether the application should exit.
     * @param isError Whether the messages describe an error.
     */
    public CommandResult {
        messages = List.copyOf(messages);
    }
}
