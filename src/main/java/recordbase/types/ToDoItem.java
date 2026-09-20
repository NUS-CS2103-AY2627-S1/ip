package recordbase.types;

import java.time.LocalDateTime;

import recordbase.utils.TimeDisplay;

/**
 * Represents a task without a specified deadline or duration.
 */
public class ToDoItem extends ListItem {
    private final LocalDateTime scheduledDate;
    /**
     * Constructs a {@code ToDoItem} with the specified task description.
     *
     * @param taskDesc the description of the task
     */
    public ToDoItem(String taskDesc) {
        this(taskDesc, Priority.MEDIUM);
    }

    /**
     * Constructs a {@code ToDoItem} with the specified description and priority.
     *
     * @param taskDesc the description of the task
     * @param priority the priority of the task
     */
    public ToDoItem(String taskDesc, Priority priority) {
        this(taskDesc, null, priority);
    }

    /**
     * Constructs a to-do with an optional scheduled date and time.
     *
     * @param taskDesc the description of the task
     * @param scheduledDate when the task is planned, or {@code null} if it is unscheduled
     * @param priority the priority of the task
     */
    public ToDoItem(String taskDesc, LocalDateTime scheduledDate, Priority priority) {
        super(taskDesc, priority);
        this.scheduledDate = scheduledDate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[T] ");

        sb.append(super.toString());

        if (scheduledDate != null) {
            sb.append(String.format(" (Scheduled: %s)", TimeDisplay.formatDateTime(scheduledDate)));
        }

        return sb.toString();
    }

    @Override
    public String saveString() {
        String basicFields = String.format("T,%s,%d,%s", this.isDone ? "1" : "0",
                priority.getLevel(), toCsvField(this.taskDesc));
        return scheduledDate == null ? basicFields : basicFields + "," + scheduledDate;
    }
}
