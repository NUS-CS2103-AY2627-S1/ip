package steph;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import steph.task.Deadline;
import steph.task.Event;
import steph.task.ToDo;

/**
 * Turns a raw line of user input into values the command loop can act on: the
 * {@link Command} it names, its argument text, and -- for the commands that
 * need it -- the task number or the fully-built task those arguments describe.
 *
 * <p>Every method here is pure text-to-value work. The parser never touches
 * the task list, the UI, or the save file; on malformed input it throws a
 * {@link StephException} whose message is ready to show the user.
 */
public class Parser {

    /** Shared opening line for every "couldn't understand your input" error below. */
    private static final String UNRECOGNIZED_INPUT_PREFIX = "Hmm.. I don't understand that.\n";

    /**
     * Identifies which command a line names, from its first word.
     *
     * @param fullCommand The whole line as typed.
     * @return The matching command.
     * @throws StephException If the first word is not a known command keyword.
     */
    public static Command parseCommand(String fullCommand) throws StephException {
        String keyword = fullCommand.split(" ", 2)[0];
        Command command = Command.fromKeyword(keyword);
        if (command == null) {
            throw new StephException("Hmm.. I don't understand that command: \"" + keyword + "\".");
        }
        return command;
    }

    /**
     * Returns the part of the line after the command keyword, trimmed. Empty
     * when the line is only the keyword.
     *
     * @param fullCommand The whole line as typed.
     * @return The argument text.
     */
    public static String parseArguments(String fullCommand) {
        String[] parts = fullCommand.split(" ", 2);
        return parts.length > 1 ? parts[1].trim() : "";
    }

    /**
     * Parses the task number for a mark / unmark / delete command and converts
     * it to a 0-based index into a list of {@code taskCount} tasks.
     *
     * @param arguments   The text after the command keyword.
     * @param taskCount   How many tasks currently exist, for the range check.
     * @param commandWord The keyword to quote back in the error message.
     * @return The 0-based index the number refers to.
     * @throws StephException If the text is missing, not a number, or out of range.
     */
    public static int parseTaskIndex(String arguments, int taskCount, String commandWord) throws StephException {
        try {
            int index = Integer.parseInt(arguments) - 1;
            if (index >= 0 && index < taskCount) {
                return index;
            }
        } catch (NumberFormatException e) {
            // Not a number, fall through to the shared "valid task number" error.
        }
        throw new StephException(UNRECOGNIZED_INPUT_PREFIX
                + "Please type \"" + commandWord + " <task-number>\" with a valid task number.");
    }

    /**
     * Returns the keyword a "find" command should search task names for.
     *
     * @param arguments the text after the "find" keyword
     * @return the keyword, with surrounding whitespace already removed by
     *         {@link #parseArguments}
     * @throws StephException if no keyword was given
     */
    public static String parseFind(String arguments) throws StephException {
        if (arguments.isEmpty()) {
            throw new StephException(UNRECOGNIZED_INPUT_PREFIX + "Please type \"find <keyword>\".");
        }
        return arguments;
    }

    /**
     * Builds a ToDo from its arguments.
     *
     * @param arguments The task name.
     * @return The new ToDo.
     * @throws StephException If the name is empty.
     */
    public static ToDo parseToDo(String arguments) throws StephException {
        if (arguments.isEmpty()) {
            throw new StephException(UNRECOGNIZED_INPUT_PREFIX + "Please type \"todo <task-name>\".");
        }
        return new ToDo(arguments);
    }

    /**
     * Builds a Deadline from {@code <task-name> /by <yyyy-mm-dd> [HHmm]}. The
     * "/by" marker is used as the split point since a task name isn't expected
     * to contain it.
     *
     * @param arguments The text after the "deadline" keyword.
     * @return The new Deadline.
     * @throws StephException If "/by" is missing, either side is empty, or the
     *                        date cannot be read.
     */
    public static Deadline parseDeadline(String arguments) throws StephException {
        String usageMessage = UNRECOGNIZED_INPUT_PREFIX + "Please type \"deadline <task-name> /by <yyyy-mm-dd>\".";
        int byIndex = arguments.indexOf("/by");
        if (byIndex == -1) {
            throw new StephException(usageMessage);
        }

        String name = arguments.substring(0, byIndex).trim();
        String by = arguments.substring(byIndex + "/by".length()).trim();
        if (name.isEmpty() || by.isEmpty()) {
            throw new StephException(usageMessage);
        }
        return new Deadline(name, parseDateTime(by));
    }

    /**
     * Builds an Event from
     * {@code <task-name> /from <yyyy-mm-dd> [HHmm] /to <yyyy-mm-dd> [HHmm] [/force]},
     * splitting first on "/from" and then on "/to" within the remainder. A
     * trailing "/force" asks the caller to skip the schedule-clash check;
     * it's stripped here, before the "/from"/"/to" split, so it never leaks
     * into the "to" date text.
     *
     * @param arguments The text after the "event" keyword.
     * @return The new Event, and whether "/force" was given.
     * @throws StephException If the markers are missing or out of order, any
     *                        part is empty, a date cannot be read, or the
     *                        start does not come before the end.
     */
    public static ParsedEvent parseEvent(String arguments) throws StephException {
        String usageMessage = UNRECOGNIZED_INPUT_PREFIX
                + "Please type \"event <task-name> /from <yyyy-mm-dd> /to <yyyy-mm-dd>\".";
        boolean isForce = arguments.endsWith("/force");
        String cleanedArguments = isForce
                ? arguments.substring(0, arguments.length() - "/force".length()).trim()
                : arguments;

        int fromIndex = cleanedArguments.indexOf("/from");
        int toIndex = cleanedArguments.indexOf("/to");

        boolean validOrder = fromIndex != -1 && toIndex != -1 && fromIndex < toIndex;

        if (!validOrder) {
            throw new StephException(usageMessage);
        }

        String name = cleanedArguments.substring(0, fromIndex).trim();
        String from = cleanedArguments.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = cleanedArguments.substring(toIndex + "/to".length()).trim();

        if (name.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new StephException(usageMessage);
        }

        LocalDateTime fromDateTime = parseDateTime(from);
        LocalDateTime toDateTime = parseDateTime(to);
        // A date-only end (midnight) means "through the end of that day," the
        // same reading Event#clashesWith uses -- so a same-day date-only
        // "/from 2019-10-15 /to 2019-10-15" is a valid whole-day event, not a
        // backwards one, even though the raw values are equal.
        LocalDateTime effectiveEnd = DateTimes.expandIfMidnight(toDateTime);
        if (!fromDateTime.isBefore(effectiveEnd)) {
            throw new StephException(UNRECOGNIZED_INPUT_PREFIX
                    + "An event's start (" + DateTimes.toDisplayFormat(fromDateTime)
                    + ") must be before its end (" + DateTimes.toDisplayFormat(toDateTime) + ").");
        }
        return new ParsedEvent(new Event(name, fromDateTime, toDateTime), isForce);
    }

    /**
     * Parses a user-supplied date, optionally with a 24-hour time ("yyyy-mm-dd"
     * or "yyyy-mm-dd HHmm", e.g. 2019-10-15 1800). A date with no time is taken
     * as midnight.
     *
     * @param text The date (and optional time) as typed.
     * @return The parsed date-time.
     * @throws StephException With a readable hint if the text is not a valid date.
     */
    private static LocalDateTime parseDateTime(String text) throws StephException {
        try {
            return DateTimes.parseUserInput(text);
        } catch (DateTimeParseException e) {
            throw new StephException("Hmm.. I couldn't read \"" + text + "\" as a date.\n"
                    + "Please use the format yyyy-mm-dd or yyyy-mm-dd HHmm, e.g. 2019-10-15 1800.");
        }
    }
}
