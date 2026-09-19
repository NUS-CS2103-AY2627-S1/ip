package chai;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chai.task.Deadline;
import chai.task.Event;
import chai.task.Todo;

/** Converts raw command text into command types, task data, and indexes. */
public final class Parser {
    /** Deadline syntax with flexible whitespace and case-insensitive markers. */
    private static final Pattern DEADLINE_COMMAND = Pattern.compile(
            "^deadline\\s+(.+?)\\s+/by\\s+(.+)$", Pattern.CASE_INSENSITIVE);

    /** Event syntax with flexible whitespace and case-insensitive markers. */
    private static final Pattern EVENT_COMMAND = Pattern.compile(
            "^event\\s+(.+?)\\s+/from\\s+(.+?)\\s+/to\\s+(.+)$", Pattern.CASE_INSENSITIVE);

    /** Prevents instantiation of this command parsing utility class. */
    private Parser() {
    }

    /** Returns the command type represented by the first word of the command. */
    public static CommandType parseCommandType(String command) {
        String keyword = command.split("\\s+", 2)[0];
        return CommandType.fromKeyword(keyword);
    }

    /** Parses a todo command into a todo task. */
    public static Todo parseTodo(String command) throws ChaiException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new ChaiException("A todo needs a description. Try: todo <description>");
        }
        return new Todo(description);
    }

    /** Parses a deadline command containing an ISO date. */
    public static Deadline parseDeadline(String command) throws ChaiException {
        Matcher matcher = DEADLINE_COMMAND.matcher(command);
        if (!matcher.matches()) {
            throw new ChaiException("Use: deadline <description> /by <yyyy-MM-dd>");
        }

        String description = matcher.group(1).trim();
        String byText = matcher.group(2).trim();
        if (description.isEmpty() || byText.isEmpty()) {
            throw new ChaiException("A deadline needs both a description and a date.");
        }

        try {
            return new Deadline(description, LocalDate.parse(byText));
        } catch (DateTimeParseException e) {
            throw new ChaiException("The deadline date must use yyyy-MM-dd, for example 2019-12-02.");
        }
    }

    /** Parses an event command containing ISO start and end dates. */
    public static Event parseEvent(String command) throws ChaiException {
        Matcher matcher = EVENT_COMMAND.matcher(command);
        if (!matcher.matches()) {
            throw new ChaiException("Use: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>");
        }

        String description = matcher.group(1).trim();
        String fromText = matcher.group(2).trim();
        String toText = matcher.group(3).trim();
        if (description.isEmpty() || fromText.isEmpty() || toText.isEmpty()) {
            throw new ChaiException("An event needs a description, start date, and end date.");
        }

        LocalDate from;
        LocalDate to;
        try {
            from = LocalDate.parse(fromText);
            to = LocalDate.parse(toText);
        } catch (DateTimeParseException e) {
            throw new ChaiException("Event dates must use yyyy-MM-dd, for example 2019-12-02.");
        }
        if (to.isBefore(from)) {
            throw new ChaiException("The event end date cannot be before its start date.");
        }
        return new Event(description, from, to);
    }

    /** Parses a one-based task number and converts it to a valid list index. */
    public static int parseTaskIndex(String command, String action, int taskCount) throws ChaiException {
        String[] parts = command.split("\\s+");
        if (parts.length != 2) {
            throw new ChaiException("Please use the format: " + action + " <task number>");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new ChaiException("The task number must be a whole number.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new ChaiException("That task number does not exist. Please choose a number from 1 to "
                    + taskCount + ".");
        }
        return taskNumber - 1;
    }

    /** Parses and returns the nonempty keyword from a find command. */
    public static String parseFindKeyword(String command) throws ChaiException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new ChaiException("Please use the format: find <keyword>");
        }
        return keyword;
    }

    /** Checks that a command contains only its keyword. */
    public static void requireNoArguments(String command, String keyword) throws ChaiException {
        if (!command.equalsIgnoreCase(keyword)) {
            throw new ChaiException("Use: " + keyword);
        }
    }
}
