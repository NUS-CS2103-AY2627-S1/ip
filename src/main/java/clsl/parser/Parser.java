package clsl.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import clsl.ClslException;

/**
 * Interprets the parts of commands entered by the user.
 */
public class Parser {
    /** Prevents this utility class from being instantiated. */
    private Parser() {
    }

    /**
     * Parses a full command into the information needed to execute it.
     *
     * @param userInput Complete command entered by the user.
     * @return A structured representation of the command.
     * @throws ClslException If the command is incomplete or invalid.
     */
    public static ParsedCommand parse(String userInput) throws ClslException {
        if (userInput == null || userInput.isBlank()) {
            throw new ClslException("command cannot be blank");
        }

        String[] commandParts = userInput.trim().split("\\s+", 2);
        String commandWord = commandParts[0];
        String arguments = commandParts.length == 2 ? commandParts[1].trim() : "";

        switch (commandWord) {
            case "list":
                requireNoArguments(arguments);
                return ParsedCommand.of(ParsedCommand.Type.LIST);
            case "bye":
                requireNoArguments(arguments);
                return ParsedCommand.of(ParsedCommand.Type.BYE);
            case "mark":
                return ParsedCommand.withTaskIndex(ParsedCommand.Type.MARK,
                        parseTaskIndex(arguments));
            case "unmark":
                return ParsedCommand.withTaskIndex(ParsedCommand.Type.UNMARK,
                        parseTaskIndex(arguments));
            case "delete":
                return ParsedCommand.withTaskIndex(ParsedCommand.Type.DELETE,
                        parseTaskIndex(arguments));
            case "todo":
                return ParsedCommand.withDescription(ParsedCommand.Type.TODO,
                        requireDescription(arguments, "todo what exactly?"));
            case "deadline":
                return parseDeadline(arguments);
            case "event":
                return parseEvent(arguments);
            case "on":
                return ParsedCommand.withDescriptionAndDate(ParsedCommand.Type.ON,
                        null,
                        parseDate(arguments, "on what date?"));
            case "find":
                return ParsedCommand.withDescription(ParsedCommand.Type.FIND,
                        requireDescription(arguments, "find what?"));
            default:
                throw new ClslException("I don't understand");
        }
    }

    private static ParsedCommand parseDeadline(String arguments) throws ClslException {
        String[] deadlineParts = arguments.split("\\s*/by\\s*", 2);
        String description = requireDescription(deadlineParts[0], "deadline of what?");
        if (deadlineParts.length < 2) {
            throw new ClslException("by when?");
        }

        LocalDate byDate = parseDate(deadlineParts[1], "by when?");
        return ParsedCommand.withDescriptionAndDate(ParsedCommand.Type.DEADLINE,
                description, byDate);
    }

    private static ParsedCommand parseEvent(String arguments) throws ClslException {
        String[] fromParts = arguments.split("\\s*/from\\s*", 2);
        String description = requireDescription(fromParts[0], "event of what?");
        if (fromParts.length < 2) {
            throw new ClslException("from when?");
        }

        String[] toParts = fromParts[1].split("\\s*/to\\s*", 2);
        if (toParts.length < 2) {
            throw new ClslException("to when?");
        }

        LocalDate startDate = parseDate(toParts[0], "from when?");
        LocalDate endDate = parseDate(toParts[1], "to when?");
        if (startDate.isAfter(endDate)) {
            throw new ClslException("from date cannot be later than to date");
        }
        return ParsedCommand.withEventDetails(description, startDate, endDate);
    }

    private static void requireNoArguments(String arguments) throws ClslException {
        if (!arguments.isEmpty()) {
            throw new ClslException("I don't understand");
        }
    }

    private static String requireDescription(String description, String errorMessage)
            throws ClslException {
        if (description.isBlank()) {
            throw new ClslException(errorMessage);
        }
        return description.trim();
    }

    private static int parseTaskIndex(String taskNumberText) throws ClslException {
        if (taskNumberText.isBlank()) {
            throw new ClslException("task number missing");
        }

        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1) {
                throw new ClslException("task number must be at least 1");
            }
            return taskNumber - 1;
        } catch (NumberFormatException e) {
            throw new ClslException("task number must be a whole number");
        }
    }

    private static LocalDate parseDate(String dateText, String missingDateMessage)
            throws ClslException {
        if (dateText.isBlank()) {
            throw new ClslException(missingDateMessage);
        }

        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            throw new ClslException("Please use yyyy-mm-dd format");
        }
    }
}
