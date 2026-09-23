package sallman;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sallman.command.AddCommand;
import sallman.command.Command;
import sallman.command.CommandType;
import sallman.command.DeleteCommand;
import sallman.command.ExitCommand;
import sallman.command.FindCommand;
import sallman.command.ListCommand;
import sallman.command.MarkCommand;
import sallman.command.OnCommand;
import sallman.command.SortCommand;
import sallman.command.SortOrder;
import sallman.command.TagCommand;
import sallman.command.UndoCommand;
import sallman.task.Deadline;
import sallman.task.Event;
import sallman.task.Task;
import sallman.task.TaskDate;
import sallman.task.Todo;


/**
 * Makes sense of what the user types.
 * <p>
 * Splitting a line into a command and its arguments, reading task numbers and
 * dates, and building tasks all happen here, so the rest of the app deals in
 * commands and tasks rather than in raw text.
 */
public class Parser {

    /**
     * Worked examples shown alongside an error, so the user can see the shape
     * of a correct command instead of only being told what was wrong.
     */
    private static final String TODO_EXAMPLE = "Try: todo read book";
    private static final String DEADLINE_EXAMPLE =
            "Try: deadline return book /by " + TaskDate.EXAMPLE;
    private static final String EVENT_EXAMPLE =
            "Try: event project meeting /from " + TaskDate.EXAMPLE + " /to 2019-10-16";

    /**
     * Commands that take nothing after their keyword. Anything typed after one
     * is refused rather than ignored, since ignoring it can mislead: "undo twice"
     * would undo only once, and "bye later" would exit straight away.
     */
    private static final Set<CommandType> TAKES_NO_ARGUMENTS =
            EnumSet.of(CommandType.LIST, CommandType.UNDO, CommandType.BYE);

    /** Not meant to be instantiated: this class only holds static helpers. */
    private Parser() {
    }

    /**
     * Turns a line of input into the command it asks for.
     * <p>
     * Only the wording is checked here. Whether a task number is in range
     * depends on the list, which this class does not see, so commands that
     * take one check it when they run.
     *
     * @param input the whole line the user typed, already trimmed
     * @return the command the user asked for
     * @throws SallmanException if the command is unknown or its arguments
     *                          cannot be read
     */
    public static Command parse(String input) throws SallmanException {
        String[] parts = splitCommand(input);
        String keyword = parts[0];
        String arguments = parts[1];

        // Rejects an unknown keyword first, so every case below is a known one.
        CommandType type = CommandType.fromKeyword(keyword);
        if (TAKES_NO_ARGUMENTS.contains(type) && !arguments.isEmpty()) {
            throw new SallmanException("I'm sorry, but " + type.getKeyword() + " doesn't take anything after it.",
                    "Try: " + type.getKeyword());
        }
        return switch (type) {
            case BYE -> new ExitCommand();
            case LIST -> new ListCommand();
            case FIND -> new FindCommand(parseSearchKeyword(arguments));
            case ON -> new OnCommand(parseOnDate(arguments));
            case MARK -> new MarkCommand(true, arguments);
            case UNMARK -> new MarkCommand(false, arguments);
            case DELETE -> new DeleteCommand(arguments);
            case TODO -> new AddCommand(parseTodo(arguments));
            case DEADLINE -> new AddCommand(parseDeadline(arguments));
            case EVENT -> new AddCommand(parseEvent(arguments));
            case TAG -> new TagCommand(true, arguments);
            case UNTAG -> new TagCommand(false, arguments);
            case UNDO -> new UndoCommand();
            case SORT -> new SortCommand(arguments);
        };
    }

    /**
     * Splits a line into its command word and the arguments that follow.
     * <p>
     * Separating them up front means a command given without arguments still
     * reaches its own branch, where the missing part can be named, instead of
     * looking like an unknown command.
     *
     * @param input the whole line the user typed, already trimmed
     * @return the command word at index 0 and its arguments at index 1, the
     *         latter empty when none were given
     */
    private static String[] splitCommand(String input) {
        String[] words = input.split("\\s+", 2);
        // split() returns at least one element for any input, including an
        // empty one, so words[0] below never needs a length check of its own.
        assert words.length >= 1 : "split returned nothing for: " + input;
        String arguments = words.length > 1 ? words[1].trim() : "";
        return new String[] {words[0], arguments};
    }

    /**
     * Splits arguments at a marker such as {@code /by}.
     * <p>
     * The marker is named in the error, so a user who left it out or spelled
     * it differently is told which one was expected. A marker only counts when
     * it stands as a word of its own, so a description such as
     * {@code go /tomorrow} is not split at {@code /to}.
     *
     * @param arguments text the user typed after the command
     * @param marker    the marker to split at, e.g. {@code /by}
     * @param taskKind  the kind of task being built, for the error message
     * @param example   a worked example shown alongside the error
     * @return the text before the marker at index 0, and after it at index 1
     * @throws SallmanException if the marker is missing, is joined to the
     *                          word beside it, or is given more than once
     */
    private static String[] splitAtMarker(String arguments, String marker, String taskKind,
            String example) throws SallmanException {
        Pattern pattern = markerPattern(marker);
        Matcher matcher = pattern.matcher(arguments);
        if (!matcher.find()) {
            if (arguments.contains(marker)) {
                // Typed, but glued to a word, e.g. "book/by". Saying the marker
                // is missing would contradict what the user can see they typed.
                throw new SallmanException("I'm sorry, but I need a space before and after the " + marker
                        + " in that " + taskKind + ".", example);
            }
            throw new SallmanException("I'm sorry, but I couldn't find a " + marker + " in that "
                    + taskKind + ".", example);
        }
        String after = arguments.substring(matcher.end());
        if (pattern.matcher(after).find()) {
            throw new SallmanException("I'm sorry, but that " + taskKind + " has more than one "
                    + marker + ".",
                    example);
        }
        return new String[] {arguments.substring(0, matcher.start()), after};
    }

    /**
     * Returns a pattern matching a marker only where it stands as a word of
     * its own, with whitespace or the end of the text on either side.
     *
     * @param marker the marker to look for, e.g. {@code /by}
     * @return the pattern
     */
    private static Pattern markerPattern(String marker) {
        return Pattern.compile("(?<!\\S)" + Pattern.quote(marker) + "(?!\\S)");
    }

    /**
     * Collapses runs of whitespace inside user-typed text to single spaces.
     * <p>
     * Stray double spaces are almost always typos, and keeping them would let
     * {@code read  book} and {@code read book} be stored as different tasks.
     *
     * @param text the text as typed
     * @return the text trimmed, with each run of whitespace made one space
     */
    private static String collapseSpaces(String text) {
        return text.trim().replaceAll("\\s+", " ");
    }

    /**
     * Rejects a part of a command that the user left out.
     *
     * @param value   the part as typed, already trimmed
     * @param message what to tell the user when it is missing
     * @param example a worked example shown alongside the error
     * @throws SallmanException if nothing was given for that part
     */
    private static void requirePresent(String value, String message, String example)
            throws SallmanException {
        if (value.isEmpty()) {
            throw new SallmanException(message, example);
        }
    }

    /**
     * Splits the arguments of a tag command into the task number and the tags.
     *
     * @param command   the command the arguments were given to, for messages
     * @param arguments text the user typed after the command
     * @return the task number at index 0 and the tags at index 1
     * @throws SallmanException if nothing was given after the command
     */
    public static String[] splitTaskNumberAndTags(String command, String arguments)
            throws SallmanException {
        if (arguments.isEmpty()) {
            throw new SallmanException("I'd love to help, but " + command + " needs a task number and a tag.",
                    "Try: " + command + " 2 fun");
        }
        String[] words = arguments.split("\\s+", 2);
        String tags = words.length > 1 ? words[1].trim() : "";
        return new String[] {words[0], tags};
    }

    /**
     * Reads the tags given to a tag command.
     * <p>
     * A leading {@code #} is optional, so both {@code tag 2 fun} and
     * {@code tag 2 #fun} work; the tag is stored without it either way.
     *
     * @param command the command the tags were given to, for messages
     * @param text    the part of the arguments after the task number
     * @return the tags, in the order they were typed
     * @throws SallmanException if no tag was given, or one is not a valid tag
     */
    public static List<String> parseTags(String command, String text)
            throws SallmanException {
        List<String> tags = Arrays.stream(text.split("\\s+"))
                .map(tag -> tag.startsWith("#") ? tag.substring(1) : tag)
                .filter(tag -> !tag.isEmpty())
                .toList();
        if (tags.isEmpty()) {
            throw new SallmanException("I'd love to help, but " + command + " needs at least one tag.",
                    "Try: " + command + " 2 fun");
        }
        for (String tag : tags) {
            if (!Task.isValidTag(tag)) {
                throw new SallmanException("I'm sorry, but I can't use \"" + tag + "\" as a tag.",
                        "A tag is made of letters, digits, hyphens or underscores.");
            }
        }
        return tags;
    }

    /**
     * Reads the order given to a {@code sort} command.
     * <p>
     * Sorting by date is what the command is most often wanted for, so it is
     * what a bare {@code sort} does.
     *
     * @param arguments text the user typed after the command
     * @return the order to sort into
     * @throws SallmanException if the order given is not one that is understood
     */
    public static SortOrder parseSortOrder(String arguments) throws SallmanException {
        if (arguments.isEmpty()) {
            return SortOrder.DATE;
        }
        return SortOrder.fromKeyword(arguments);
    }

    /**
     * Reads the keyword given to a {@code find} command.
     *
     * @param arguments text the user typed after the command
     * @return the keyword to search for
     * @throws SallmanException if no keyword was given
     */
    public static String parseSearchKeyword(String arguments) throws SallmanException {
        if (arguments.isEmpty()) {
            throw new SallmanException("I'd love to help, but find needs something to search for.",
                    "Try: find book");
        }
        return collapseSpaces(arguments);
    }

    /**
     * Reads the date given to an {@code on} command.
     *
     * @param arguments text the user typed after the command
     * @return the date asked about
     * @throws SallmanException if the date is missing or cannot be read
     */
    public static LocalDate parseOnDate(String arguments) throws SallmanException {
        if (arguments.isEmpty()) {
            throw new SallmanException("I'd love to help, but on needs a date.",
                    "Try: on " + TaskDate.EXAMPLE);
        }
        return TaskDate.parse(arguments);
    }

    /**
     * Reads the task number given to a {@code mark} or {@code unmark} command.
     *
     * @param command   the command the number was given to, used in messages
     * @param arguments text the user typed after the command
     * @param taskCount number of tasks currently in the list
     * @return the matching index into the task array, counting from 0
     * @throws SallmanException if the number is missing, not a number, or
     *                          outside the list
     */
    public static int parseTaskNumber(String command, String arguments, int taskCount)
            throws SallmanException {
        if (arguments.isEmpty()) {
            throw new SallmanException("I'd love to help, but " + command + " needs a task number.",
                    "Try: " + command + " 2");
        }
        if (arguments.split("\\s+").length > 1) {
            throw new SallmanException("I'm sorry, but " + command + " takes one task number at a time.",
                    "Try: " + command + " 2");
        }
        int index;
        try {
            // Task numbers shown to the user start at 1, arrays start at 0.
            index = Integer.parseInt(arguments) - 1;
        } catch (NumberFormatException e) {
            if (arguments.matches("-?\\d+")) {
                // All digits, just too many for an int: it is a number, only not
                // one in the list, so calling it "not a number" would be wrong.
                throw noSuchTask(arguments, taskCount);
            }
            // Translate Java's exception into one phrased for the user.
            throw new SallmanException("I'm sorry, but \"" + arguments + "\" doesn't look like a task number.",
                    "Try: " + command + " 2");
        }
        if (taskCount == 0 || index < 0 || index >= taskCount) {
            throw noSuchTask(arguments, taskCount);
        }
        // Every out-of-range number has been rejected above, so whoever called
        // this can index the list straight away without checking again.
        assert index >= 0 && index < taskCount
                : "returning out-of-range index " + index + " for " + taskCount + " tasks";
        return index;
    }

    /**
     * Returns the error for a task number that is not in the list.
     *
     * @param number    the number as the user typed it
     * @param taskCount number of tasks currently in the list
     * @return the exception to throw, naming the numbers that are valid
     */
    private static SallmanException noSuchTask(String number, int taskCount) {
        if (taskCount == 0) {
            return new SallmanException("As a large language model, I must point out that your list "
                    + "is empty, so there is no task " + number + ".", TODO_EXAMPLE);
        }
        return new SallmanException("As a large language model, I must point out that there is no task "
                + number + ".",
                taskCount == 1
                        ? "You only have task 1."
                        : "Pick a number from 1 to " + taskCount + ".");
    }

    /**
     * Builds a todo from the text following the {@code todo} command.
     *
     * @param arguments text the user typed after the command
     * @return the new task
     * @throws SallmanException if the description is missing
     */
    public static Task parseTodo(String arguments) throws SallmanException {
        if (arguments.isEmpty()) {
            throw new SallmanException("I'd love to help, but a todo needs a description.", TODO_EXAMPLE);
        }
        return new Todo(collapseSpaces(arguments));
    }

    /**
     * Builds a deadline from the text following the {@code deadline} command,
     * e.g. {@code return book /by 2019-10-15}.
     *
     * @param arguments text the user typed after the command
     * @return the new task
     * @throws SallmanException if the {@code /by}, the description, or the due
     *                          date is missing, or the date cannot be read
     */
    public static Task parseDeadline(String arguments) throws SallmanException {
        String[] parts = splitAtMarker(arguments, "/by", "deadline", DEADLINE_EXAMPLE);
        String description = collapseSpaces(parts[0]);
        String by = parts[1].trim();
        requirePresent(description, "I'd love to help, but that deadline has no description before the /by.",
                DEADLINE_EXAMPLE);
        requirePresent(by, "I'd love to help, but that deadline has no due date after the /by.",
                DEADLINE_EXAMPLE);
        return new Deadline(description, TaskDate.parse(by));
    }

    /**
     * Builds an event from the text following the {@code event} command,
     * e.g. {@code project meeting /from 2019-10-15 /to 2019-10-16}.
     *
     * @param arguments text the user typed after the command
     * @return the new task
     * @throws SallmanException if any of the description, {@code /from} or
     *                          {@code /to} parts is missing, a date cannot be
     *                          read, or the end falls before the start
     */
    public static Task parseEvent(String arguments) throws SallmanException {
        Matcher fromMarker = markerPattern("/from").matcher(arguments);
        Matcher toMarker = markerPattern("/to").matcher(arguments);
        if (fromMarker.find() && toMarker.find() && toMarker.start() < fromMarker.start()) {
            // Otherwise the /to would be swallowed into the description, and the
            // user told there is no /to at all.
            throw new SallmanException("I'm sorry, but the /from has to come before the /to in that event.",
                    EVENT_EXAMPLE);
        }
        String[] fromParts = splitAtMarker(arguments, "/from", "event", EVENT_EXAMPLE);
        String[] toParts = splitAtMarker(fromParts[1], "/to", "event", EVENT_EXAMPLE);
        String description = collapseSpaces(fromParts[0]);
        String from = toParts[0].trim();
        String to = toParts[1].trim();
        requirePresent(description, "I'd love to help, but that event has no description before the /from.",
                EVENT_EXAMPLE);
        requirePresent(from, "I'd love to help, but that event has no start time after the /from.",
                EVENT_EXAMPLE);
        requirePresent(to, "I'd love to help, but that event has no end time after the /to.",
                EVENT_EXAMPLE);
        LocalDate start = TaskDate.parse(from);
        LocalDate end = TaskDate.parse(to);
        // An event may start and end on the same date. Events are recorded in
        // whole days, with no times, so that is a one-day event rather than one
        // lasting no time at all; rejecting it would leave no way to record one.
        if (end.isBefore(start)) {
            // Such an event covers no days at all, so it could never be found
            // by the on command and is almost certainly a typo.
            throw new SallmanException(
                    "As a large language model, I can't schedule an event that ends before it starts.",
                    "Check the order of the /from and /to dates.");
        }
        return new Event(description, start, end);
    }
}
