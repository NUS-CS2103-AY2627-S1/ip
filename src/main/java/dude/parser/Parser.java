package dude.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.stream.Collectors;

import dude.command.core.CommandType;
import dude.exception.UsageDetails;
import dude.exception.UsageException;

/**
 * Parses raw command input without constructing or executing commands.
 */
public final class Parser {
    private static final String SUPPORTED_COMMANDS = Arrays.stream(CommandType.values())
            .map(CommandType::getWord)
            .collect(Collectors.joining(", "));
    private static final String COMMAND_USAGE = Arrays.stream(CommandType.values())
            .map(CommandType::getUsageMessage)
            .collect(Collectors.joining(" | "));

    private Parser() {
    }

    /**
     * Parses a full command line into a syntactic request.
     *
     * @param input Raw command line.
     * @return Parsed command request.
     * @throws UsageException If the command word or no-argument contract is invalid.
     */
    public static CommandRequest parse(String input) throws UsageException {
        if (input == null || input.isBlank()) {
            throw new UsageException(new UsageDetails("", "command", "<missing>",
                    SUPPORTED_COMMANDS, COMMAND_USAGE, "<task type>"));
        }
        String[] commandParts = input.trim().split("\\s+", 2);
        CommandType commandType = parseType(commandParts[0]);
        String argument = commandParts.length > 1 ? commandParts[1] : null;
        if (requiresNoArgument(commandType)) {
            requireNoArgument(commandType, argument);
        }
        return new CommandRequest(commandType, argument);
    }

    /**
     * Parses a one-based task number into a zero-based index.
     *
     * @param commandType Command whose argument is being parsed.
     * @param argument    Raw task-number argument.
     * @param taskCount   Number of available tasks.
     * @return Zero-based task index.
     * @throws UsageException If the argument is invalid.
     */
    public static int parseTaskIndex(CommandType commandType, String argument, int taskCount)
            throws UsageException {
        if (argument == null || argument.isBlank()) {
            throw usageError(new UsageRequest(commandType, "task number", "<missing>",
                    "an integer", "<task number>"));
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException exception) {
            throw usageError(new UsageRequest(commandType, "task number", argument,
                    "an integer", "<task number>"), exception);
        }
        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw usageError(new UsageRequest(commandType, "task number", argument,
                    "an existing task number", "<task number>"));
        }
        return taskIndex;
    }

    /**
     * Parses the date argument of the date-query command.
     *
     * @param argument Raw date argument.
     * @return Parsed date.
     * @throws UsageException If the argument is invalid.
     */
    public static LocalDate parseDate(String argument) throws UsageException {
        if (argument == null || argument.isBlank()) {
            throw usageError(new UsageRequest(CommandType.ON, "date", "<missing>",
                    "yyyy-MM-dd", "<yyyy-MM-dd>"));
        }
        try {
            return dude.task.TaskDate.parseDate(argument);
        } catch (DateTimeParseException exception) {
            throw usageError(new UsageRequest(CommandType.ON, "date", argument,
                    "yyyy-MM-dd", "<yyyy-MM-dd>"), exception);
        }
    }

    /**
     * Returns whether a command accepts no argument.
     *
     * @param commandType Command type.
     * @return True for commands that reject arguments.
     */
    private static boolean requiresNoArgument(CommandType commandType) {
        return commandType == CommandType.BYE || commandType == CommandType.LIST
                || commandType == CommandType.UNDO;
    }

    /**
     * Parses a command word.
     *
     * @param action Command word.
     * @return Matching command type.
     * @throws UsageException If the command word is unknown.
     */
    private static CommandType parseType(String action) throws UsageException {
        for (CommandType commandType : CommandType.values()) {
            if (commandType.getWord().equals(action)) {
                return commandType;
            }
        }
        throw new UsageException(new UsageDetails(action, "command", action,
                SUPPORTED_COMMANDS, COMMAND_USAGE, "<task type>"));
    }

    /**
     * Rejects arguments for a command that intentionally takes none.
     *
     * @param commandType Command being parsed.
     * @param argument   Raw argument, if supplied.
     * @throws UsageException If an argument was supplied.
     */
    private static void requireNoArgument(CommandType commandType, String argument)
            throws UsageException {
        if (argument != null && !argument.isBlank()) {
            throw new UsageException(new UsageDetails(commandType.getWord(), "argument", argument,
                    "no arguments", commandType.getUsageMessage(), commandType.getWord()));
        }
    }

    /**
     * Creates a usage exception without a parsing cause.
     *
     * @param request Usage-error context.
     * @return Structured usage exception.
     */
    private static UsageException usageError(UsageRequest request) {
        return new UsageException(request.toDetails());
    }

    /**
     * Creates a usage exception with a parsing cause.
     *
     * @param request Usage-error context.
     * @param cause        Parsing cause.
     * @return Structured usage exception.
     */
    private static UsageException usageError(UsageRequest request, Throwable cause) {
        return new UsageException(request.toDetails(), cause);
    }

    /**
     * Groups the context needed to create one structured usage error.
     *
     * @param commandType  Command type.
     * @param fieldName    Invalid field.
     * @param actualValue  Supplied value.
     * @param expectedType Expected format.
     * @param usageToken   Usage token to highlight.
     */
    private record UsageRequest(CommandType commandType, String fieldName, String actualValue,
            String expectedType, String usageToken) {
        /**
         * Converts this request into the shared structured usage-details value.
         *
         * @return Structured usage details.
         */
        private UsageDetails toDetails() {
            return new UsageDetails(commandType.getWord(), fieldName, actualValue,
                    expectedType, commandType.getUsageMessage(), usageToken);
        }
    }
}
