package janet.logic;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import janet.exception.InvalidCommandException;
import janet.exception.InvalidTaskException;
import janet.task.Deadline;
import janet.task.Event;
import janet.task.TaskDateTime;
import janet.task.Todo;

/**
 * Converts user-entered command text into executable commands.
 */
public class Parser {
    /**
     * Prevents construction of this utility class.
     */
    private Parser() {
    }

    /**
     * Parses a complete user command.
     *
     * @param userInput command text entered by the user.
     * @return a command ready for execution
     */
    public static Command parse(String userInput) {
        assert userInput != null : "User input must come from an available UI command";

        String normalizedInput = userInput.strip();
        if (normalizedInput.isEmpty()) {
            throw new InvalidCommandException("Please enter a command.");
        }

        CommandName commandName = CommandName.from(normalizedInput);
        return switch (commandName) {
            case LIST -> {
                requireNoArgument(normalizedInput, "list");
                yield new ListCommand();
            }
            case TODO -> new AddCommand(parseTodo(normalizedInput));
            case EVENT -> parseEvent(normalizedInput);
            case DEADLINE -> parseDeadline(normalizedInput);
            case DELETE -> new DeleteCommand(parseTaskNumbers(normalizedInput));
            case MARK -> new MarkCommand(parseTaskNumber(normalizedInput));
            case UNMARK -> new UnmarkCommand(parseTaskNumber(normalizedInput));
            case FIND -> new FindCommand(parseKeyword(normalizedInput));
            case BYE -> {
                requireNoArgument(normalizedInput, "bye");
                yield new ExitCommand();
            }
            case UNKNOWN -> throw new InvalidCommandException("OOPS!!! I don't recognize that command.");
        };
    }

    /**
     * Rejects extra text supplied to a command that takes no argument.
     *
     * @param userInput complete command text.
     * @param commandName command that must not receive an argument.
     */
    private static void requireNoArgument(String userInput, String commandName) {
        if (!parseArgument(userInput).isEmpty()) {
            throw new InvalidCommandException("Sorry, " + commandName + " does not take any arguments.");
        }
    }

    /**
     * Parses a to-do command.
     *
     * @param userInput complete command text.
     * @return the to-do task described by the command
     */
    private static Todo parseTodo(String userInput) {
        String description = parseArgument(userInput);
        if (description.isEmpty()) {
            throw new InvalidCommandException("OOPS!!! A todo needs a description.");
        }
        validateStorableText(description);
        return new Todo(description);
    }

    /**
     * Parses an event command.
     *
     * @param userInput complete command text.
     * @return a command that adds the event
     */
    private static AddCommand parseEvent(String userInput) {
        String eventCommand = parseArgument(userInput);
        int fromIndex = eventCommand.indexOf(" /from ");
        int toIndex = eventCommand.indexOf(" /to ");
        if (fromIndex <= 0 || toIndex <= fromIndex) {
            throw new InvalidCommandException("Sorry, please use: event <task> /from <start> /to <end>.");
        }

        String description = eventCommand.substring(0, fromIndex).trim();
        String start = toIndex >= fromIndex + 7
                ? eventCommand.substring(fromIndex + 7, toIndex).trim()
                : "";
        String end = eventCommand.substring(toIndex + 5).trim();
        if (description.isEmpty() || start.isEmpty() || end.isEmpty()) {
            throw new InvalidCommandException("OOPS!!! An event needs a description, start, and end.");
        }
        validateStorableText(description);
        validateStorableText(start);
        validateStorableText(end);

        TaskDateTime parsedStart = parseTaskDateTime(start);
        TaskDateTime parsedEnd = parseTaskDateTime(end);
        validateEventRange(parsedStart, parsedEnd);
        boolean hasPastDateTime = parsedStart.isPast() || parsedEnd.isPast();
        return new AddCommand(new Event(description, start, end), hasPastDateTime);
    }

    /**
     * Parses a deadline command.
     *
     * @param userInput complete command text.
     * @return a command that adds the deadline
     */
    private static AddCommand parseDeadline(String userInput) {
        String deadlineCommand = parseArgument(userInput);
        int byIndex = deadlineCommand.indexOf(" /by ");
        if (byIndex <= 0) {
            throw new InvalidCommandException("Sorry, please use: deadline <task> /by <date or time>.");
        }

        String description = deadlineCommand.substring(0, byIndex).trim();
        String deadlineText = deadlineCommand.substring(byIndex + 5).trim();
        if (description.isEmpty() || deadlineText.isEmpty()) {
            throw new InvalidCommandException("OOPS!!! A deadline needs a description and due time.");
        }
        validateStorableText(description);
        try {
            LocalDate deadline = LocalDate.parse(deadlineText);
            return new AddCommand(new Deadline(description, deadline), deadline.isBefore(LocalDate.now()));
        } catch (DateTimeParseException exception) {
            throw new InvalidCommandException("Sorry, please provide a deadline date in yyyy-MM-dd format.");
        }
    }

    /**
     * Parses a strictly formatted event date or time.
     *
     * @param text event start or end text.
     * @return the parsed date or time
     */
    private static TaskDateTime parseTaskDateTime(String text) {
        try {
            return TaskDateTime.parse(text);
        } catch (DateTimeParseException exception) {
            throw new InvalidCommandException("Sorry, use yyyy-MM-dd, HH:mm, or yyyy-MM-dd HH:mm.");
        }
    }

    /**
     * Rejects a recognized event range whose end precedes its start.
     *
     * @param start parsed start.
     * @param end parsed end.
     */
    private static void validateEventRange(TaskDateTime start, TaskDateTime end) {
        if (!start.hasSameKind(end)) {
            throw new InvalidCommandException("Sorry, please use the same date/time format for an event's range.");
        }
        if (end.isBefore(start)) {
            throw new InvalidCommandException("Sorry, an event's end must be equal to or later than its start.");
        }
    }

    /**
     * Parses the one-based task number at the end of a task command.
     *
     * @param userInput complete command text.
     * @return the parsed task number
     */
    private static int parseTaskNumber(String userInput) {
        String taskNumber = parseArgument(userInput);
        if (taskNumber.isEmpty()) {
            throw new InvalidTaskException("Sorry, please provide a valid task number.");
        }

        try {
            return Integer.parseInt(taskNumber);
        } catch (NumberFormatException exception) {
            throw new InvalidTaskException("Sorry, please provide a valid task number.");
        }
    }

    /**
     * Parses the one-based task numbers supplied to a mass operation.
     *
     * @param userInput complete command text.
     * @return task numbers in the order entered by the user
     */
    private static List<Integer> parseTaskNumbers(String userInput) {
        String taskNumbers = parseArgument(userInput);
        if (taskNumbers.isEmpty()) {
            throw new InvalidTaskException("Sorry, please provide a valid task number.");
        }

        String[] numberTexts = taskNumbers.split("\\s+");
        try {
            return Arrays.stream(numberTexts)
                    .map(Integer::parseInt)
                    .toList();
        } catch (NumberFormatException exception) {
            if (numberTexts.length == 1) {
                throw new InvalidTaskException("Sorry, please provide a valid task number.");
            }
            throw new InvalidTaskException("Sorry, please provide valid task numbers separated by spaces.");
        }
    }

    /**
     * Parses the keyword supplied to a find command.
     *
     * @param userInput complete command text.
     * @return the non-empty keyword to search for
     */
    private static String parseKeyword(String userInput) {
        String keyword = parseArgument(userInput);
        if (keyword.isEmpty()) {
            throw new InvalidCommandException("OOPS!!! A find command needs a keyword.");
        }
        return keyword;
    }

    /**
     * Returns the trimmed text after a command name.
     *
     * @param userInput complete command text.
     * @return the command argument, or an empty string when none was supplied
     */
    private static String parseArgument(String userInput) {
        int firstWhitespace = -1;
        for (int index = 0; index < userInput.length(); index++) {
            if (Character.isWhitespace(userInput.charAt(index))) {
                firstWhitespace = index;
                break;
            }
        }
        return firstWhitespace < 0 ? "" : userInput.substring(firstWhitespace + 1).strip();
    }

    /**
     * Rejects control characters that would corrupt Janet's line-based data format.
     *
     * @param text task text that will be saved.
     */
    private static void validateStorableText(String text) {
        if (text.indexOf('\t') >= 0 || text.indexOf('\n') >= 0 || text.indexOf('\r') >= 0) {
            throw new InvalidCommandException("Sorry, task details cannot contain tabs or line breaks.");
        }
    }
}
