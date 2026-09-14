package stuart.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import stuart.exception.StuartException;
import stuart.task.Task;
import stuart.ui.Ui;

/**
 * Makes sense of the user's raw command text: parsing task numbers, dates,
 * and command-specific fields (like a deadline's {@code /by} date) out of
 * it. Reports malformed input as a {@link StuartException}; does not decide
 * what to do about it.
 */
public class Parser {
    private Parser() {
    }

    /**
     * The kind of command a line of user input represents.
     */
    public enum CommandType {
        /** Exit the chatbot. */
        BYE,
        /** List all tasks. */
        LIST,
        /** List all tasks, sorted by date with dateless tasks last. */
        SORTED,
        /** List the tasks occurring on a specific date. */
        ON,
        /** Mark a task as done. */
        MARK,
        /** Find tasks with a keyword. */
        FIND,
        /** List tasks due soon. */
        REMIND,
        /** Mark a task as not done. */
        UNMARK,
        /** Delete a task. */
        DELETE,
        /** Add a to-do task. */
        TODO,
        /** Add a deadline task. */
        DEADLINE,
        /** Add an event task. */
        EVENT,
        /** Input that doesn't match any recognized command keyword. */
        UNKNOWN
    }

    /**
     * A command's type, together with whatever text followed its keyword
     * (empty for a keyword-only command like {@code list}). {@code Stuart}
     * dispatches on {@link #type} and hands {@link #arguments} to whichever
     * {@code Parser} method that command needs next.
     *
     * @param type the recognized command type
     * @param arguments the text following the command keyword, not yet interpreted
     */
    public record ParsedCommand(CommandType type, String arguments) {
    }

    /**
     * The description and due date parsed from the text after a
     * {@code deadline} command.
     *
     * @param description what the deadline is
     * @param by the due date text, as typed by the user
     */
    public record DeadlineFields(String description, String by) {
    }

    /**
     * The description, start, and end times parsed from the text after an
     * {@code event} command.
     *
     * @param description what the event is
     * @param from the start date text, as typed by the user
     * @param to the end date text, as typed by the user
     */
    public record EventFields(String description, String from, String to) {
    }

    /**
     * Identifies which command a trimmed line of user input is, and
     * separates out whatever text followed its keyword. Recognizes only the
     * keyword itself here; it's up to whichever {@code Parser} method the
     * caller uses next (e.g. {@link #parseIndex}, {@link #parseDeadlineFields})
     * to make sense of the remaining text, and up to the caller to validate
     * its content (e.g. that it isn't empty).
     *
     * @param trimmedCommand the user's input, already trimmed
     * @return the recognized command type and its argument text
     */
    public static ParsedCommand parseCommand(String trimmedCommand) {
        assert trimmedCommand.equals(trimmedCommand.trim())
                : "caller should have already trimmed trimmedCommand, per this method's own contract";
        if (trimmedCommand.equals("bye")) {
            return new ParsedCommand(CommandType.BYE, "");
        } else if (trimmedCommand.equals("list")) {
            return new ParsedCommand(CommandType.LIST, "");
        } else if (trimmedCommand.equals("sorted")) {
            return new ParsedCommand(CommandType.SORTED, "");
        } else if (trimmedCommand.equals("on") || trimmedCommand.startsWith("on ")) {
            return new ParsedCommand(CommandType.ON, trimmedCommand.substring("on".length()).trim());
        } else if (trimmedCommand.startsWith("mark ")) {
            return new ParsedCommand(CommandType.MARK, trimmedCommand.substring("mark ".length()));
        } else if (trimmedCommand.startsWith("unmark ")) {
            return new ParsedCommand(CommandType.UNMARK, trimmedCommand.substring("unmark ".length()));
        } else if (trimmedCommand.startsWith("delete ")) {
            return new ParsedCommand(CommandType.DELETE, trimmedCommand.substring("delete ".length()));
        } else if (trimmedCommand.equals("todo") || trimmedCommand.startsWith("todo ")) {
            return new ParsedCommand(CommandType.TODO, trimmedCommand.substring("todo".length()).trim());
        } else if (trimmedCommand.equals("deadline") || trimmedCommand.startsWith("deadline ")) {
            return new ParsedCommand(CommandType.DEADLINE, trimmedCommand.substring("deadline".length()).trim());
        } else if (trimmedCommand.equals("event") || trimmedCommand.startsWith("event ")) {
            return new ParsedCommand(CommandType.EVENT, trimmedCommand.substring("event".length()).trim());
        } else if (trimmedCommand.equals("find") || trimmedCommand.startsWith("find ")) {
            return new ParsedCommand(CommandType.FIND, trimmedCommand.substring("find".length()).trim());
        } else if (trimmedCommand.equals("remind") || trimmedCommand.startsWith("remind ")) {
            return new ParsedCommand(CommandType.REMIND, trimmedCommand.substring("remind".length()).trim());
        } else {
            return new ParsedCommand(CommandType.UNKNOWN, trimmedCommand);
        }
    }

    /**
     * Parses the task number following a {@code mark}/{@code unmark}/
     * {@code delete} command into a 0-based index.
     *
     * @param indexText the text after the command word, expected to be a 1-based number
     * @return the 0-based index, or -1 if {@code indexText} is not a valid number
     */
    public static int parseIndex(String indexText) {
        try {
            return Integer.parseInt(indexText.trim()) - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Parses a non-negative day count, e.g. for the {@code remind} command's
     * optional look-ahead window.
     *
     * @param text the day-count text to parse
     * @return the parsed day count
     * @throws StuartException if {@code text} is not a non-negative integer
     */
    public static int parseDayCount(String text) throws StuartException {
        int days;
        try {
            days = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new StuartException("\"" + text + "\" is not a valid number of days.");
        }
        if (days < 0) {
            throw new StuartException("The number of days must not be negative.");
        }
        return days;
    }

    /**
     * Parses a date in {@code yyyy-MM-dd} format, e.g. {@code "2019-10-15"}.
     *
     * @param text the date text to parse
     * @return the parsed date
     * @throws StuartException if {@code text} is not a valid {@code yyyy-MM-dd} date
     */
    public static LocalDate parseDate(String text) throws StuartException {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new StuartException(
                    "\"" + text + "\" is not a valid date. Please use yyyy-MM-dd, e.g. 2019-10-15.");
        }
    }

    /**
     * Rejects task text that contains the save-file field delimiter, since
     * it would be misread as extra fields the next time the save file is
     * loaded (e.g. a description of "milk | eggs" would split into two
     * fields instead of one).
     *
     * @param text the description or date/time text to check
     * @throws StuartException if {@code text} contains {@code " | "}
     */
    public static void checkNoSaveDelimiter(String text) throws StuartException {
        if (text.contains(Task.SAVE_DELIMITER)) {
            throw new StuartException("Task details cannot contain \"" + Task.SAVE_DELIMITER + "\".");
        }
    }

    /**
     * Splits the text after {@code deadline}/{@code deadline } into its
     * description and {@code /by} fields. Does not validate that either
     * field is non-empty; the caller decides what to do about that.
     *
     * @param rest the text after the {@code deadline} keyword
     * @return the parsed description and {@code by} fields
     * @throws StuartException if {@code rest} has no {@code /by} marker at all
     */
    public static DeadlineFields parseDeadlineFields(String rest) throws StuartException {
        int byIndex = rest.indexOf("/by");
        if (byIndex == -1) {
            throw new StuartException("A deadline needs a description and \"/by <yyyy-MM-dd>\", \n"
                    + Ui.TEXT_INDENT + "e.g. deadline return book /by 2019-10-15");
        }
        if (rest.indexOf("/by", byIndex + 1) != -1) {
            throw new StuartException("A deadline can only have one \"/by\" date.");
        }
        String description = rest.substring(0, byIndex).trim();
        String by = rest.substring(byIndex + "/by".length()).trim();
        return new DeadlineFields(description, by);
    }

    /**
     * Splits the text after {@code event}/{@code event } into its
     * description, {@code /from}, and {@code /to} fields. Does not validate
     * that any field is non-empty; the caller decides what to do about that.
     *
     * @param rest the text after the {@code event} keyword
     * @return the parsed description, {@code from}, and {@code to} fields
     * @throws StuartException if {@code rest} is missing {@code /from} or
     *         {@code /to}, or has them in the wrong order
     */
    public static EventFields parseEventFields(String rest) throws StuartException {
        int fromIndex = rest.indexOf("/from");
        int toIndex = rest.indexOf("/to");
        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new StuartException(
                    "An event needs a description, \"/from <yyyy-MM-dd>\", and \"/to <yyyy-MM-dd>\", \n"
                    + Ui.TEXT_INDENT + "e.g. event meeting /from 2019-10-15 /to 2019-10-16");
        }
        if (rest.indexOf("/from", fromIndex + 1) != -1) {
            throw new StuartException("An event can only have one \"/from\" date.");
        }
        if (rest.indexOf("/to", toIndex + 1) != -1) {
            throw new StuartException("An event can only have one \"/to\" date.");
        }
        String description = rest.substring(0, fromIndex).trim();
        String from = rest.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = rest.substring(toIndex + "/to".length()).trim();
        return new EventFields(description, from, to);
    }
}
