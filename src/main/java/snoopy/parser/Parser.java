package snoopy.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import snoopy.command.CommandType;
import snoopy.exception.SnoopyException;
import snoopy.task.Deadline;
import snoopy.task.Event;
import snoopy.task.Task;
import snoopy.task.Todo;

/**
 * Interprets user input and converts command arguments into application data.
 */
public final class Parser {
    private Parser() {
        // This utility class stores no state and should not be instantiated.
    }

    /**
     * Normalizes nullable user input by removing surrounding whitespace.
     *
     * @param input Raw user input.
     * @return Trimmed input, or an empty string when the input is null.
     */
    public static String normalizeInput(String input) {
        return input == null ? "" : input.trim();
    }

    /**
     * Identifies the command type represented by a normalized command.
     *
     * @param command Complete normalized command.
     * @return Matching command type, or {@link CommandType#UNKNOWN}.
     */
    public static CommandType parseCommandType(String command) {
        assert command != null : "Normalized command must be provided";
        if (command.isEmpty()) {
            return CommandType.UNKNOWN;
        }

        String firstWord = command.split("\\s+", 2)[0];
        CommandType commandType = Arrays.stream(CommandType.values())
                .filter(type -> type.getKeyword().equals(firstWord))
                .findFirst()
                .orElse(CommandType.UNKNOWN);
        boolean hasArguments = commandType != CommandType.BYE && commandType != CommandType.LIST;
        if (!hasArguments && !command.equals(commandType.getKeyword())) {
            return CommandType.UNKNOWN;
        }
        return commandType;
    }

    /**
     * Returns the trimmed arguments following a recognized command keyword.
     *
     * @param command Complete normalized command.
     * @param commandType Recognized command type.
     * @return Command arguments, or an empty string when none were supplied.
     */
    public static String getArguments(String command, CommandType commandType) {
        assert command != null : "Command must be provided";
        assert commandType != null : "Command type must be provided";
        assert command.startsWith(commandType.getKeyword())
                : "Command must start with its recognized keyword";
        return command.substring(commandType.getKeyword().length()).trim();
    }

    /**
     * Parses an add command into its corresponding task subtype.
     *
     * @param command Complete normalized command.
     * @param commandType Todo, deadline, or event command type.
     * @return Task described by the command.
     * @throws SnoopyException If required task details are missing or invalid.
     */
    public static Task parseTask(String command, CommandType commandType)
            throws SnoopyException {
        switch (commandType) {
            case TODO:
                return parseTodo(command, commandType);
            case DEADLINE:
                return parseDeadline(command, commandType);
            case EVENT:
                return parseEvent(command, commandType);
            default:
                throw new IllegalArgumentException("Cannot parse a task from " + commandType);
        }
    }

    /**
     * Parses and validates a task number.
     *
     * @param numberText Task number entered by the user.
     * @param taskCount Number of tasks currently stored.
     * @param commandType Command that requires the task number.
     * @return Zero-based index of the selected task.
     * @throws SnoopyException If the number is missing, non-numeric, or outside the list.
     */
    public static int parseTaskIndex(String numberText, int taskCount, CommandType commandType)
            throws SnoopyException {
        assert taskCount >= 0 : "Task count cannot be negative";
        if (numberText.isEmpty()) {
            throw new SnoopyException(
                    "Please provide a task number, for example '"
                            + commandType.getKeyword() + " 2'.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new SnoopyException("'" + numberText + "' is not a valid task number.");
        }

        if (taskCount == 0) {
            throw new SnoopyException(
                    "Your task list is empty, so there is no task to "
                            + commandType.getKeyword() + ".");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new SnoopyException(
                    "Task " + taskNumber + " does not exist. Choose a number from 1 to "
                            + taskCount + ".");
        }
        return taskNumber - 1;
    }

    /**
     * Parses the task number and description from an update command.
     *
     * @param command Complete normalized update command.
     * @param taskCount Number of tasks currently stored.
     * @param commandType Update command type.
     * @return Parsed update details.
     * @throws SnoopyException If the task number or description is invalid.
     */
    public static UpdateDetails parseUpdate(String command, int taskCount,
            CommandType commandType) throws SnoopyException {
        String arguments = getArguments(command, commandType);
        String[] fields = arguments.split("\\s+", 2);
        if (fields.length < 2 || fields[1].isBlank()) {
            throw new SnoopyException(
                    "Please use: update <task number> <new description>.");
        }
        int taskIndex = parseTaskIndex(fields[0], taskCount, commandType);
        return new UpdateDetails(taskIndex, fields[1].trim());
    }

    private static Todo parseTodo(String command, CommandType commandType)
            throws SnoopyException {
        String description = getArguments(command, commandType);
        if (description.isEmpty()) {
            throw new SnoopyException("Please tell me what to add after 'todo'.");
        }
        return new Todo(description);
    }

    private static Deadline parseDeadline(String command, CommandType commandType)
            throws SnoopyException {
        int byIndex = command.indexOf(" /by ");
        if (byIndex < 0) {
            throw new SnoopyException("Please use: deadline <description> /by <date or time>.");
        }
        if (hasRepeatedDelimiter(command, " /by ")) {
            throw new SnoopyException("A deadline must contain exactly one '/by' separator.");
        }
        String description = command.substring(commandType.getKeyword().length(), byIndex).trim();
        String byText = command.substring(byIndex + 5).trim();
        if (description.isEmpty() || byText.isEmpty()) {
            throw new SnoopyException("A deadline needs both a description and a '/by' value.");
        }
        return new Deadline(description, parseDate(byText, commandType));
    }

    private static Event parseEvent(String command, CommandType commandType)
            throws SnoopyException {
        int fromIndex = command.indexOf(" /from ");
        int toIndex = command.indexOf(" /to ");
        if (fromIndex < 0 || toIndex < 0 || fromIndex >= toIndex) {
            throw new SnoopyException("Please use: event <description> /from <start> /to <end>.");
        }
        if (hasRepeatedDelimiter(command, " /from ") || hasRepeatedDelimiter(command, " /to ")) {
            throw new SnoopyException(
                    "An event must contain exactly one '/from' and one '/to' separator.");
        }
        if (fromIndex + 7 > toIndex) {
            throw new SnoopyException(
                    "An event needs a description, a '/from' value, and a '/to' value.");
        }
        String description = command.substring(commandType.getKeyword().length(), fromIndex).trim();
        String fromText = command.substring(fromIndex + 7, toIndex).trim();
        String toText = command.substring(toIndex + 5).trim();
        if (description.isEmpty() || fromText.isEmpty() || toText.isEmpty()) {
            throw new SnoopyException(
                    "An event needs a description, a '/from' value, and a '/to' value.");
        }

        LocalDate from = parseDate(fromText, commandType);
        LocalDate to = parseDate(toText, commandType);
        if (to.isBefore(from)) {
            throw new SnoopyException("The event end date cannot be before its start date.");
        }
        if (to.equals(from)) {
            throw new SnoopyException("The event end date must be after its start date.");
        }
        return new Event(description, from, to);
    }

    private static LocalDate parseDate(String dateText, CommandType commandType)
            throws SnoopyException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            String subject = commandType == CommandType.DEADLINE
                    ? "the deadline date" : "event dates";
            throw new SnoopyException(
                    "Please enter " + subject
                            + " as yyyy-MM-dd, for example 2019-10-15.");
        }
    }

    private static boolean hasRepeatedDelimiter(String command, String delimiter) {
        int firstIndex = command.indexOf(delimiter);
        return firstIndex >= 0 && command.indexOf(delimiter, firstIndex + delimiter.length()) >= 0;
    }

    /**
     * Contains the validated values from an update command.
     */
    public static final class UpdateDetails {
        private final int taskIndex;
        private final String description;

        private UpdateDetails(int taskIndex, String description) {
            this.taskIndex = taskIndex;
            this.description = description;
        }

        /**
         * Returns the zero-based index of the task to update.
         *
         * @return Validated task index.
         */
        public int getTaskIndex() {
            return taskIndex;
        }

        /**
         * Returns the replacement task description.
         *
         * @return Validated description.
         */
        public String getDescription() {
            return description;
        }
    }
}
