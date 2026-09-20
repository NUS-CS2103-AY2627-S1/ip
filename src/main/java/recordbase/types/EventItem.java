package recordbase.types;

import java.time.LocalDateTime;

import recordbase.utils.TimeDisplay;

/**
 * Represents a task that occurs over a specified period of time.
 *
 * <p>A {@code EventItem} stores the task description together with its start and end date and time.</p>
 */
public class EventItem extends ListItem {
    private final LocalDateTime fromDate;
    private final LocalDateTime toDate;

    /**
     * Constructs a {@code EventItem} with the specified task description, start date and time, end date and time.
     *
     * @param taskDesc the description of the task
     * @param fromDate the date and time when the event starts
     * @param toDate the date and time when the event ends
     */
    public EventItem(String taskDesc, LocalDateTime fromDate, LocalDateTime toDate) {
        this(taskDesc, fromDate, toDate, Priority.MEDIUM);
    }

    /**
     * Constructs an event task with the specified description, duration, and priority.
     *
     * @param taskDesc the description of the task
     * @param fromDate the date and time when the event starts
     * @param toDate the date and time when the event ends
     * @param priority the priority of the task
     */
    public EventItem(String taskDesc, LocalDateTime fromDate, LocalDateTime toDate, Priority priority) {
        super(taskDesc, priority);
        assert fromDate != null : "Event start must not be null";
        assert toDate != null : "Event end must not be null";
        assert !toDate.isBefore(fromDate) : "Event end must not be before its start";

        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[E] ");

        sb.append(super.toString());

        String durationDescription = String.format(" (From: %s | To: %s | Duration: %s)",
                TimeDisplay.formatDateTime(this.fromDate), TimeDisplay.formatDateTime(this.toDate),
                TimeDisplay.formatDuration(this.fromDate, this.toDate));
        sb.append(durationDescription);
        return sb.toString();
    }

    @Override
    public String saveString() {
        return String.format("E,%s,%d,%s,%s,%s", this.isDone ? "1" : "0",
                priority.getLevel(), toCsvField(this.taskDesc), this.fromDate, this.toDate);
    }
}
