package bucket;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Base class for every kind of task - todo, deadline and event.
 * Holds the description and done state that all tasks share.
 */
public abstract class Task {
    private boolean isDone;
    private String name;

    /**
     * Creates a task that starts off not done.
     *
     * @param name Description the user typed.
     */
    public Task(String name) {
        // Parser hands back "" rather than null when nothing follows the command word,
        // so a null name means a caller built a task some other way. toString and
        // toSaveString both format this field without checking it first.
        assert name != null : "a task needs a description, empty at worst, never null";

        this.isDone = false;
        this.name = name;
    }

    /**
     * Returns the mark shown inside the second pair of brackets.
     *
     * @return "X" if done, a single space if not.
     */
    public String getDoneIcon() {
        return isDone ? "X" : " ";
    }

    public String getName() {
        return name;
    }

    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * Returns the letter shown inside the first pair of brackets.
     * Each subclass answers for itself.
     *
     * @return "T", "D" or "E".
     */
    public abstract String getTypeIcon();

    /**
     * Returns the date this task should be ordered by, or empty if it has none.
     * Each kind of task decides for itself which of its dates matters, so sorting
     * never has to ask what type a task is.
     *
     * @return Date to sort on, or empty for a task with no date.
     */
    public abstract Optional<LocalDate> getSortDate();

    /**
     * Returns one line for the save file, like "T | 1 | read book".
     * Subclasses append their own extra fields onto this.
     *
     * @return Save-file form of this task.
     */
    public String toSaveString() {
        // Storage.parse splits saved lines on " | ", so a description containing that
        // separator would be read back as a different task, or dropped as corrupted.
        assert !this.name.contains(" | ") : "a description must not contain the save separator";

        return String.format("%s | %d | %s", getTypeIcon(), isDone ? 1 : 0, this.name);
    }

    /**
     * Returns the form shown to the user, like "[T][X] read book".
     *
     * @return Display form of this task.
     */
    @Override
    public String toString() {
        return String.format("[%s][%s] %s", getTypeIcon(), getDoneIcon(), this.name);
    }
}
