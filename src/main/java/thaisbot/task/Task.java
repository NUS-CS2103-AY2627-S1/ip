package thaisbot.task;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Base class for tasks stored in the application. Subclasses represent specific task types
 * such as todos, deadlines and events.
 */
public class Task {
    private final String description;
    private TaskStatus status;
    private final LinkedHashSet<String> tags;

    /**
     * Create a new Task with the given description. Status defaults to NOT_DONE.
     * @param description task description
     */
    public Task(String description) {
        this.description = description;
        this.status = TaskStatus.NOT_DONE;
        this.tags = new LinkedHashSet<>();
    }

    /**
     * Mark this task as done.
     */
    public void markAsDone() {
        this.status = TaskStatus.DONE;
    }

    /**
     * Mark this task as not done.
     */
    public void unmarkAsDone() {
        this.status = TaskStatus.NOT_DONE;
    }

    /**
     * Return the status icon (e.g., X for done).
     * @return single-character status icon
     */
    public String getStatusIcon() {
        return status.getIcon();
    }

    /**
     * Set the task status explicitly.
     * @param status new status
     */
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    /**
     * Adds one or more tags to the task.
     * @param newTags tags to add
     */
    public void addTags(Collection<String> newTags) {
        assert newTags != null;
        tags.addAll(newTags);
    }

    /**
     * Returns the task's tags in insertion order.
     * @return task tags
     */
    public List<String> getTags() {
        return List.copyOf(tags);
    }

    /**
     * Convert the task to the pipe-separated format used for storage.
     * @return storage string
     */
    public String toFileString() {
        String doneFlag = status == TaskStatus.DONE ? "1" : "0";
        return "T | " + doneFlag + " | " + description;
    }

    /**
     * Returns true if this task occurs on the given date. Default implementation is false;
     * subclasses override it for date-aware tasks.
     * @param date date to test
     * @return true if the task occurs on date
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description + formatTags();
    }

    /**
     * Returns the raw description string for text searches.
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns true if the task is marked done.
     * @return true if done
     */
    public boolean isDone() {
        return this.status == TaskStatus.DONE;
    }

    /**
     * Returns true if the task description or any tag contains the query string.
     * @param query search text
     * @return true if the task matches the query
     */
    public boolean matchesSearch(String query) {
        assert query != null;
        return description.contains(query)
                || tags.stream().anyMatch(tag -> tag.contains(query) || ("#" + tag).contains(query));
    }

    /**
     * Formats the task tags for display.
     * @return formatted tag text
     */
    protected String formatTags() {
        if (tags.isEmpty()) {
            return "";
        }
        return " " + tags.stream()
                .map(tag -> "#" + tag)
                .collect(Collectors.joining(" "));
    }

    /**
     * Formats the task tags for storage.
     * @return formatted storage tag fields
     */
    protected String formatTagsForStorage() {
        return tags.stream()
                .map(tag -> " | #" + tag)
                .collect(Collectors.joining());
    }
}
