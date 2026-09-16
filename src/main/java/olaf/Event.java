package olaf;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Represents a task that takes place between a given start and end.
 */
final class Event extends Task {
    /** Identifies an event task in storage and displayed responses. */
    static final String TYPE_CODE = "E";

    /** Explains the date-order requirement shared by creation and rescheduling. */
    static final String INVALID_DATE_RANGE_MESSAGE =
            "The event end date must be on or after its start date.";

    private LocalDate startDate;
    private LocalDate endDate;

    Event(String description, LocalDate startDate, LocalDate endDate) {
        super(description);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    private Event(Event original) {
        super(original);
        startDate = original.startDate;
        endDate = original.endDate;
    }

    /** {@inheritDoc} */
    @Override
    Task copy() {
        return new Event(this);
    }

    /**
     * Replaces both event dates after validating the complete date range.
     *
     * @param startDate replacement start date
     * @param endDate replacement end date, which may equal the start date
     * @throws TaskRescheduleException if the end date is before the start date
     * @throws NullPointerException if either date is null
     */
    void reschedule(LocalDate startDate, LocalDate endDate) throws TaskRescheduleException {
        Objects.requireNonNull(startDate, "startDate");
        Objects.requireNonNull(endDate, "endDate");
        if (endDate.isBefore(startDate)) {
            throw new TaskRescheduleException(INVALID_DATE_RANGE_MESSAGE);
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** {@inheritDoc} */
    @Override
    String getTypeCode() {
        return TYPE_CODE;
    }

    /** {@inheritDoc} */
    @Override
    List<String> getAdditionalStorageFields() {
        return List.of(startDate.toString(), endDate.toString());
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return super.toString() + " (from: " + TaskDateFormat.format(startDate)
                + " to: " + TaskDateFormat.format(endDate) + ")";
    }
}
