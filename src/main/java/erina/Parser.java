package erina;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Optional;

import erina.task.Deadline;
import erina.task.Event;
import erina.task.Todo;

/**
 * Turns the text the user types into the objects the program works with.
 *
 * <p>All knowledge of command syntax, such as {@code /by} and {@code /from}
 * markers, date formats and task numbering, lives here. The rest of the
 * program never inspects raw input, so a change to the syntax touches only
 * this class.
 */
public class Parser {
    /** Shown whenever an event command is missing one of its three parts. */
    private static final String EVENT_FORMAT_MESSAGE = "Pardon me. An event needs a description, "
            + "a /from time and a /to time, like: event project meeting /from Mon 2pm /to 4pm";

    /**
     * A character the user may not type into a task.
     *
     * <p>It separates the fields of the save file, so a task containing it
     * would be split in the wrong places when loaded back, silently losing
     * part of the task.
     */
    private static final String RESERVED_CHARACTER = "|";

    /** This class is not meant to be instantiated; its methods are static. */
    private Parser() {
    }

    /**
     * Splits one line of input into the command word and its argument.
     *
     * <p>Commands taking an argument ({@code mark 2}) have to be told apart
     * from commands that do not ({@code list}), so the line is split at the
     * first run of whitespace only: everything after it belongs to the
     * argument. Extra spaces and capital letters in the command word are
     * forgiven, since they do not change what the user meant.
     *
     * @param input one line as typed by the user, not blank
     * @return the command and the text that followed it
     * @throws ErinaException if the first word is not a command
     */
    public static ParsedCommand parse(String input) throws ErinaException {
        // \s+ matches any run of spaces or tabs, so "mark   2" reads as "mark 2".
        String[] parts = input.trim().split("\\s+", 2);
        String argument = parts.length > 1 ? parts[1].trim() : "";
        return new ParsedCommand(Command.fromKeyword(parts[0].toLowerCase()), argument);
    }

    /**
     * Rejects anything typed after a command that takes no argument.
     *
     * <p>Silently ignoring {@code list all} could mislead the user into
     * thinking {@code all} did something, so it is pointed out instead.
     *
     * @param command  the command the user asked for
     * @param argument the text after the command word
     * @throws ErinaException if the argument is not empty
     */
    public static void checkNoArgument(Command command, String argument) throws ErinaException {
        if (!argument.isEmpty()) {
            throw new ErinaException("Pardon me. " + command.getKeyword()
                    + " does not take anything after it. Just type: " + command.getKeyword());
        }
    }

    /**
     * Builds a to-do from the text after the {@code todo} command.
     *
     * @param argument the text after the command word
     * @return the to-do described by that text
     * @throws ErinaException if no description was given, or it contains a
     *                        character that cannot be saved
     */
    public static Todo parseTodo(String argument) throws ErinaException {
        if (argument.isEmpty()) {
            throw new ErinaException(
                    "Pardon me. The description of a todo cannot be empty.");
        }
        checkNoReservedCharacter(argument);
        return new Todo(argument);
    }

    /**
     * Builds a deadline from the text after the {@code deadline}
     * command, which has the form {@code <description> /by <yyyy-mm-dd>}.
     *
     * @param argument the text after the command word
     * @return the deadline described by that text
     * @throws ErinaException if the description or the /by part is missing or
     *                        repeated, or the /by part is not a real date
     */
    public static Deadline parseDeadline(String argument) throws ErinaException {
        if (argument.isEmpty()) {
            throw new ErinaException(
                    "Pardon me. The description of a deadline cannot be empty.");
        }
        checkNoReservedCharacter(argument);
        checkAtMostOnce(argument, "/by");

        // Limit of 2 so that a description containing "/by" is left intact.
        String[] parts = argument.split(" /by ", 2);
        if (parts.length < 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new ErinaException("Pardon me. A deadline needs a description and a "
                    + "/by date, like: deadline return book /by 2019-10-15");
        }
        return new Deadline(parts[0].trim(), parseDate(parts[1].trim()));
    }

    /**
     * Builds an event from the text after the {@code event} command,
     * which has the form {@code <description> /from <start> /to <end>}.
     *
     * @param argument the text after the command word
     * @return the event described by that text
     * @throws ErinaException if the description, the /from part or the /to
     *                        part is missing or repeated, or the event ends
     *                        before it starts
     */
    public static Event parseEvent(String argument) throws ErinaException {
        if (argument.isEmpty()) {
            throw new ErinaException(
                    "Pardon me. The description of an event cannot be empty.");
        }
        checkNoReservedCharacter(argument);
        checkAtMostOnce(argument, "/from");
        checkAtMostOnce(argument, "/to");

        String[] fromParts = argument.split(" /from ", 2);
        if (fromParts.length < 2 || fromParts[0].isBlank()) {
            throw new ErinaException(EVENT_FORMAT_MESSAGE);
        }

        String[] toParts = fromParts[1].split(" /to ", 2);
        if (toParts.length < 2 || toParts[0].isBlank() || toParts[1].isBlank()) {
            throw new ErinaException(EVENT_FORMAT_MESSAGE);
        }

        String from = toParts[0].trim();
        String to = toParts[1].trim();
        checkEndNotBeforeStart(from, to);
        return new Event(fromParts[0].trim(), from, to);
    }

    /**
     * Turns the task number the user typed into a position in the list.
     *
     * <p>Every command taking a task number validates it here, so the checks
     * and their wording stay in one place.
     *
     * @param argument  the task number as typed by the user, counting from 1
     * @param taskCount how many tasks there are to refer to
     * @return the matching 0-based index
     * @throws ErinaException if the number is missing, not a number, or does
     *                        not refer to an existing task
     */
    public static int parseIndex(String argument, int taskCount) throws ErinaException {
        if (argument.isEmpty()) {
            throw new ErinaException("Pardon me. Please tell me which task number, "
                    + "like: mark 2");
        }

        int number;
        try {
            number = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            // Rethrown as an ErinaException so the main loop deals with one
            // kind of failure, phrased for the user rather than the compiler.
            throw new ErinaException("Pardon me. \"" + argument
                    + "\" is not a task number.");
        }

        if (taskCount == 0) {
            throw new ErinaException("Pardon me. There are no tasks yet, so there is "
                    + "no task " + number + ".");
        }
        if (number < 1 || number > taskCount) {
            throw new ErinaException("Pardon me. There is no task " + number
                    + ". Your list only goes up to " + taskCount + ".");
        }

        // The user counts from 1 but the list is indexed from 0.
        int index = number - 1;
        assert index >= 0 && index < taskCount : "validated index out of range: " + index;
        return index;
    }

    /**
     * Turns a date the user typed into a real date.
     *
     * <p>Dates that do not exist, such as {@code 2023-02-30}, are rejected
     * too, because {@link LocalDate#parse} checks the day against the month.
     *
     * @param text the date as typed, expected as {@code yyyy-mm-dd}
     * @return the date it names
     * @throws ErinaException if the text is not a real date in that form
     */
    public static LocalDate parseDate(String text) throws ErinaException {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new ErinaException("Pardon me. \"" + text + "\" is not a date I "
                    + "understand. Please use yyyy-mm-dd, like 2019-10-15.");
        }
    }

    /**
     * Rejects text containing the character the save file uses as a separator.
     *
     * @param text the text the user wants to save
     * @throws ErinaException if the text contains {@link #RESERVED_CHARACTER}
     */
    private static void checkNoReservedCharacter(String text) throws ErinaException {
        if (text.contains(RESERVED_CHARACTER)) {
            throw new ErinaException("Pardon me. Tasks cannot contain the character "
                    + RESERVED_CHARACTER + ", because I use it to organise the save file.");
        }
    }

    /**
     * Rejects a marker such as {@code /by} that was typed more than once.
     *
     * <p>Without this check, {@code /by 2019-10-15 /by 2019-10-16} would be
     * reported as a confusing "not a date" error for the whole remainder.
     * Only whole words count, so a word like {@code /bypass} is not mistaken
     * for the marker.
     *
     * @param argument the text after the command word
     * @param marker   the marker to count, for example {@code "/by"}
     * @throws ErinaException if the marker appears more than once
     */
    private static void checkAtMostOnce(String argument, String marker) throws ErinaException {
        long count = Arrays.stream(argument.split("\\s+"))
                .filter(marker::equals)
                .count();
        if (count > 1) {
            throw new ErinaException("Pardon me. Please give " + marker + " only once.");
        }
    }

    /**
     * Rejects an event whose end date comes before its start date.
     *
     * <p>Event times are free text ({@code Mon 2pm}), which cannot be
     * compared, so the check applies only when both are dates. An event that
     * starts and ends on the same date is allowed, since it is a normal
     * one-day event.
     *
     * @param from when the event starts, as typed
     * @param to   when the event ends, as typed
     * @throws ErinaException if both are dates and the end is before the start
     */
    private static void checkEndNotBeforeStart(String from, String to) throws ErinaException {
        Optional<LocalDate> start = tryParseDate(from);
        Optional<LocalDate> end = tryParseDate(to);
        if (start.isPresent() && end.isPresent() && end.get().isBefore(start.get())) {
            throw new ErinaException("Pardon me. An event cannot end (" + to
                    + ") before it starts (" + from + ").");
        }
    }

    /**
     * Reads the text as a date if it is one.
     *
     * @param text the text to read
     * @return the date, or an empty Optional if the text is not a date
     */
    private static Optional<LocalDate> tryParseDate(String text) {
        try {
            return Optional.of(LocalDate.parse(text));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }
}
