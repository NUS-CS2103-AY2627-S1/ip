package bob;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import bob.command.AddCommand;
import bob.command.Command;
import bob.command.DeleteCommand;
import bob.command.ExitCommand;
import bob.command.FindCommand;
import bob.command.ListCommand;
import bob.command.MarkCommand;
import bob.command.UnmarkCommand;
import bob.task.Deadline;
import bob.task.Event;
import bob.task.Priority;
import bob.task.Task;
import bob.task.Todo;

/**
 * Interprets user input and converts it into commands that Bob can execute.
 */
public class Parser {
    private static final DateTimeFormatter EVENT_INPUT_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);

    private Parser() {
        // This class contains only stateless parsing operations.
    }

    /**
     * Normalizes spacing, then parses one line of input and validates its command syntax.
     *
     * @param input command entered by the user
     * @return command data ready for Bob to execute
     * @throws BobException if the command or any of its arguments is invalid
     */
    public static Command parse(String input) throws BobException {
        input = input.strip().replaceAll("[ \\t]+", " ");
        if (input.equals("bye")) {
            return new ExitCommand();
        } else if (input.equals("list")) {
            return new ListCommand();
        } else if (input.equals("find") || input.startsWith("find ")) {
            return new FindCommand(parseKeyword(input));
        } else if (input.equals("mark") || input.startsWith("mark ")) {
            return new MarkCommand(parseTaskNumber(input, "mark"));
        } else if (input.equals("unmark") || input.startsWith("unmark ")) {
            return new UnmarkCommand(parseTaskNumber(input, "unmark"));
        } else if (input.equals("delete") || input.startsWith("delete ")) {
            return new DeleteCommand(parseTaskNumber(input, "delete"));
        } else if (input.equals("todo") || input.startsWith("todo ")) {
            return parseAdd(input, "todo");
        } else if (input.equals("deadline") || input.startsWith("deadline ")) {
            return parseAdd(input, "deadline");
        } else if (input.equals("event") || input.startsWith("event ")) {
            return parseAdd(input, "event");
        }
        throw new BobException(
                "pls try either one of list, find, todo, deadline, event, mark, unmark, delete, or bye");
    }

    /**
     * Removes an optional final priority field before parsing existing task details.
     *
     * @param input complete user input
     * @param type task command word
     * @return command containing the prioritized task
     * @throws BobException if task details or priority are invalid
     */
    private static AddCommand parseAdd(String input, String type) throws BobException {
        Priority priority = Priority.NONE;
        java.util.regex.Matcher marker = java.util.regex.Pattern.compile(" /priority(?=\\s|$)")
                .matcher(input);
        if (marker.find()) {
            String label = input.substring(marker.end()).trim();
            try {
                priority = Priority.fromLabel(label);
            } catch (IllegalArgumentException exception) {
                throw new BobException("pls end the task with /priority low, moderate, high, or none");
            }
            input = input.substring(0, marker.start()).trim();
        }
        Task task = switch (type) {
            case "todo" -> parseTodo(input, priority);
            case "deadline" -> parseDeadline(input, priority);
            case "event" -> parseEvent(input, priority);
            default -> throw new IllegalArgumentException("Unknown task type: " + type);
        };
        return new AddCommand(task);
    }

    /**
     * Extracts and validates the keyword following the find command.
     *
     * @param input complete user input
     * @return keyword to search for
     * @throws BobException if the keyword is missing
     */

    private static String parseKeyword(String input) throws BobException {
        String keyword = input.substring("find".length()).trim();
        requireNotEmpty(keyword, "can't find something if idk what it is");
        return keyword;
    }

    /**
     * Extracts and validates the one-based task number following a command word.
     *
     * @param input complete user input
     * @param command command word preceding the number
     * @return parsed one-based task number
     * @throws BobException if the number is missing or is not an integer
     */
    private static int parseTaskNumber(String input, String command) throws BobException {
        String numberText = input.substring(command.length()).trim();
        if (numberText.isEmpty()) {
            throw new BobException("can't help if idk which task no");
        }

        final int taskNumber;
        if (!numberText.matches("[0-9]+")) {
            throw new BobException("enter a valid task no pls");
        }
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new BobException("enter a valid task no pls");
        }
        return taskNumber;
    }

    /**
     * Parses a to-do command and requires a non-empty description.
     *
     * @param input complete user input
     * @param priority task priority
     * @return to-do task described by the input
     * @throws BobException if the description is empty
     */
    private static Todo parseTodo(String input, Priority priority) throws BobException {
        String description = input.substring("todo".length()).trim();
        requireNotEmpty(description, "a todo needs a desc, eg: todo sleep");
        return new Todo(description, priority);
    }

    /**
     * Parses a deadline command containing a description and ISO date.
     *
     * @param input complete user input
     * @param priority task priority
     * @return deadline task described by the input
     * @throws BobException if required fields are missing or the date is invalid
     */
    private static Deadline parseDeadline(String input, Priority priority) throws BobException {
        int byPosition = input.indexOf(" /by");
        if (byPosition == -1) {
            throw new BobException("a deadline needs /by and a date, eg play /by 2019-12-02");
        }
        String description = input.substring("deadline".length(), byPosition).trim();
        String by = input.substring(byPosition + " /by".length()).trim();
        if (by.contains("/by") || description.contains("/by") || description.contains("/from")
                || description.contains("/to")) {
            throw new BobException("use /by exactly once after the deadline desc");
        }
        requireNotEmpty(description, "pls gimme a desc before /by");
        requireNotEmpty(by, "pls gimme a date after /by");
        if (!by.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            throw new BobException("wrong format for date !! here’s an eg: 2019-12-02");
        }
        try {
            return new Deadline(description, LocalDate.parse(by), priority);
        } catch (DateTimeParseException exception) {
            throw new BobException("date doesn't exist, try again");
        }
    }

    /**
     * Parses an event command containing a description, start, and end time.
     *
     * @param input complete user input
     * @param priority task priority
     * @return event task described by the input
     * @throws BobException if required fields are missing, a date-time is invalid,
     *         or the event ends before it starts
     */
    private static Event parseEvent(String input, Priority priority) throws BobException {
        int fromPosition = input.indexOf(" /from");
        int toPosition = input.indexOf(" /to");
        if (fromPosition == -1 || toPosition == -1 || toPosition < fromPosition) {
            throw new BobException(
                    "an event needs /from and /to, eg event meeting /from 2019-12-02 1400 /to 2019-12-02 1600");
        }
        String description = input.substring("event".length(), fromPosition).trim();
        String from = input.substring(fromPosition + " /from".length(), toPosition).trim();
        String to = input.substring(toPosition + " /to".length()).trim();
        if (description.contains("/from") || description.contains("/to") || from.contains("/from")
                || from.contains("/to") || to.contains("/from") || to.contains("/to")) {
            throw new BobException("use /from and /to exactly once in that order");
        }
        requireNotEmpty(description, "pls gimme event desc before /from.");
        requireNotEmpty(from, "pls gimme start time after /from.");
        requireNotEmpty(to, "pls gimme end time after /to.");
        String eventDateTimePattern = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{4}";
        if (!from.matches(eventDateTimePattern) || !to.matches(eventDateTimePattern)) {
            throw new BobException("wrong format !! here’s an eg: 2019-12-02 1800");
        }
        try {
            LocalDateTime start = LocalDateTime.parse(from, EVENT_INPUT_FORMAT);
            LocalDateTime end = LocalDateTime.parse(to, EVENT_INPUT_FORMAT);
            if (end.isBefore(start)) {
                throw new BobException("how can an event end before it starts?");
            }
            return new Event(description, start, end, priority);
        } catch (DateTimeParseException exception) {
            throw new BobException("date or time doesn't exist, try again");
        }
    }

    /**
     * Rejects an empty required command field with the supplied user-facing message.
     *
     * @param value field value to validate
     * @param message message to use when validation fails
     * @throws BobException if {@code value} is empty
     */
    private static void requireNotEmpty(String value, String message) throws BobException {
        if (value.isEmpty()) {
            throw new BobException(message);
        }
    }
}
