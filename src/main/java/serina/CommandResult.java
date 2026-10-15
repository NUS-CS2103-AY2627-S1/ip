package serina;

import java.util.List;

/**
 * Contains the messages and application state produced by one Serina command.
 */
public class CommandResult {
    private final List<ResponseMessage> messages;
    private final boolean shouldExit;

    /**
     * Creates a command result with the given responses and exit behavior.
     *
     * @param responses Messages to show in order.
     * @param shouldExit Whether the application should stop accepting commands.
     */
    public CommandResult(List<String> responses, boolean shouldExit) {
        this.messages = responses.stream()
                .map(ResponseMessage::normal)
                .toList();
        this.shouldExit = shouldExit;
    }

    /**
     * Creates a command result from individual responses and the given exit behavior.
     *
     * @param shouldExit Whether the application should stop accepting commands.
     * @param responses Messages to show in order.
     */
    public CommandResult(boolean shouldExit, String... responses) {
        this(List.of(responses), shouldExit);
    }

    /**
     * Creates a command result from an immutable copy of typed messages.
     */
    private CommandResult(boolean shouldExit, List<ResponseMessage> messages) {
        this.messages = List.copyOf(messages);
        this.shouldExit = shouldExit;
    }

    /**
     * Creates a command result from typed response messages.
     *
     * @param shouldExit Whether the application should stop accepting commands.
     * @param messages Typed messages shown in order.
     * @return Command result containing the messages.
     */
    public static CommandResult ofMessages(boolean shouldExit, ResponseMessage... messages) {
        return new CommandResult(shouldExit, List.of(messages));
    }

    /**
     * Returns the messages produced by the command.
     *
     * @return Responses in display order.
     */
    public List<String> getResponses() {
        return messages.stream()
                .map(ResponseMessage::getText)
                .toList();
    }

    /**
     * Returns the typed messages produced by the command.
     *
     * @return Typed responses in display order.
     */
    public List<ResponseMessage> getMessages() {
        return messages;
    }

    /**
     * Returns whether the application should stop accepting commands.
     *
     * @return {@code true} if the command ends the conversation.
     */
    public boolean shouldExit() {
        return shouldExit;
    }
}
