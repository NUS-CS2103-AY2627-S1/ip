package elora.task;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import elora.EloraException;

/**
 * Builds validated {@link Task} instances from the raw argument text
 * following a command word, e.g. the "read book /by 2019-10-15" in
 * "deadline read book /by 2019-10-15". Each {@code createX} method
 * either returns a fully valid task or throws an {@link EloraException}
 * describing what's wrong with the input, keeping all of a command's
 * argument-parsing rules in one place instead of mixed into command
 * execution.
 */
public class TaskFactory {
    private static final String BY_DELIMITER = " /by ";
    private static final String FROM_DELIMITER = " /from ";
    private static final String TO_DELIMITER = " /to ";
    private static final String RESERVED_CHARACTER = "|";

    /**
     * Matches text with the exact shape of an ISO date (yyyy-mm-dd), whether
     * or not it's a real calendar date. Used to tell apart an event time
     * that's meant to be a date but got the day/month wrong (e.g.
     * "2029-01-32", which should be rejected) from genuine free text (e.g.
     * "Mon 2pm", which should be accepted as-is).
     */
    private static final Pattern ISO_DATE_SHAPE = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    /**
     * Builds a Todo from a todo command's arguments.
     *
     * @param arguments The text after the "todo" command word.
     * @return The new Todo.
     * @throws EloraException If the description is missing or invalid.
     */
    public Todo createTodo(String arguments) throws EloraException {
        String description = requireNonEmpty(arguments,
                "Hold on - a todo needs a description. What would you like to remember?");
        requireNoReservedCharacter(description);
        return new Todo(description);
    }

    /**
     * Builds a Deadline from a deadline command's arguments.
     *
     * @param arguments The text after the "deadline" command word.
     * @return The new Deadline.
     * @throws EloraException If the description, /by, or date is missing or invalid.
     */
    public Deadline createDeadline(String arguments) throws EloraException {
        requireNonEmpty(arguments, "Hold on - a deadline needs a description too. What's due?");
        requireContains(arguments, BY_DELIMITER, "Hold on - I'll need a /by time to know when this"
                + " is due. Try: deadline return book /by Sunday");
        requireSingleOccurrence(arguments, BY_DELIMITER,
                "Hold on - I see more than one /by. Please give just one due date.");
        String[] parts = arguments.split(BY_DELIMITER, 2);
        String description = requireNonEmpty(parts[0].trim(),
                "Hold on - a deadline needs a description too. What's due?");
        String byText = requireNonEmpty(parts[1].trim(),
                "Hold on - you've given me a /by, but no actual date. When's this due?");
        requireNoReservedCharacter(description);
        return new Deadline(description, parseIsoDate(byText));
    }

    /**
     * Builds an Event from an event command's arguments.
     *
     * @param arguments The text after the "event" command word.
     * @return The new Event.
     * @throws EloraException If the description, /from, or /to is missing or
     *     invalid, or the event ends before (or when) it starts.
     */
    public Event createEvent(String arguments) throws EloraException {
        requireNonEmpty(arguments, "Hold on - an event needs a description. What's happening?");
        requireContains(arguments, FROM_DELIMITER, "Hold on - I'll need a /from time to know when this"
                + " starts. Try: event meeting /from Mon 2pm /to 4pm");
        requireSingleOccurrence(arguments, FROM_DELIMITER,
                "Hold on - I see more than one /from. Please give just one start time.");
        String[] fromParts = arguments.split(FROM_DELIMITER, 2);
        String description = requireNonEmpty(fromParts[0].trim(),
                "Hold on - an event needs a description. What's happening?");
        String[] toParts = splitOnToDelimiter(fromParts[1]);
        String from = requireNonEmpty(toParts[0].trim(),
                "Hold on - when does this begin? I'm missing the /from time.");
        String to = requireNonEmpty(toParts[1].trim(),
                "Hold on - and when does it end? I'm missing the /to time.");
        requireNoReservedCharacter(description);
        requireValidEventTimes(from, to);
        return new Event(description, from, to);
    }

    /**
     * Validates an event's start and end times: neither may contain the
     * reserved '|' character, neither may have the shape of an ISO date
     * (yyyy-mm-dd) without being a real one, and if both are ISO dates,
     * to must come after from.
     *
     * @param from The event's start time, as typed by the user.
     * @param to The event's end time, as typed by the user.
     * @throws EloraException If any of the above checks fail.
     */
    private static void requireValidEventTimes(String from, String to) throws EloraException {
        requireNoReservedCharacter(from);
        requireNoReservedCharacter(to);
        requireValidIfDateShaped(from, "/from");
        requireValidIfDateShaped(to, "/to");
        requireChronologicalOrder(tryParseIsoDate(from), tryParseIsoDate(to));
    }

    /**
     * Splits the text following /from into its /to time, first validating
     * that /to is present and given only once.
     *
     * @param textAfterFrom The text following /from (including /to and after).
     * @return The two-element split on /to.
     * @throws EloraException If /to is missing or given more than once.
     */
    private String[] splitOnToDelimiter(String textAfterFrom) throws EloraException {
        requireContains(textAfterFrom, TO_DELIMITER,
                "Hold on - I still need a /to time to know when this ends.");
        requireSingleOccurrence(textAfterFrom, TO_DELIMITER,
                "Hold on - I see more than one /to. Please give just one end time.");
        return textAfterFrom.split(TO_DELIMITER, 2);
    }

    /**
     * Rejects a blank string, returning it unchanged otherwise.
     *
     * @param text The text to check.
     * @param errorMessage The error to report if text is blank.
     * @return text, unchanged.
     * @throws EloraException If text is empty.
     */
    private static String requireNonEmpty(String text, String errorMessage) throws EloraException {
        if (text.isEmpty()) {
            throw new EloraException(errorMessage);
        }
        return text;
    }

    /**
     * Rejects text that doesn't contain the given delimiter.
     *
     * @param text The text to check.
     * @param delimiter The delimiter that must be present.
     * @param errorMessage The error to report if it's missing.
     * @throws EloraException If delimiter isn't found in text.
     */
    private static void requireContains(String text, String delimiter, String errorMessage)
            throws EloraException {
        if (!text.contains(delimiter)) {
            throw new EloraException(errorMessage);
        }
    }

    /**
     * Rejects text where the given delimiter appears more than once, so a
     * parameter like /by given twice is caught with a clear message
     * instead of producing a confusing downstream parse error.
     *
     * @param text The text to check.
     * @param delimiter The delimiter to count.
     * @param errorMessage The error to report if it appears more than once.
     * @throws EloraException If delimiter occurs more than once in text.
     */
    private static void requireSingleOccurrence(String text, String delimiter, String errorMessage)
            throws EloraException {
        int occurrences = (text.length() - text.replace(delimiter, "").length()) / delimiter.length();
        if (occurrences > 1) {
            throw new EloraException(errorMessage);
        }
    }

    /**
     * Rejects text containing the '|' character, since the save file
     * uses " | " to separate fields; letting it through would corrupt
     * the save file and shift fields when the file is next loaded.
     *
     * @param text The user-supplied text to check.
     * @throws EloraException If text contains '|'.
     */
    private static void requireNoReservedCharacter(String text) throws EloraException {
        if (text.contains(RESERVED_CHARACTER)) {
            throw new EloraException(
                    "Hold on - task details can't contain the '|' character; "
                    + "I use that internally to save your tasks.");
        }
    }

    /**
     * Parses text as an ISO date (yyyy-mm-dd), reporting a friendly error
     * (rather than a raw parser exception) if it isn't a valid calendar
     * date - including non-existent ones like Feb 30.
     *
     * @param text The text to parse.
     * @return The parsed date.
     * @throws EloraException If text isn't a valid yyyy-mm-dd date.
     */
    private static LocalDate parseIsoDate(String text) throws EloraException {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new EloraException(
                    "Hold on - I don't understand that date. Please use yyyy-mm-dd, like 2019-10-15.");
        }
    }

    /**
     * Rejects an event whose start and end are both parseable as plain
     * ISO dates (yyyy-mm-dd) and out of order, i.e. the end date is the
     * same as or before the start date. Event times are otherwise free
     * text (e.g. "Mon 2pm"), so this check only fires when both sides
     * happen to be dates; a free-text time is left unvalidated.
     *
     * @param fromDate The event's start time, parsed as a date, or null if
     *     it isn't one.
     * @param toDate The event's end time, parsed as a date, or null if it
     *     isn't one.
     * @throws EloraException If both are dates and toDate isn't after fromDate.
     */
    private static void requireChronologicalOrder(LocalDate fromDate, LocalDate toDate) throws EloraException {
        if (fromDate != null && toDate != null && !toDate.isAfter(fromDate)) {
            throw new EloraException(
                    "Hold on - an event's end date can't be the same as or before its start date.");
        }
    }

    /**
     * Rejects text that has the exact shape of an ISO date (yyyy-mm-dd) but
     * isn't a real calendar date, e.g. "2029-01-32" or "2028-02-30". Text
     * that doesn't have that shape at all (e.g. "Mon 2pm") is left alone,
     * since event times are otherwise free text.
     *
     * @param text The event's /from or /to text, as typed by the user.
     * @param label Which field text is, for the error message ("/from" or "/to").
     * @throws EloraException If text has the yyyy-mm-dd shape but isn't a valid date.
     */
    private static void requireValidIfDateShaped(String text, String label) throws EloraException {
        if (!ISO_DATE_SHAPE.matcher(text).matches()) {
            return;
        }
        if (tryParseIsoDate(text) == null) {
            throw new EloraException(
                    "Hold on - \"" + text + "\" looks like a yyyy-mm-dd date for " + label
                    + ", but it isn't a real calendar date.");
        }
    }

    /**
     * Parses text as an ISO date (yyyy-mm-dd), returning null instead of
     * throwing if it isn't one, since event times are usually free text.
     *
     * @param text The text to try to parse.
     * @return The parsed date, or null if text isn't a valid ISO date.
     */
    private static LocalDate tryParseIsoDate(String text) {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
