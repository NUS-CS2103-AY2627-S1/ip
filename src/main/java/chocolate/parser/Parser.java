package chocolate.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import chocolate.exception.ChocolateException;
import chocolate.task.Deadline;
import chocolate.task.Event;
import chocolate.task.Todo;

/**
 * Converts raw user input into commands that the application can execute.
 */
public class Parser {
    private static final String INVALID_COMMAND_MESSAGE =
            "That is not a valid command. Please try any of these: todo, deadline, event, "
                    + "list, mark, unmark, delete, find, archive, or bye.";
    private static final String ARCHIVE_USAGE_MESSAGE = "Please use: archive or archive list.";

    /**
     * Prevents instantiation of this utility class.
     */
    private Parser() {
    }

    /**
     * Parses a complete line of user input.
     *
     * @param input User-entered command line.
     * @return Structured command ready for execution.
     * @throws ChocolateException If the command or its arguments are invalid.
     */
    public static ParsedCommand parse(String input) throws ChocolateException {
        input = input.trim().replaceAll("\\s+", " ");
        if (input.equals("bye")) {
            return ParsedCommand.createSimple(CommandType.BYE);
        } else if (input.equals("list")) {
            return ParsedCommand.createSimple(CommandType.LIST);
        } else if (input.equals("archive")) {
            return ParsedCommand.createSimple(CommandType.ARCHIVE);
        } else if (input.equals("archive list")) {
            return ParsedCommand.createSimple(CommandType.ARCHIVE_LIST);
        } else if (isCommand(input, "archive")) {
            throw new ChocolateException(ARCHIVE_USAGE_MESSAGE);
        } else if (isCommand(input, "mark")) {
            return ParsedCommand.createWithIndex(CommandType.MARK, parseTaskIndex(input, "mark"));
        } else if (isCommand(input, "unmark")) {
            return ParsedCommand.createWithIndex(
                    CommandType.UNMARK, parseTaskIndex(input, "unmark"));
        } else if (isCommand(input, "todo")) {
            return ParsedCommand.createWithTask(CommandType.TODO, parseTodo(input));
        } else if (isCommand(input, "deadline")) {
            return ParsedCommand.createWithTask(CommandType.DEADLINE, parseDeadline(input));
        } else if (isCommand(input, "event")) {
            return ParsedCommand.createWithTask(CommandType.EVENT, parseEvent(input));
        } else if (isCommand(input, "delete")) {
            return ParsedCommand.createWithIndex(
                    CommandType.DELETE, parseTaskIndex(input, "delete"));
        } else if (isCommand(input, "find")) {
            return ParsedCommand.createWithKeyword(CommandType.FIND, parseKeyword(input));
        }
        throw new ChocolateException(INVALID_COMMAND_MESSAGE);
    }

    /**
     * Returns whether the input begins with a complete command word.
     *
     * @param input Complete user input.
     * @param commandWord Command word to match.
     * @return True when the input is the command or begins with the command followed by a space.
     */
    private static boolean isCommand(String input, String commandWord) {
        return input.equals(commandWord) || input.startsWith(commandWord + " ");
    }

    /**
     * Parses a todo command into a task.
     *
     * @param input Complete todo command.
     * @return Todo described by the command.
     * @throws ChocolateException If the description is empty.
     */
    private static Todo parseTodo(String input) throws ChocolateException {
        String description = input.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new ChocolateException(
                    "Please provide a valid description after the command you used!");
        }
        return new Todo(description);
    }

    /**
     * Parses a deadline command into a dated task.
     *
     * @param input Complete deadline command.
     * @return Deadline described by the command.
     * @throws ChocolateException If the command structure or date is invalid.
     */
    private static Deadline parseDeadline(String input) throws ChocolateException {
        String details = input.substring("deadline".length()).trim();
        int byIndex = details.indexOf(" /by ");
        if (countOccurrences(details, " /by ") != 1
                || byIndex <= 0 || byIndex + " /by ".length() >= details.length()) {
            throw new ChocolateException("Please use: deadline DESCRIPTION /by yyyy-MM-dd.");
        }

        String description = details.substring(0, byIndex);
        String dateText = details.substring(byIndex + " /by ".length());
        try {
            return new Deadline(description, LocalDate.parse(dateText));
        } catch (DateTimeParseException e) {
            throw new ChocolateException("Please use the date format yyyy-MM-dd.");
        }
    }

    /**
     * Parses an event command into a task with start and end details.
     *
     * @param input Complete event command.
     * @return Event described by the command.
     * @throws ChocolateException If the command structure is invalid.
     */
    private static Event parseEvent(String input) throws ChocolateException {
        String details = input.substring("event".length()).trim();
        int fromIndex = details.indexOf(" /from ");
        int toIndex = details.indexOf(" /to ");
        if (countOccurrences(details, " /from ") != 1 || countOccurrences(details, " /to ") != 1
                || fromIndex <= 0 || toIndex <= fromIndex + " /from ".length()
                || toIndex + " /to ".length() >= details.length()) {
            throw new ChocolateException("Please use: event DESCRIPTION /from START /to END.");
        }

        String description = details.substring(0, fromIndex);
        String from = details.substring(fromIndex + " /from ".length(), toIndex);
        String to = details.substring(toIndex + " /to ".length());
        validateEventDateRange(from, to);
        return new Event(description, from, to);
    }

    /**
     * Rejects ISO-date event ranges whose end is not after their start.
     * Non-date event details remain supported as free-form text.
     *
     * @param from Start detail supplied by the user.
     * @param to End detail supplied by the user.
     * @throws ChocolateException If two ISO dates form an invalid range.
     */
    private static void validateEventDateRange(String from, String to) throws ChocolateException {
        try {
            LocalDate startDate = LocalDate.parse(from);
            LocalDate endDate = LocalDate.parse(to);
            if (!startDate.isBefore(endDate)) {
                throw new ChocolateException("The event end date must be after its start date.");
            }
        } catch (DateTimeParseException e) {
            // Event date/time details are intentionally allowed to be free-form text.
        }
    }

    /**
     * Extracts and converts a one-based task number into a zero-based index.
     *
     * @param input Complete indexed command.
     * @param commandWord Command word preceding the task number.
     * @return Zero-based task index.
     * @throws ChocolateException If the task number is missing or not an integer.
     */
    private static int parseTaskIndex(String input, String commandWord)
            throws ChocolateException {
        String numberText = input.substring(commandWord.length()).trim();
        if (numberText.isEmpty()) {
            throw new ChocolateException("Please provide a task number!");
        }
        try {
            int taskNumber = Integer.parseInt(numberText);
            if (taskNumber < 1) {
                throw new ChocolateException("Task number must be at least 1.");
            }
            return taskNumber - 1;
        } catch (NumberFormatException e) {
            throw new ChocolateException("Please provide a whole number for the task number!");
        }
    }

    /**
     * Counts non-overlapping appearances of a parameter marker.
     *
     * @param text Text to inspect.
     * @param marker Parameter marker to count.
     * @return Number of appearances of the marker.
     */
    private static int countOccurrences(String text, String marker) {
        int count = 0;
        int index = text.indexOf(marker);
        while (index >= 0) {
            count++;
            index = text.indexOf(marker, index + marker.length());
        }
        return count;
    }

    /**
     * Extracts a non-empty keyword from a find command.
     *
     * @param input Complete find command.
     * @return Keyword to search for.
     * @throws ChocolateException If the keyword is empty.
     */
    private static String parseKeyword(String input) throws ChocolateException {
        String keyword = input.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new ChocolateException("Please provide a keyword to find!");
        }
        return keyword;
    }
}
