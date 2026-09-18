package hermes.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** One thing the user wants to keep track of. */
public abstract class Task implements Comparable<Task> {

    /**
     * How a date is shown to the user.
     *
     * <p>Written in the same numeric form the user is asked to type, so a date
     * read off the list can be typed straight back in. A month written as a
     * name would depend on the locale of the machine: on one, September reads
     * "Sept" and "Sep" is refused, and elsewhere the reverse.
     */
    protected static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HHmm");

    protected String taskDescription;
    protected boolean isCompleted = false;

    /** Creates a task that is not yet completed. */
    public Task(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    /** Creates a task in a known state, used when loading from storage. */
    public Task(boolean isCompleted, String taskDescription) {
        this.isCompleted = isCompleted;
        this.taskDescription = taskDescription;
    }

    /** Marks the task as completed and returns the reply to show the user. */
    public boolean mark() {
        if (isCompleted) {
            return false;
        }

        this.isCompleted = true;
        return true;
    }

    /** Marks the task as not completed and returns the reply to show the user. */
    public boolean unmark() {
        if (!this.isCompleted) {
            return false;
        }

        this.isCompleted = false;
        return true;
    }

    /** Returns true if this task has been completed. */
    public boolean isCompleted() {
        return this.isCompleted;
    }

    /**
     * Returns true if the other task records the same errand as this one.
     *
     * <p>Two tasks are alike when they are of the same kind and hold the same
     * description and the same dates. Whether either is completed is left out:
     * an unfinished copy of a task already done is still the same errand.
     *
     * @param other the task to compare against.
     * @return true if both describe the same errand.
     */
    public boolean hasSameDetails(Task other) {
        return detailsOf(this).equals(detailsOf(other));
    }

    /**
     * Returns a task's stored line without its completion flag.
     *
     * <p>The stored line already holds the kind, description and dates, so
     * comparing it cannot miss a field the way naming each one would.
     */
    private static String detailsOf(Task task) {
        String[] fields = task.getFileContent().split("\\|", -1);
        fields[1] = "";
        return String.join("|", fields);
    }

    /** Returns the text the user gave to describe this task. */
    public String getTaskDescription() {
        return this.taskDescription;
    }

    /** Returns this task as one line of the data file. */
    public abstract String getFileContent();

    /** Returns the moment this task is measured against, or null if it has none. */
    public abstract LocalDateTime getDueDateTime();

    /** Returns an independent copy of this task. */
    public abstract Task copy();

    /** Returns true if this task has a date falling no later than the given moment. */
    public boolean isDueBy(LocalDateTime deadline) {
        LocalDateTime dueDateTime = getDueDateTime();
        return dueDateTime != null && !dueDateTime.isAfter(deadline);
    }

    enum TaskOrder {
        ACTIVE_DATED,
        ACTIVE_UNDATED,
        COMPLETED
    }

    /** Groups tasks for sorting; the constants are declared in the order they sort. */
    private TaskOrder getSortRank() {
        if (this.isCompleted) {
            return TaskOrder.COMPLETED;
        }
        return this.getDueDateTime() == null ? TaskOrder.ACTIVE_UNDATED : TaskOrder.ACTIVE_DATED;
    }

    @Override
    public int compareTo(Task otherTask) {
        TaskOrder thisSortRank = getSortRank();
        TaskOrder otherSortRank = otherTask.getSortRank();
        int rankDifference = thisSortRank.compareTo(otherSortRank);

        if (rankDifference != 0) {
            return rankDifference;
        }

        // Same rank, so only dated active tasks have anything left to separate them.
        if (this.getSortRank() == TaskOrder.ACTIVE_DATED) {
            assert this.getDueDateTime() != null && otherTask.getDueDateTime() != null
                    : "Rank 0 means dated, so neither due date can be null";
            return this.getDueDateTime().compareTo(otherTask.getDueDateTime());
        }

        return 0;
    }

    @Override
    public String toString() {
        return String.format(
                "[%c] %s",
                isCompleted ? 'X' : ' ',
                this.taskDescription);
    }
}
