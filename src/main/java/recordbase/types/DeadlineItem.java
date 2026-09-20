package recordbase.types;

import java.time.LocalDateTime;

import recordbase.utils.TimeDisplay;

/**
 * Represents a task with a specified deadline.
 *
 * <p>A {@code DeadlineItem} stores the task description and the date and time by which the task
 * should be completed.</p>
 */
public class DeadlineItem extends ListItem {
    private final LocalDateTime byDate;

    /**
     * Constructs a {@code DeadlineItem} with the specified task description and deadline.
     *
     * @param taskDesc the description of the task
     * @param byDate the date and time which the task should be completed
     */
    public DeadlineItem(String taskDesc, LocalDateTime byDate) {
        this(taskDesc, byDate, Priority.MEDIUM);
    }

    /**
     * Constructs a deadline task with the specified description, deadline, and priority.
     *
     * @param taskDesc the description of the task
     * @param byDate the date and time by which the task should be completed
     * @param priority the priority of the task
     */
    public DeadlineItem(String taskDesc, LocalDateTime byDate, Priority priority) {
        super(taskDesc, priority);
        assert byDate != null : "Deadline must not be null";

        this.byDate = byDate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[D] ");

        sb.append(super.toString());

        String deadlineDescription = String.format(" (Due: %s | %s)",
                TimeDisplay.formatDateTime(this.byDate), TimeDisplay.formatDeadlineStatus(this.byDate));
        sb.append(deadlineDescription);
        return sb.toString();
    }

    @Override
    public String saveString() {
        return String.format("D,%s,%d,%s,%s", this.isDone ? "1" : "0",
                priority.getLevel(), toCsvField(this.taskDesc), this.byDate);
    }
}
