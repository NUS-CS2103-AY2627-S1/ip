package altair.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Locale;

import altair.AltairException;
import altair.command.CommandType;
import altair.task.Deadline;
import altair.task.Event;
import altair.task.Task;
import altair.task.Todo;

/**
 * Turns the raw text of a user command into the value it describes.
 *
 * <p>Every method here is a pure function of its arguments and throws
 * {@link AltairException} with a message already worded for display when the
 * command text is malformed. {@link altair.Altair} calls these to build a
 * {@link Task} or extract a task number before acting on it, which keeps the
 * text-parsing rules out of the class that routes commands and mutates the
 * task list.</p>
 */
public class Parser {

    /** The date format accepted in commands. */
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    /** Not meant to be instantiated: every method is static. */
    private Parser() {
    }

    /**
     * Creates the task represented by a user command by dispatching to the
     * helper for its command type.
     *
     * @param command the complete command entered by the user.
     * @return the new task.
     * @throws AltairException if the command is incomplete or unknown.
     */
    public static Task parseTask(String command) throws AltairException {
        String trimmed = command == null ? "" : command.trim();

        switch (CommandType.from(trimmed)) {
        case TODO:
            return createTodo(trimmed);
        case DEADLINE:
            return createDeadline(trimmed);
        case EVENT:
            return createEvent(trimmed);
        default:
            throw new AltairException("I do not understand your command. Try again, perhaps?");
        }
    }

    /**
     * Creates the {@link Todo} described by a {@code todo <description>} command.
     *
     * @param command the trimmed command entered by the user.
     * @return the new todo.
     * @throws AltairException if the description is missing or contains '|'.
     */
    private static Todo createTodo(String command) throws AltairException {
        String description = textAfterCommand(command, "todo");
        if (description.isEmpty()) {
            throw new AltairException("The description of a todo cannot be empty.");
        }
        validateStorableText(description);
        return new Todo(description);
    }

    /**
     * Creates the {@link Deadline} described by a
     * {@code deadline <description> /by <date>} command.
     *
     * <p>Markers let the description and date contain spaces.</p>
     *
     * @param command the trimmed command entered by the user.
     * @return the new deadline.
     * @throws AltairException if the description or date is missing or malformed, or a field contains '|'.
     */
    private static Deadline createDeadline(String command) throws AltairException {
        String remainder = textAfterCommand(command, "deadline");
        String[] words = splitWords(remainder);
        int byIndex = findMarker(words, "/by", 0);
        if (words.length == 0 || byIndex == 0) {
            throw new AltairException("The description of a deadline cannot be empty.");
        }
        if (byIndex < 0) {
            throw new AltairException("A deadline needs a date after /by.");
        }

        String description = joinWords(words, 0, byIndex);
        String byText = joinWords(words, byIndex + 1, words.length);
        if (description.isEmpty()) {
            throw new AltairException("The description of a deadline cannot be empty.");
        }
        if (byText.isEmpty()) {
            throw new AltairException("A deadline needs a date after /by.");
        }
        validateStorableText(description);
        validateStorableText(byText);
        return new Deadline(description, parseDate(byText, "A deadline date"));
    }

    /**
     * Creates the {@link Event} described by an
     * {@code event <description> /from <date> /to <date>} command.
     *
     * <p>Markers let the description and dates contain spaces.</p>
     *
     * @param command the trimmed command entered by the user.
     * @return the new event.
     * @throws AltairException if the description or a date is missing or malformed, a field contains '|',
     *     or the start date is after the end date.
     */
    private static Event createEvent(String command) throws AltairException {
        String remainder = textAfterCommand(command, "event");
        String[] words = splitWords(remainder);
        int fromIndex = findMarker(words, "/from", 0);
        int toIndex = findMarker(words, "/to", fromIndex < 0 ? 0 : fromIndex + 1);
        if (words.length == 0 || fromIndex == 0) {
            throw new AltairException("The description of an event cannot be empty.");
        }
        if (fromIndex < 0 || toIndex < 0 || toIndex <= fromIndex + 1) {
            throw new AltairException("An event needs /from and /to dates.");
        }

        String description = joinWords(words, 0, fromIndex);
        String fromText = joinWords(words, fromIndex + 1, toIndex);
        String toText = joinWords(words, toIndex + 1, words.length);
        if (description.isEmpty()) {
            throw new AltairException("The description of an event cannot be empty.");
        }
        if (fromText.isEmpty() || toText.isEmpty()) {
            throw new AltairException("An event needs /from and /to dates.");
        }
        validateStorableText(description);
        validateStorableText(fromText);
        validateStorableText(toText);

        LocalDate fromDate = parseDate(fromText, "An event start date");
        LocalDate toDate = parseDate(toText, "An event end date");
        if (fromDate.isAfter(toDate)) {
            throw new AltairException("An event's start date cannot be after its end date.");
        }
        return new Event(description, fromDate, toDate);
    }

    /**
     * Returns the part of a command after its command word.
     *
     * @param command the trimmed command.
     * @param commandWord the command word to remove.
     * @return the remaining text.
     */
    public static String textAfterCommand(String command, String commandWord) {
        return command.substring(commandWord.length()).trim();
    }

    /** Splits a command remainder into non-empty whitespace-delimited words. */
    private static String[] splitWords(String text) {
        String trimmed = text == null ? "" : text.trim();
        return trimmed.isEmpty() ? new String[0] : trimmed.split("\\s+");
    }

    /** Finds a case-insensitive marker token from a given word index onward. */
    private static int findMarker(String[] words, String marker, int startIndex) {
        for (int i = Math.max(0, startIndex); i < words.length; i++) {
            if (words[i].toLowerCase(Locale.ROOT).equals(marker)) {
                return i;
            }
        }
        return -1;
    }

    /** Joins a range of command words with single spaces. */
    private static String joinWords(String[] words, int startIndex, int endIndex) {
        if (startIndex >= endIndex) {
            return "";
        }
        return String.join(" ", Arrays.copyOfRange(words, startIndex, endIndex));
    }

    /** Parses a user-supplied ISO date and reports a helpful command error. */
    private static LocalDate parseDate(String text, String dateDescription) throws AltairException {
        try {
            return LocalDate.parse(text, INPUT_DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new AltairException(dateDescription + " must use yyyy-MM-dd format.");
        }
    }

    /** Rejects the file delimiter in user data so saved rows stay parseable. */
    private static void validateStorableText(String text) throws AltairException {
        if (text.contains("|")) {
            throw new AltairException("Task details cannot contain the '|' character.");
        }
    }

    /**
     * Parses the single positive integer used by the mark, unmark, and delete commands.
     *
     * @param command the command entered by the user.
     * @param operation the command word, used in the usage hint on error.
     * @return the parsed task number.
     * @throws AltairException if the command does not contain exactly one number argument.
     */
    public static int parseTaskNumber(String command, String operation) throws AltairException {
        String[] parts = splitWords(command);
        if (parts.length != 2) {
            throw new AltairException("Please use: " + operation + " <task number>.");
        }
        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException exception) {
            throw new AltairException("Please use a valid task number.");
        }
    }

    /**
     * Rejects arguments on commands that only have a bare command form.
     *
     * @param command the command entered by the user.
     * @param operation the command word, used in the usage hint on error.
     * @throws AltairException if the command carries any argument.
     */
    public static void ensureNoArguments(String command, String operation) throws AltairException {
        if (splitWords(command).length != 1) {
            throw new AltairException("Please use: " + operation + ".");
        }
    }
}
