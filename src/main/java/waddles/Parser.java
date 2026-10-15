package waddles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;

/**
 * Deals with making sense of the user's raw command input.
 */
public class Parser {
    private static final int TODO_PREFIX_LENGTH = "todo".length();
    private static final int DEADLINE_PREFIX_LENGTH = "deadline".length();
    private static final int EVENT_PREFIX_LENGTH = "event".length();
    private static final int FIND_PREFIX_LENGTH = "find".length();

    /**
     * Accepts yyyy-mm-dd, optionally followed by a space and a 24-hour
     * HHmm time (e.g. "2026-09-09" or "2026-09-09 1800"). Event's fields
     * are plain LocalDate (see Event.java), so the time — when present —
     * is only validated and compared here; it isn't retained in the
     * structured date used for recurrence/schedule matching. A missing
     * time defaults to midnight, purely so /from and /to can be compared
     * against each other regardless of whether either supplied a time.
     */
    private static final DateTimeFormatter EVENT_DATE_TIME_FORMAT = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .optionalStart()
            .appendLiteral(' ')
            .appendPattern("HHmm")
            .optionalEnd()
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Extracts the command word (the first word) from the user's input.
     *
     * @param input The full line of user input.
     * @return The command word, e.g. "todo", "list", "mark".
     */
    public static String getCommandWord(String input) {
        return input.split(" ", 2)[0];
    }

    /**
     * Parses the task number following a command word (e.g. "mark 2")
     * into a zero-indexed position within the task list.
     *
     * @param input The full line of user input.
     * @param command The command word already matched, e.g. "mark".
     * @param taskCount The current number of tasks, used for bounds checking.
     * @return The zero-indexed task position.
     * @throws WaddlesException If the number is missing, invalid, or out of range.
     */
    public static int parseTaskIndex(String input, String command, int taskCount) throws WaddlesException {
        String numberPart = input.length() > command.length() ? input.substring(command.length()).trim() : "";
        int index;
        try {
            index = Integer.parseInt(numberPart) - 1;
        } catch (NumberFormatException e) {
            throw new WaddlesException(
                    WaddlesException.ERROR_PREFIX + " Oink! That's not a valid task number, try e.g. "
                            + command + " 2");
        }
        if (index < 0 || index >= taskCount) {
            throw new WaddlesException(WaddlesException.ERROR_PREFIX + " Oink! That task number isn't in the trough.");
        }
        assert index >= 0 && index < taskCount : "index must be within bounds here, since the "
                + "out-of-range case above already returns via an exception";
        return index;
    }

    /**
     * Parses a "todo" command into a ToDo task.
     *
     * @param input The full line of user input.
     * @return The parsed ToDo task.
     * @throws WaddlesException If the description is empty.
     */
    public static Task parseTodo(String input) throws WaddlesException {
        String description = input.length() > TODO_PREFIX_LENGTH ? input.substring(TODO_PREFIX_LENGTH).trim() : "";
        if (description.isEmpty()) {
            throw new WaddlesException(
                    WaddlesException.ERROR_PREFIX + " Oink! A todo needs a description — don't leave me guessing!");
        }
        return new ToDo(description);
    }

    /**
     * Parses a "deadline" command into a Deadline task.
     *
     * @param input The full line of user input.
     * @return The parsed Deadline task.
     * @throws WaddlesException If the description or /by date is missing,
     *     or the /by date isn't a valid yyyy-mm-dd date.
     */
    public static Task parseDeadline(String input) throws WaddlesException {
        String rest = input.length() > DEADLINE_PREFIX_LENGTH
                ? input.substring(DEADLINE_PREFIX_LENGTH).trim() : "";

        Recurrence recurrence = Recurrence.NONE;
        if (rest.contains(" /every ")) {
            String[] withRecurrence = rest.split(" /every ", 2);
            rest = withRecurrence[0].trim();
            recurrence = parseRecurrenceKeyword(withRecurrence[1].trim());
        }

        if (!rest.contains(" /by ")) {
            throw new WaddlesException(
                    WaddlesException.ERROR_PREFIX + " Oink! A deadline needs a description and a /by date, "
                            + "e.g. deadline return book /by 2024-12-01");
        }
        String[] parts = rest.split(" /by ", 2);
        String description = parts[0].trim();
        String by = parts[1].trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new WaddlesException(
                    WaddlesException.ERROR_PREFIX + " Oink! A deadline needs both a description and a /by date.");
        }
        requireValidDate(by, "deadline return book /by 2024-12-01");

        Deadline deadline = new Deadline(description, by);
        if (recurrence != Recurrence.NONE) {
            if (!deadline.hasStructuredDate()) {
                throw new WaddlesException(
                        WaddlesException.ERROR_PREFIX
                                + " Oink! A recurring deadline needs its date in yyyy-mm-dd format, "
                                + "e.g. deadline return book /by 2024-12-01 /every week");
            }
            deadline.setRecurrence(recurrence);
        }
        return deadline;
    }

    /**
     * Parses an "event" command into an Event task.
     *
     * @param input The full line of user input.
     * @return The parsed Event task.
     * @throws WaddlesException If the description, /from, or /to is missing,
     *     /from or /to isn't a valid yyyy-mm-dd date (optionally followed
     *     by an HHmm time), or /to is before /from.
     */
    public static Task parseEvent(String input) throws WaddlesException {
        String rest = input.length() > EVENT_PREFIX_LENGTH ? input.substring(EVENT_PREFIX_LENGTH).trim() : "";

        Recurrence recurrence = Recurrence.NONE;
        if (rest.contains(" /every ")) {
            String[] withRecurrence = rest.split(" /every ", 2);
            rest = withRecurrence[0].trim();
            recurrence = parseRecurrenceKeyword(withRecurrence[1].trim());
        }

        if (!rest.contains(" /from ") || !rest.contains(" /to ")) {
            throw new WaddlesException(
                    WaddlesException.ERROR_PREFIX
                            + " Oink! An event needs a description, a /from time, and a /to time, "
                            + "e.g. event meeting /from 2024-12-01 /to 2024-12-02");
        }
        String[] parts = rest.split(" /from ", 2);
        String description = parts[0].trim();
        String[] fromTo = parts[1].split(" /to ", 2);
        String from = fromTo[0].trim();
        String to = fromTo[1].trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new WaddlesException(
                    WaddlesException.ERROR_PREFIX
                            + " Oink! An event needs a description, a /from time, and a /to time.");
        }
        requireValidDateTime(from, "event meeting /from 2024-12-01 /to 2024-12-02");
        requireValidDateTime(to, "event meeting /from 2024-12-01 /to 2024-12-02");
        if (parseEventDateTime(to).isBefore(parseEventDateTime(from))) {
            throw new WaddlesException(
                    WaddlesException.ERROR_PREFIX + " Oink! An event can't end before it starts — /to must be "
                            + "on or after /from, e.g. event meeting /from 2024-12-01 1400 /to 2024-12-01 1500");
        }

        Event event = new Event(description, from, to);
        if (recurrence != Recurrence.NONE) {
            // setRecurrence() backfills a structured (date-only) fromDate/toDate
            // when /from or /to included a time, since recurrence needs one to
            // advance by; the hasStructuredDate() check below must come after.
            event.setRecurrence(recurrence);
            if (!event.hasStructuredDate()) {
                throw new WaddlesException(
                        WaddlesException.ERROR_PREFIX
                                + " Oink! A recurring event needs both dates in yyyy-mm-dd format, "
                                + "e.g. event standup /from 2024-12-01 /to 2024-12-01 /every day");
            }
        }
        return event;
    }

    /**
     * Checks that the given text is a real calendar date in the one format
     * Waddles accepts: yyyy-mm-dd (e.g. "2024-12-01"). Any other format,
     * including slash-separated dates like "2024/12/01", or a date that
     * doesn't exist on the calendar (e.g. month 13), is rejected here so
     * that invalid input is never silently stored as raw, unparsed text.
     *
     * @param text The text the user supplied where a date was expected.
     * @param exampleCommand A full example command shown in the error message.
     * @throws WaddlesException If the text isn't a valid yyyy-mm-dd date.
     */
    private static void requireValidDate(String text, String exampleCommand) throws WaddlesException {
        try {
            LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new WaddlesException(
                    WaddlesException.ERROR_PREFIX + " Oink! '" + text + "' isn't a date I recognise — "
                            + "dates must be given as yyyy-mm-dd (e.g. 2024-12-01), so try again, "
                            + "e.g. " + exampleCommand);
        }
    }

    /**
     * Checks that the given text is a real calendar date in yyyy-mm-dd
     * format, optionally followed by a 24-hour HHmm time (e.g. "2026-09-09"
     * or "2026-09-09 1800") — the format Event's /from and /to accept.
     * Anything else, including a wrong date separator, an invalid calendar
     * date, or a malformed time, is rejected here so it's never silently
     * stored as raw, unparsed text.
     *
     * @param text The text the user supplied where a date (and optional time) was expected.
     * @param exampleCommand A full example command shown in the error message.
     * @throws WaddlesException If the text isn't a valid yyyy-mm-dd date with an optional HHmm time.
     */
    private static void requireValidDateTime(String text, String exampleCommand) throws WaddlesException {
        try {
            EVENT_DATE_TIME_FORMAT.parse(text);
        } catch (DateTimeParseException e) {
            throw new WaddlesException(
                    WaddlesException.ERROR_PREFIX + " Oink! '" + text + "' isn't a date (and time) I recognise — "
                            + "dates must be given as yyyy-mm-dd, optionally followed by a 24-hour time "
                            + "(e.g. 2024-12-01 or 2024-12-01 1800), so try again, e.g. " + exampleCommand);
        }
    }

    /**
     * Parses an already-validated Event /from or /to string into a
     * LocalDateTime, defaulting to midnight when no HHmm time was given.
     * Only meant to be called after {@link #requireValidDateTime} has
     * already confirmed the text parses cleanly.
     *
     * @param text A date, optionally with an HHmm time, already known to be valid.
     * @return The corresponding LocalDateTime.
     */
    private static LocalDateTime parseEventDateTime(String text) {
        return LocalDateTime.from(EVENT_DATE_TIME_FORMAT.parse(text));
    }

    /**
     * Parses the word after "/every" (e.g. "week") into a Recurrence.
     *
     * @param keyword The word the user typed.
     * @return The matching Recurrence.
     * @throws WaddlesException If the keyword isn't one of the recognised recurrence periods.
     */
    private static Recurrence parseRecurrenceKeyword(String keyword) throws WaddlesException {
        Recurrence recurrence = Recurrence.fromKeyword(keyword);
        if (recurrence == null) {
            throw new WaddlesException(
                    WaddlesException.ERROR_PREFIX
                            + " Oink! /every must be followed by one of: daily, weekly, monthly "
                            + "(or day, week, month)");
        }
        return recurrence;
    }

    /**
     * Parses a "find" command into the keyword to search for.
     *
     * @param input The full line of user input.
     * @return The keyword to search for.
     * @throws WaddlesException If the keyword is empty.
     */
    public static String parseFind(String input) throws WaddlesException {
        String keyword = input.length() > FIND_PREFIX_LENGTH ? input.substring(FIND_PREFIX_LENGTH).trim() : "";
        if (keyword.isEmpty()) {
            throw new WaddlesException(
                    WaddlesException.ERROR_PREFIX + " Oink! Give me a keyword to search for, e.g. find book");
        }
        return keyword;
    }

    /**
     * Parses a "schedule" command into the date whose schedule should be shown.
     *
     * @param input The full line of user input.
     * @return The date to view the schedule for.
     * @throws WaddlesException If no date is given, or it isn't "today" or a valid yyyy-mm-dd date.
     */
    public static LocalDate parseSchedule(String input) throws WaddlesException {
        String text = input.length() > 8 ? input.substring(8).trim() : "";
        if (text.isEmpty()) {
            throw new WaddlesException(
                    WaddlesException.ERROR_PREFIX
                            + " Oink! Give me a date, e.g. schedule 2024-12-01 or schedule today");
        }
        if (text.equalsIgnoreCase("today")) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new WaddlesException(
                    WaddlesException.ERROR_PREFIX
                            + " Oink! Give the date as yyyy-mm-dd (e.g. 2024-12-01), or use 'today'.");
        }
    }
}
