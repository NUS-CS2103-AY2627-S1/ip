package tungtung;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Classifies commands and creates tasks from user input. */
public class Parser {
    private static final String INVALID_DEADLINE = "OOPS!!! Use: deadline DESCRIPTION /by yyyy-MM-dd.";
    private static final String INVALID_EVENT = "OOPS!!! Use: event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd.";
    private static final String INVALID_DATE = "OOPS!!! Dates must use yyyy-MM-dd, for example 2019-10-15.";
    private static final String INVALID_EVENT_DATE_RANGE =
            "OOPS!!! An event's end date cannot be before its start date.";
    private static final String INVALID_FILE_SEPARATOR = "OOPS!!! Task details cannot contain \" | \".";
    /** The command categories understood by Tung Tung. */
    public enum CommandType { LIST, FIND, SORT, MARK, UNMARK, DELETE, ADD }

    /**
     * Identifies which operation a command requests.
     *
     * @param input complete command entered by the user
     * @return command category for the input
     */
    public CommandType identify(String input) {
        if (input.equals("list")) {
            return CommandType.LIST;
        }
        if (input.equals("find") || input.startsWith("find ")) {
            return CommandType.FIND;
        }
        if (input.equals("sort") || input.startsWith("sort ")) {
            return CommandType.SORT;
        }
        if (input.equals("mark") || input.startsWith("mark ")) {
            return CommandType.MARK;
        }
        if (input.equals("unmark") || input.startsWith("unmark ")) {
            return CommandType.UNMARK;
        }
        if (input.equals("delete") || input.startsWith("delete ")) {
            return CommandType.DELETE;
        }
        return CommandType.ADD;
    }

    /** Creates a task from a todo, deadline, or event command. */
    public Task parseTask(String input) throws TungTungException {
        if (input == null || input.contains("\n") || input.contains("\r")) {
            throw new TungTungException("OOPS!!! Enter one command on a single line.");
        }
        if (!StandardCharsets.UTF_8.newEncoder().canEncode(input)) {
            throw new TungTungException("OOPS!!! Task details contain invalid Unicode text.");
        }
        if (input.equals("todo") || input.equals("todo ")) {
            throw new TungTungException("OOPS!!! There is nothing TODO.");
        }
        if (input.startsWith("todo ")) {
            String description = input.substring(5);
            if (description.isBlank()) {
                throw new TungTungException("OOPS!!! There is nothing TODO.");
            }
            validateTaskText(description);
            return new ToDo(description);
        }
        if (input.equals("deadline")) {
            throw new TungTungException(INVALID_DEADLINE);
        }
        if (input.startsWith("deadline ")) {
            return parseDeadline(input.substring(9));
        }
        if (input.equals("event")) {
            throw new TungTungException(INVALID_EVENT);
        }
        if (input.startsWith("event ")) {
            return parseEvent(input.substring(6));
        }
        throw new TungTungException("OOPS!!! IDK what u are on about :-()");
    }

    private Deadline parseDeadline(String input) throws TungTungException {
        String[] parts = input.split(" /by ", 2);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new TungTungException(INVALID_DEADLINE);
        }
        assert parts.length == 2 : "A validated deadline command has description and date fields.";
        validateTaskText(parts[0]);
        validateTaskText(parts[1]);
        return new Deadline(parts[0], parseDate(parts[1]));
    }

    private Event parseEvent(String input) throws TungTungException {
        int fromIndex = input.indexOf(" /from ");
        int toIndex = input.indexOf(" /to ");
        if (fromIndex < 0 || toIndex < fromIndex + 7
                || input.indexOf(" /from ", fromIndex + 1) >= 0
                || input.indexOf(" /to ", toIndex + 1) >= 0) {
            throw new TungTungException(INVALID_EVENT);
        }
        String[] parts = {input.substring(0, fromIndex),
            input.substring(fromIndex + 7, toIndex), input.substring(toIndex + 5)};
        if (parts.length != 3 || parts[0].isBlank() || parts[1].isBlank() || parts[2].isBlank()) {
            throw new TungTungException(INVALID_EVENT);
        }
        assert parts.length == 3 : "A validated event command has description, start, and end fields.";
        validateTaskText(parts[0]);
        validateTaskText(parts[1]);
        validateTaskText(parts[2]);
        LocalDate from = parseDate(parts[1]);
        LocalDate to = parseDate(parts[2]);
        if (to.isBefore(from)) {
            throw new TungTungException(INVALID_EVENT_DATE_RANGE);
        }
        assert !to.isBefore(from) : "An accepted event must have a non-reversed date range.";
        return new Event(parts[0], from, to);
    }

    private LocalDate parseDate(String dateText) throws TungTungException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new TungTungException(INVALID_DATE);
        }
    }

    private void validateTaskText(String text) throws TungTungException {
        if ((text + " ").contains(" | ")) {
            throw new TungTungException(INVALID_FILE_SEPARATOR);
        }
    }
}
