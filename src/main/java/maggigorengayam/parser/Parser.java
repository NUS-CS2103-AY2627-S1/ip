package maggigorengayam.parser;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import maggigorengayam.MaggiGorengAyamException;
import maggigorengayam.command.AddCommand;
import maggigorengayam.command.Command;
import maggigorengayam.command.DeleteCommand;
import maggigorengayam.command.ExitCommand;
import maggigorengayam.command.FindCommand;
import maggigorengayam.command.FreeTimeCommand;
import maggigorengayam.command.HelpCommand;
import maggigorengayam.command.ListCommand;
import maggigorengayam.command.MarkCommand;
import maggigorengayam.command.OnCommand;
import maggigorengayam.command.UnmarkCommand;
import maggigorengayam.task.Deadline;
import maggigorengayam.task.Event;
import maggigorengayam.task.Task;
import maggigorengayam.task.ToDo;
import maggigorengayam.util.DateTimeUtil;

/**
 * Turns one raw command line into a {@link Command}, ready for
 * MaggiGorengAyam's main loop to execute without needing to know how any
 * particular command works. Parser only knows about the text the user
 * typed - it has no TaskList, so a command that needs a task number (e.g.
 * `mark 5`) is built with the number as typed, syntactically validated
 * (a whole number) but not yet range-checked; that check happens inside
 * the command's own execute(), once it has a live TaskList.
 */
public class Parser {
    /**
     * Parses one raw command line typed by the user into the
     * {@link Command} that should run for it.
     *
     * @throws MaggiGorengAyamException if the command is unrecognized or
     *         its arguments are malformed.
     */
    public static Command parse(String command) throws MaggiGorengAyamException {
        if (command.equals("bye")) {
            return new ExitCommand();
        }
        if (command.equals("list")) {
            return new ListCommand();
        }
        if (command.equals("help")) {
            return new HelpCommand();
        }
        if (command.equals("on") || command.startsWith("on ")) {
            return parseOn(command);
        }
        if (command.equals("find") || command.startsWith("find ")) {
            return parseFind(command);
        }
        if (command.equals("freetime") || command.startsWith("freetime ")) {
            return parseFreeTime(command);
        }
        if (command.equals("mark") || command.startsWith("mark ")) {
            return new MarkCommand(parseTaskNumber(command.substring(4).trim(), "mark"));
        }
        if (command.equals("unmark") || command.startsWith("unmark ")) {
            return new UnmarkCommand(parseTaskNumber(command.substring(6).trim(), "unmark"));
        }
        if (command.equals("delete") || command.startsWith("delete ")) {
            return new DeleteCommand(parseTaskNumber(command.substring(6).trim(), "delete"));
        }
        if (command.equals("todo") || command.startsWith("todo ")) {
            return new AddCommand(parseTodo(command));
        }
        if (command.equals("deadline") || command.startsWith("deadline ")) {
            return new AddCommand(parseDeadline(command));
        }
        if (command.equals("event") || command.startsWith("event ")) {
            return new AddCommand(parseEvent(command));
        }
        throw new MaggiGorengAyamException("Huh? What is that one, I don't understand leh.");
    }

    /** Parses the argument after {@code on} into an {@link OnCommand}. */
    private static Command parseOn(String command) throws MaggiGorengAyamException {
        String dateArg = command.substring(2).trim();
        if (dateArg.isEmpty()) {
            throw new MaggiGorengAyamException("Which date, you never say leh. Try like 'on 2019-12-02'.");
        }
        try {
            return new OnCommand(DateTimeUtil.parseDateOnly(dateArg));
        } catch (DateTimeParseException e) {
            throw new MaggiGorengAyamException(
                    "'" + dateArg + "' not a proper date leh. Use yyyy-MM-dd, like 'on 2019-12-02'.");
        }
    }

    /** Parses the keyword after {@code find} into a {@link FindCommand}. */
    private static Command parseFind(String command) throws MaggiGorengAyamException {
        String keyword = command.substring(4).trim();
        if (keyword.isEmpty()) {
            throw new MaggiGorengAyamException("Find what, you tell me what word first. Try 'find book'.");
        }
        return new FindCommand(keyword);
    }

    /**
     * Parses the duration and working-hours window after {@code freetime}
     * into a {@link FreeTimeCommand}, e.g. {@code freetime 4 0900 1800}.
     */
    private static Command parseFreeTime(String command) throws MaggiGorengAyamException {
        String rest = command.substring(8).trim();
        String[] tokens = rest.isEmpty() ? new String[0] : rest.split("\\s+");
        if (tokens.length != 3) {
            throw new MaggiGorengAyamException(
                    "Usage: 'freetime <hours> <windowStartHHmm> <windowEndHHmm>' lah, "
                            + "e.g. 'freetime 4 0900 1800'.");
        }
        int durationMinutes = parseDurationMinutes(tokens[0]);
        LocalTime windowStart = parseWindowTime(tokens[1], "window start");
        LocalTime windowEnd = parseWindowTime(tokens[2], "window end");
        if (!windowEnd.isAfter(windowStart)) {
            throw new MaggiGorengAyamException(
                    "Window end (" + tokens[2] + ") must be later than window start (" + tokens[1] + ") lah.");
        }
        int windowMinutes = (windowEnd.toSecondOfDay() - windowStart.toSecondOfDay()) / 60;
        if (durationMinutes > windowMinutes) {
            throw new MaggiGorengAyamException(
                    "Confirm cannot fit a " + tokens[0] + "-hour slot inside a "
                            + tokens[1] + "-" + tokens[2] + " window lah.");
        }
        return new FreeTimeCommand(durationMinutes, windowStart, windowEnd);
    }

    /** Parses the duration argument to {@code freetime} (hours, possibly decimal) into whole minutes. */
    private static int parseDurationMinutes(String value) throws MaggiGorengAyamException {
        double hours;
        try {
            hours = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new MaggiGorengAyamException(
                    "Duration must be a number leh, like 'freetime 4 0900 1800'.");
        }
        int durationMinutes = (int) Math.round(hours * 60);
        if (durationMinutes <= 0) {
            throw new MaggiGorengAyamException("Duration must be more than 0 hours can or not.");
        }
        return durationMinutes;
    }

    /**
     * Parses an {@code HHmm} window-boundary argument to {@code freetime},
     * labelling errors with {@code fieldLabel}.
     */
    private static LocalTime parseWindowTime(String value, String fieldLabel) throws MaggiGorengAyamException {
        try {
            return DateTimeUtil.parseTimeOnly(value);
        } catch (DateTimeParseException e) {
            throw new MaggiGorengAyamException(
                    "'" + value + "' not a proper " + fieldLabel + " leh. Use 24-hour HHmm, like '0900'.");
        }
    }

    private static Task parseTodo(String command) throws MaggiGorengAyamException {
        String description = command.substring(4).trim();
        if (description.isEmpty()) {
            throw new MaggiGorengAyamException("What todo you want, you never say leh. Try 'todo buy milk'.");
        }
        requireNoReservedCharacters(description);
        return new ToDo(description);
    }

    /** Parses the description and {@code /by} date after {@code deadline} into a {@link Deadline}. */
    private static Task parseDeadline(String command) throws MaggiGorengAyamException {
        String rest = command.substring(8).trim();
        if (rest.isEmpty()) {
            throw new MaggiGorengAyamException(
                    "Wah, I cannot read mind one leh. Give me description and deadline can?");
        }
        if (!rest.contains(" /by ")) {
            throw new MaggiGorengAyamException(
                    "Eh, use '/by' for the deadline can. Like 'deadline return book /by Sunday'.");
        }
        requireMarkerNotDuplicated(rest, " /by ", "'/by'");
        String[] parts = rest.split(" /by ", 2);
        String description = parts[0].trim();
        String by = parts[1].trim();
        if (description.isEmpty()) {
            throw new MaggiGorengAyamException("Only date, no description? You want what sia?");
        }
        if (by.isEmpty()) {
            throw new MaggiGorengAyamException("No deadline one? Then for what you calling it deadline.");
        }
        requireNoReservedCharacters(description);
        DateTimeUtil.ParsedDateTime parsedBy = parseDateField(by, "deadline date");
        return new Deadline(description, parsedBy.date, parsedBy.time);
    }

    /** Parses the description and {@code /from}/{@code /to} dates after {@code event} into an {@link Event}. */
    private static Task parseEvent(String command) throws MaggiGorengAyamException {
        String rest = command.substring(5).trim();
        if (rest.isEmpty()) {
            throw new MaggiGorengAyamException("Huh, I no have telepathy leh, you must tell me properly.");
        }
        if (!rest.contains(" /from ")) {
            throw new MaggiGorengAyamException(
                    "Eh, use '/from' can. Like "
                            + "'event project meeting /from Mon 2pm /to 4pm'.");
        }
        requireMarkerNotDuplicated(rest, " /from ", "'/from'");
        String[] fromParts = rest.split(" /from ", 2);
        String description = fromParts[0].trim();
        String afterFrom = fromParts[1].trim();
        if (description.isEmpty()) {
            throw new MaggiGorengAyamException("Description where? You want event about what?");
        }
        if (!afterFrom.contains(" /to ")) {
            throw new MaggiGorengAyamException(
                    "Until when, you never say leh. Use '/to', like "
                            + "'event project meeting /from Mon 2pm /to 4pm'.");
        }
        requireMarkerNotDuplicated(afterFrom, " /to ", "'/to'");
        String[] toParts = afterFrom.split(" /to ", 2);
        String from = toParts[0].trim();
        String to = toParts[1].trim();
        if (from.isEmpty()) {
            throw new MaggiGorengAyamException("From when, you never say leh. Give a start time after '/from'.");
        }
        if (to.isEmpty()) {
            throw new MaggiGorengAyamException("To when, you never say leh. Give an end time after '/to'.");
        }
        requireNoReservedCharacters(description);
        DateTimeUtil.ParsedDateTime parsedFrom = parseDateField(from, "start date/time");
        DateTimeUtil.ParsedDateTime parsedTo = parseDateField(to, "end date/time");
        requireToAfterFrom(parsedFrom, parsedTo);
        return new Event(description, parsedFrom.date, parsedFrom.time, parsedTo.date, parsedTo.time);
    }

    /**
     * Rejects a marker (e.g. {@code /by}) that appears more than once in
     * {@code text}. Without this, a repeated marker doesn't fail cleanly -
     * the leftover second occurrence gets folded into the value after the
     * first split, producing a confusing "not a proper date" error instead
     * of naming the actual problem. Checked on the raw text (before
     * splitting) so even back-to-back repeats (e.g. "/by /by") are caught,
     * since the space between them would otherwise double as both markers'
     * boundary and hide from a post-split check.
     */
    private static void requireMarkerNotDuplicated(String text, String marker, String markerLabel)
            throws MaggiGorengAyamException {
        if (text.indexOf(marker) != text.lastIndexOf(marker)) {
            throw new MaggiGorengAyamException("Only can use " + markerLabel + " once leh, you got it twice.");
        }
    }

    /**
     * Rejects an event whose "to" is not strictly after its "from". A
     * missing time is treated as the very start of its day for "from" and
     * the very end of its day for "to", so an all-day same-day event (both
     * times unspecified) still counts as valid, while an exactly-equal
     * from/to (when both carry times) is correctly rejected.
     */
    private static void requireToAfterFrom(DateTimeUtil.ParsedDateTime from, DateTimeUtil.ParsedDateTime to)
            throws MaggiGorengAyamException {
        LocalDateTime effectiveFrom = LocalDateTime.of(from.date, from.time != null ? from.time : LocalTime.MIN);
        LocalDateTime effectiveTo = LocalDateTime.of(to.date, to.time != null ? to.time : LocalTime.MAX);
        if (!effectiveTo.isAfter(effectiveFrom)) {
            throw new MaggiGorengAyamException(
                    "Eh, your event ends before (or same time as) it starts leh. Check your /from and /to again.");
        }
    }

    /**
     * Parses a 1-indexed task number typed after "mark"/"unmark"/"delete".
     * Only checks that the text is a well-formed whole number; whether
     * that number actually exists in the current list is a range check
     * the command's execute() does once it has a live TaskList.
     */
    private static int parseTaskNumber(String arg, String commandName) throws MaggiGorengAyamException {
        if (arg.isEmpty()) {
            throw new MaggiGorengAyamException(
                    "Which task you want to " + commandName + ", never say leh. Try '" + commandName + " 2'.");
        }
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new MaggiGorengAyamException(
                    "Task number must be whole number one lah. Try '" + commandName + " 2'.");
        }
    }

    /**
     * Rejects a task description containing a character that would corrupt
     * the on-disk save format: {@code |} is the field delimiter used by
     * {@link Task#toSaveFormat()}/{@link Storage}, and {@code \n}/{@code \r}
     * would break the save file's one-task-per-line structure. Without this
     * check, such a description would be silently mis-parsed (or the task
     * silently dropped) on the next load - this check turns that silent
     * data loss into an immediate, explicit error at the point the user
     * enters the offending text. (Dates/times don't need this check:
     * {@link #parseDateField} already rejects any text that isn't a valid
     * date, which includes anything containing these characters.)
     */
    private static void requireNoReservedCharacters(String description) throws MaggiGorengAyamException {
        if (description.contains("|") || description.contains("\n") || description.contains("\r")) {
            throw new MaggiGorengAyamException(
                    "Eh cannot use '|' or a line break in the description leh, "
                            + "I need those for saving. Take it out can?");
        }
    }

    /**
     * Parses a "yyyy-MM-dd" or "yyyy-MM-dd HHmm" value typed after `/by`,
     * `/from`, or `/to`, converting an unparseable value into a friendly
     * {@link MaggiGorengAyamException} instead of a raw
     * {@link DateTimeParseException}.
     */
    private static DateTimeUtil.ParsedDateTime parseDateField(String value, String fieldLabel)
            throws MaggiGorengAyamException {
        try {
            return DateTimeUtil.parse(value);
        } catch (DateTimeParseException e) {
            throw new MaggiGorengAyamException(
                    "'" + value + "' not a proper " + fieldLabel + " leh. Use yyyy-MM-dd,"
                            + " can add 24-hour time also, like '2019-12-02' or '2019-12-02 1800'.");
        }
    }
}
