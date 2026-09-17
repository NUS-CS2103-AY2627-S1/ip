package megatron.task;

import java.time.LocalDateTime;

/**
 * A task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private final LocalDateTime deadlineDateTime;

    /**
     * Creates a deadline by parsing a supported user date/time value.
     *
     * @param description The deadline description.
     * @param deadlineInput The supported deadline date/time input.
     */
    public Deadline(String description, String deadlineInput) {
        super(description, TaskType.DEADLINE);
        deadlineDateTime = DatetimeValidator.parseToLocalDateTime(deadlineInput);
    }

    /**
     * Recreates a deadline from its ISO local date/time storage value.
     *
     * @param description The deadline description.
     * @param deadlineDateTime The stored local date/time value.
     */
    public Deadline(String description, LocalDateTime deadlineDateTime) {
        super(description, TaskType.DEADLINE);
        this.deadlineDateTime = deadlineDateTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtra() {
        return deadlineDateTime.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + DatetimeValidator.formatForUser(deadlineDateTime) + ")";
    }
}
