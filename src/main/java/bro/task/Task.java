package bro.task;

import java.time.LocalDate;

/**
 * Abstract representation of a task in the Bro application.
 */
public abstract class Task {
    private String description;
    private boolean isDone;

    /**
     * Constructs a new Task with the specified description, initially not done.
     *
     * @param description The description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Constructs a new Task with the specified description and completion status.
     *
     * @param description The description of the task.
     * @param isDone      The initial completion status.
     */
    public Task(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    public void markDone() {
        this.isDone = true;
    }

    public void unmarkDone() {
        this.isDone = false;
    }

    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Checks whether this task occurs on or involves the given date.
     * Default implementation returns false. Subclasses should override where
     * appropriate.
     *
     * @param date The LocalDate to check against.
     * @return True if the task involves the given date, false otherwise.
     */
    public boolean isOnDate(LocalDate date) {
        return false;
    }

    /**
     * Checks whether this task contains any of the string keywords or phrases (case-insensitive).
     *
     * @param keywords One or more keywords or keyword phrases to search for.
     * @return True if the task contains at least one keyword, false otherwise.
     */
    public boolean containsKeywords(String... keywords) {
        if (keywords == null || keywords.length == 0) {
            return false;
        }
        String lowerDescription = this.description.toLowerCase();
        for (String keyword : keywords) {
            if (keyword != null && !keyword.trim().isEmpty()
                    && lowerDescription.contains(keyword.trim().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the description of the task.
     *
     * @return The task description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the description of the task (alias for {@link #getDescription()}).
     *
     * @return The task description.
     */
    public String getTask() {
        return getDescription();
    }

    /**
     * Sets the description of the task.
     *
     * @param description The new description of the task.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Formats the common file record prefix containing the task type code,
     * completion status flag, and description.
     *
     * @param typeTag The 1-letter task type code (e.g., "T", "D", "E").
     * @return The formatted file prefix string.
     */
    protected String formatFilePrefix(String typeTag) {
        return typeTag + " | " + (this.isDone ? "1" : "0") + " | " + this.description;
    }

    @Override
    public String toString() {
        if (this.isDone) {
            return String.format("[X] %s", this.description);
        } else {
            return String.format("[ ] %s", this.description);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Task other = (Task) obj;
        return this.description != null && this.description.trim().equalsIgnoreCase(
                other.description == null ? null : other.description.trim());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(getClass(),
                description == null ? null : description.trim().toLowerCase());
    }

    /**
     * Formats the task into a pipe-delimited string for text file storage.
     *
     * @return The formatted string representation for storage.
     */
    public abstract String toFileFormat();
}
