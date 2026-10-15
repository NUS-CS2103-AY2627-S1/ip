package charliek.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import charliek.parser.DateTimeParser;

/**
 * A task that must be completed before a specified date or time.
 */
public class Deadline extends Task {
    /**
     * The date by which the task should be completed, when no time was supplied.
     */
    private final LocalDate deadlineDate;

    /**
     * The date and time by which the task should be completed, when a time was supplied.
     */
    private final LocalDateTime deadlineDateTime;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description the text describing the task.
     * @param deadline the date or time by which the task should be completed.
     */
    public Deadline(String description, String deadline) {
        this(description, DateTimeParser.parseUserInput(deadline));
    }

    /**
     * Creates a deadline with a date-only value.
     *
     * @param description the text describing the task.
     * @param deadline the date by which the task should be completed.
     */
    public Deadline(String description, LocalDate deadline) {
        this(description, DateTimeParser.ParsedDateTime.ofDate(deadline));
    }

    /**
     * Creates a deadline with a date and time value.
     *
     * @param description the text describing the task.
     * @param deadline the date and time by which the task should be completed.
     */
    public Deadline(String description, LocalDateTime deadline) {
        this(description, DateTimeParser.ParsedDateTime.ofDateTime(deadline));
    }

    /**
     * Creates a deadline from a parsed date or date-time value.
     *
     * @param description the text describing the task.
     * @param deadline the parsed date or date-time by which the task should be completed.
     * @throws IllegalArgumentException if {@code deadline} is {@code null}.
     */
    public Deadline(String description, DateTimeParser.ParsedDateTime deadline) {
        super(description);
        if (deadline == null) {
            throw new IllegalArgumentException("A deadline date/time is required.");
        }
        this.deadlineDate = deadline.date();
        this.deadlineDateTime = deadline.dateTime();

        // A deadline stores exactly one representation so formatting and sorting cannot disagree.
        assert (deadlineDate == null) != (deadlineDateTime == null)
                : "A deadline must contain either a date or a date-time, but not both.";
    }

    /**
     * Returns the type of this task.
     *
     * @return {@link TaskType#DEADLINE}.
     */
    @Override
    protected TaskType getType() {
        return TaskType.DEADLINE;
    }

    /**
     * Returns the values stored for this deadline after the common task fields.
     *
     * @return the description and deadline values.
     */
    @Override
    public List<String> getStorageFields() {
        return List.of(getDescription(), DateTimeParser.formatForStorage(toParsedDateTime()));
    }

    /**
     * Returns the deadline in the task-list format.
     *
     * @return the formatted deadline.
     */
    @Override
    protected String getDateDetails() {
        return " (by: " + DateTimeParser.formatForDisplay(toParsedDateTime()) + ")";
    }

    /**
     * Returns the deadline as the task's chronological ordering key.
     *
     * @return the deadline at midnight for date-only values, or the supplied deadline time.
     */
    @Override
    public Optional<LocalDateTime> getSortDateTime() {
        LocalDateTime sortDateTime = deadlineDateTime == null
                ? deadlineDate.atStartOfDay()
                : deadlineDateTime;
        return Optional.of(sortDateTime);
    }

    /**
     * Returns this deadline as the date/time value used by the parser and formatter.
     */
    private DateTimeParser.ParsedDateTime toParsedDateTime() {
        return new DateTimeParser.ParsedDateTime(deadlineDate, deadlineDateTime);
    }
}
