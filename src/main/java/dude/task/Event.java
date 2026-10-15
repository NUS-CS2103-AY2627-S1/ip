package dude.task;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import dude.exception.UsageDetails;
import dude.exception.UsageException;

/**
 * A task that starts and ends at specified dates or times.
 */
public class Event extends Task {
    private static final String USAGE_MESSAGE = "Usage: event <description> /from"
            + " <yyyy-MM-dd [HHmm]> /to <yyyy-MM-dd [HHmm]>";

    /**
     * Date or time when this event starts.
     */
    private final EventPeriod period;

    /**
     * Creates an incomplete event task with its start and end dates or times.
     *
     * @param description Text describing the event.
     * @param period      Date and time range of the event.
     */
    public Event(String description, EventPeriod period) {
        super(description);
        this.period = Objects.requireNonNull(period, "period");
    }

    /**
     * Creates an event task from command input.
     *
     * @param input Raw text following the event command.
     * @return Parsed event task.
     * @throws UsageException If the input does not contain valid event details.
     */
    public static Event fromInput(String input) throws UsageException {
        String trimmed = validateInput(input);
        String[] eventParts = splitEventDescription(trimmed);
        String[] timeParts = splitEventTimes(eventParts[1]);
        return new Event(eventParts[0], parsePeriod(timeParts));
    }

    /**
     * Validates and trims raw event input.
     *
     * @param input Raw event input.
     * @return Trimmed event input.
     * @throws UsageException If the input is missing.
     */
    private static String validateInput(String input) throws UsageException {
        if (input == null || input.isBlank() || input.trim().startsWith("/from")) {
            throw usageError(new UsageDetails("event", "task details", "<missing>",
                    "non-empty text", USAGE_MESSAGE, "<task details>"));
        }
        return input.trim();
    }

    /**
     * Splits the description from the event's date range.
     *
     * @param input Trimmed event input.
     * @return Description and date-range parts.
     * @throws UsageException If the /from delimiter is invalid.
     */
    private static String[] splitEventDescription(String input) throws UsageException {
        if (countStandaloneTokens(input, "/from") != 1
                || countStandaloneTokens(input, "/to") != 1) {
            throw usageError(new UsageDetails("event", "details", input,
                    "exactly one /from and one /to delimiter", USAGE_MESSAGE, "/from"));
        }
        String[] eventParts = splitAt(input, "/from");
        if (eventParts == null) {
            String token = containsStandaloneToken(input, "/from") ? "<description>" : "/from";
            throw usageError(new UsageDetails("event", "from", input,
                    "a start date/time after /from", USAGE_MESSAGE, token));
        }
        return eventParts;
    }

    /**
     * Splits an event date range into its start and end values.
     *
     * @param dateRange Event date-range input.
     * @return Start and end date strings.
     * @throws UsageException If the /to delimiter is invalid.
     */
    private static String[] splitEventTimes(String dateRange) throws UsageException {
        String[] timeParts = splitAt(dateRange, "/to");
        if (timeParts == null) {
            String token = containsStandaloneToken(dateRange, "/to") ? "<end>" : "/to";
            throw usageError(new UsageDetails("event", "to", dateRange,
                    "an end date/time after /to", USAGE_MESSAGE, token));
        }
        return timeParts;
    }

    /**
     * Parses and validates the endpoints of an event.
     *
     * @param timeParts Start and end date strings.
     * @return Validated event period.
     * @throws UsageException If either date or the period is invalid.
     */
    private static EventPeriod parsePeriod(String[] timeParts) throws UsageException {
        TaskDate from = parseDate(timeParts[0], "from");
        TaskDate to = parseDate(timeParts[1], "to");
        try {
            return new EventPeriod(from, to);
        } catch (IllegalArgumentException exception) {
            throw usageError(new UsageDetails("event", "to", timeParts[1],
                    "a date after the start date", USAGE_MESSAGE,
                    "<yyyy-MM-dd [HHmm]>"), exception);
        }
    }

    /**
     * Parses one event endpoint.
     *
     * @param value Endpoint date string.
     * @param field Endpoint field name.
     * @return Parsed endpoint.
     * @throws UsageException If the endpoint is invalid.
     */
    private static TaskDate parseDate(String value, String field) throws UsageException {
        try {
            return TaskDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw usageError(new UsageDetails("event", field, value,
                    "yyyy-MM-dd or yyyy-MM-dd HHmm", USAGE_MESSAGE,
                    "<yyyy-MM-dd [HHmm]>"), exception);
        }
    }

    /**
     * Creates a usage exception without an underlying parsing cause.
     *
     * @param details Structured usage details.
     * @return Usage exception.
     */
    private static UsageException usageError(UsageDetails details) {
        return new UsageException(details);
    }

    /**
     * Returns a usage exception for an invalid date with its parsing cause.
     *
     * @param details Structured usage details.
     * @param cause   Underlying parsing cause.
     * @return Usage exception.
     */
    private static UsageException usageError(UsageDetails details, Throwable cause) {
        return new UsageException(details, cause);
    }

    /**
     * Returns the start date or time of this event.
     *
     * @return Event start date or time.
     */
    public TaskDate getFrom() {
        return period.from();
    }

    /**
     * Returns the end date or time of this event.
     *
     * @return Event end date or time.
     */
    public TaskDate getTo() {
        return period.to();
    }

    /**
     * Returns whether this event occurs on the supplied date, including dates between its endpoints.
     *
     * @param targetDate Date to compare with.
     * @return True when the event includes the target date.
     */
    @Override
    public boolean occursOn(LocalDate targetDate) {
        return !targetDate.isBefore(getFrom().date()) && !targetDate.isAfter(getTo().date());
    }

    /**
     * Returns the formatted event representation.
     *
     * @return Event type marker followed by the task and event range.
     */
    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(), getFrom(), getTo());
    }
}
