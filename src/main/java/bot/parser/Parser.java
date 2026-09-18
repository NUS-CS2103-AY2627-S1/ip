package bot.parser;

import java.time.LocalDate;

import bot.exception.BotException;
import bot.task.Deadline;
import bot.task.Event;
import bot.task.Todo;

/**
 * Makes sense of user commands: splits a raw input line into a command
 * word and its argument text, and turns that argument text into the
 * value each command needs (a new Task, a 0-based task index, or a
 * date). Knows the command grammar (e.g. the "/by", "/from", "/to"
 * markers) but nothing about how tasks are stored or how results are
 * displayed - that stays with {@link bot.task.TaskList} and {@link bot.ui.Ui}.
 */
public class Parser {

    /** The command word and its raw, untrimmed argument text from one line of user input. */
    public static final class ParsedCommand {
        private final String commandWord;
        private final String arguments;

        private ParsedCommand(String commandWord, String arguments) {
            this.commandWord = commandWord;
            this.arguments = arguments;
        }

        /** The command word, e.g. {@code "todo"} in {@code "todo read book"}. */
        public String commandWord() {
            return commandWord;
        }

        /** The raw, untrimmed text after the command word, e.g. {@code " read book"}. */
        public String arguments() {
            return arguments;
        }
    }

    /** Splits one line of user input into its command word and the rest of the line. */
    public static ParsedCommand parseCommand(String input) {
        String normalized = input.trim().replaceAll("\\s+", " ");
        String[] parts = normalized.split(" ", 2);
        String commandWord = parts[0];
        String arguments = (parts.length > 1) ? parts[1] : "";
        return new ParsedCommand(commandWord, arguments);
    }

    /**
     * Parses a 1-based task number as typed by the user (e.g. the "2" in
     * "mark 2") and converts it to the corresponding 0-based index into
     * the task list.
     */
    public static int parseTaskIndex(String arg, String commandWord, int taskCount) throws BotException {
        String trimmed = arg.trim();
        if (trimmed.isEmpty()) {
            throw new BotException(
                    "OOPS!!! Tell me which task number to " + commandWord + ", e.g. \"" + commandWord + " 2\".");
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            throw new BotException("OOPS!!! \"" + trimmed + "\" isn't a task number - try something like \""
                    + commandWord + " 2\".");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new BotException("OOPS!!! There's no task number " + taskNumber + " in your list.");
        }
        return taskNumber - 1;
    }

    /**
     * Parses the argument text of a {@code todo} command into a new Todo.
     *
     * @param rest The text after the {@code todo} command word.
     * @return A new, not-done Todo with that description.
     * @throws BotException if the description is empty.
     */
    public static Todo parseTodo(String rest) throws BotException {
        String description = rest.trim();
        if (description.isEmpty()) {
            throw new BotException("OOPS!!! A todo needs a description, e.g. \"todo borrow book\".");
        }
        return new Todo(description);
    }

    /**
     * Parses the argument text of a {@code deadline} command into a new Deadline.
     *
     * @param rest The text after the {@code deadline} command word.
     * @return A new, not-done Deadline with that description and due date/time.
     * @throws BotException if the description, the {@code /by} marker, or the date/time is missing or invalid.
     */
    public static Deadline parseDeadline(String rest) throws BotException {
        int byIndex = rest.indexOf("/by ");
        if (byIndex == -1) {
            throw new BotException("OOPS!!! A deadline needs a \"/by\" date or time, e.g. "
                    + "\"deadline return book /by 2019-10-15\" or \"deadline return book /by 2019-10-15 1800\".");
        }
        String description = rest.substring(0, byIndex).trim();
        String by = rest.substring(byIndex + 4).trim();
        if (description.isEmpty()) {
            throw new BotException("OOPS!!! A deadline needs a description, e.g. "
                    + "\"deadline return book /by 2019-10-15\".");
        }
        if (by.isEmpty()) {
            throw new BotException("OOPS!!! Tell me the date or time after \"/by\", e.g. "
                    + "\"deadline return book /by 2019-10-15\".");
        }
        return new Deadline(description, TaskDateTime.parse(by));
    }

    /**
     * Parses the argument text of an {@code event} command into a new Event.
     *
     * @param rest The text after the {@code event} command word.
     * @return A new, not-done Event with that description and start/end date/time.
     * @throws BotException if the description, either marker, or either date/time is missing or invalid.
     */
    public static Event parseEvent(String rest) throws BotException {
        if (rest.indexOf("/from ") != rest.lastIndexOf("/from ")
                || rest.indexOf("/to ") != rest.lastIndexOf("/to ")) {
            throw new BotException("OOPS!!! An event can have only one \"/from\" and one \"/to\" marker.");
        }
        int fromIndex = rest.indexOf("/from ");
        int toIndex = rest.indexOf("/to ");
        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new BotException("OOPS!!! An event needs both \"/from\" and \"/to\", e.g. "
                    + "\"event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600\".");
        }
        String description = rest.substring(0, fromIndex).trim();
        String from = rest.substring(fromIndex + 6, toIndex).trim();
        String to = rest.substring(toIndex + 4).trim();
        if (description.isEmpty()) {
            throw new BotException("OOPS!!! An event needs a description, e.g. "
                    + "\"event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600\".");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new BotException("OOPS!!! Tell me both a \"/from\" and \"/to\" date or time, e.g. "
                    + "\"event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600\".");
        }
        TaskDateTime fromDateTime = TaskDateTime.parse(from);
        TaskDateTime toDateTime = TaskDateTime.parse(to);
        if (!toDateTime.isAfter(fromDateTime)) {
            throw new BotException("OOPS!!! An event's end date must be after its start date.");
        }
        return new Event(description, fromDateTime, toDateTime);
    }

    /**
     * Parses the argument text of an {@code on} command into a date.
     *
     * @param rest The text after the {@code on} command word.
     * @return The parsed date.
     * @throws BotException if the date is missing or not in {@code yyyy-MM-dd} form.
     */
    public static LocalDate parseOnDate(String rest) throws BotException {
        String trimmed = rest.trim();
        if (trimmed.isEmpty()) {
            throw new BotException("OOPS!!! Tell me which date, e.g. \"on 2019-10-15\".");
        }
        return TaskDateTime.parseDateOnly(trimmed);
    }

    /**
     * Parses the argument text of a {@code find} command into a search keyword.
     *
     * @param rest The text after the {@code find} command word.
     * @return The trimmed keyword to search task descriptions for.
     * @throws BotException if the keyword is missing.
     */
    public static String parseFindKeyword(String rest) throws BotException {
        String trimmed = rest.trim();
        if (trimmed.isEmpty()) {
            throw new BotException("OOPS!!! Tell me what keyword to search for, e.g. \"find book\".");
        }
        return trimmed;
    }
}
