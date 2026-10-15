package apollo.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import apollo.task.Deadline;
import apollo.task.Event;
import apollo.task.Todo;

/** Converts raw user commands into command types, arguments, and tasks. */
public class Parser {
    /** Commands recognized by Apollo. */
    public enum Command {
        LIST,
        MARK,
        UNMARK,
        DELETE,
        TODO,
        EVENT,
        DEADLINE,
        DUE_TODAY,
        ONGOING_NOW,
        DUE_THIS_DATE,
        FIND,
        UNDO,
        BYE
    }

    /**
     * Extracts and identifies the command word at the start of an input line.
     *
     * @param input complete command entered by the user
     * @return the corresponding command
     * @throws IllegalArgumentException if the command word is not recognized
     */
    public Command parseCommand(String input) {
        String commandWord = input.trim().split("\\s+", 2)[0];
        commandWord = switch (commandWord.toUpperCase()) {
            case "DUETODAY" -> "DUE_TODAY";
            case "ONGOINGNOW" -> "ONGOING_NOW";
            case "DUETHISDATE" -> "DUE_THIS_DATE";
            default -> commandWord;
        };
        return Command.valueOf(commandWord.toUpperCase());
    }

    /**
     * Parses a one-based task number and converts it to a zero-based list index.
     *
     * @param input command containing the task number as its second token
     * @return zero-based task index
     * @throws NumberFormatException if the task number is not an integer
     */
    public int parseIndex(String input) {
        String[] inputArgs = input.trim().split("\\s+");
        return Integer.parseInt(inputArgs[1]) - 1;
    }

    /**
     * Creates a todo from a todo command.
     *
     * @param input command in the form {@code todo DESCRIPTION}
     * @return the parsed todo
     * @throws IllegalArgumentException if the description is empty
     */
    public Todo parseTodo(String input) {
        String description = input.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new IllegalArgumentException("Todo description cannot be empty");
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline from a deadline command.
     *
     * @param input command in the form {@code deadline DESCRIPTION /by DATE_TIME}
     * @return the parsed deadline
     * @throws java.time.format.DateTimeParseException if the date and time is invalid
     * @throws IllegalArgumentException if the deadline description is empty
     */
    public Deadline parseDeadline(String input) {
        String deadlineArgs = input.substring("deadline".length()).trim();
        int byIndex = deadlineArgs.indexOf("/by");
        String description = deadlineArgs.substring(0, byIndex).trim();
        if (description.isEmpty()) {
            throw new IllegalArgumentException("Deadline description cannot be empty");
        }
        String dateString = deadlineArgs.substring(byIndex + "/by".length()).trim();
        LocalDateTime by = DateParser.parseDateTime(dateString, LocalTime.of(23, 59));
        return new Deadline(description, by);
    }

    /**
     * Creates an event from an event command.
     *
     * @param input command in the form {@code event DESCRIPTION /from DATE_TIME /to DATE_TIME}
     * @return the parsed event
     * @throws java.time.format.DateTimeParseException if either date and time is invalid
     * @throws IllegalArgumentException if the description is empty or the event ends before it starts
     */
    public Event parseEvent(String input) {
        String eventArgs = input.substring("event".length()).trim();
        int fromIndex = eventArgs.indexOf("/from");
        int toIndex = eventArgs.indexOf("/to");
        String description = eventArgs.substring(0, fromIndex).trim();
        if (description.isEmpty()) {
            throw new IllegalArgumentException("Event description cannot be empty");
        }
        String fromString = eventArgs.substring(fromIndex + "/from".length(), toIndex).trim();
        String toString = eventArgs.substring(toIndex + "/to".length()).trim();
        LocalDateTime from = DateParser.parseDateTime(fromString, LocalTime.MIDNIGHT);
        LocalDateTime to = DateParser.parseDateTime(toString, LocalTime.of(23, 59));

        if (to.isBefore(from)) {
            throw new IllegalArgumentException("Event end cannot be before its start");
        }
        return new Event(description, from, to);
    }

    /**
     * Parses the date argument of a {@code dueThisDate} command.
     *
     * @param input command containing a date in {@code d/M/yyyy} format
     * @return the parsed date
     * @throws java.time.format.DateTimeParseException if the date is invalid
     */
    public LocalDate parseDueDate(String input) {
        String dateString = input.substring("dueThisDate".length()).trim();
        return DateParser.parseDate(dateString);
    }

    /**
     * Parses command to get the search text.
     *
     * @param input command line input
     * @return search text used to filter for tasks
     */
    public String parseFindText(String input) {
        String searchText = input.substring("find".length()).trim();

        if (searchText.isEmpty()) {
            throw new IllegalArgumentException("Search text cannot be empty");
        }

        return searchText;
    }
}
