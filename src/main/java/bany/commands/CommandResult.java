package bany.commands;

import java.util.List;
import java.util.Objects;

/**
 * Describes the user-facing outcome of executing one command.
 *
 * @param messages ordered user-facing messages produced by the command.
 * @param outcome final state of the command execution.
 */
public record CommandResult(
        List<ResponseMessage> messages,
        CommandOutcome outcome
) {
    /**
     * Validates and defensively copies the messages in this result.
     *
     * @throws NullPointerException if the outcome or message list is null.
     * @throws IllegalArgumentException if no messages are supplied.
     */
    public CommandResult {
        messages = List.copyOf(Objects.requireNonNull(messages, "Messages cannot be null."));
        outcome = Objects.requireNonNull(outcome, "Outcome cannot be null");

        if (messages.isEmpty()) {
            throw new IllegalArgumentException("A command result must contain a message.");
        }
    }

    /**
     * Creates a successful result containing informational or warning messages.
     *
     * @param messages ordered messages to display.
     * @return a result with the success outcome.
     */
    public static CommandResult success(ResponseMessage... messages) {
        return new CommandResult(
                List.of(messages), CommandOutcome.SUCCESS
        );
    }

    /**
     * Creates an error result containing messages that explain the failure.
     *
     * @param messages ordered messages to display.
     * @return a result with the error outcome.
     */
    public static CommandResult error(ResponseMessage... messages) {
        return new CommandResult(
                List.of(messages), CommandOutcome.ERROR
        );
    }

    /**
     * Creates a successful result that requests application exit after its messages are shown.
     *
     * @param messages ordered messages to display before closing.
     * @return a result with the exit outcome.
     */
    public static CommandResult exit(ResponseMessage... messages) {
        return new CommandResult(
                List.of(messages), CommandOutcome.EXIT
        );
    }

    /**
     * Returns whether the application should close after displaying this result.
     *
     * @return true if the outcome requests application exit.
     */
    public boolean shouldExit() {
        return outcome == CommandOutcome.EXIT;
    }

}
