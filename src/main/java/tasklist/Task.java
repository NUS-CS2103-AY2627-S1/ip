package tasklist;

/**
 * Represents a basic task with a description and completion status.
 */
public class Task {
    /**
     * The category of task: a plain to-do, a deadline, or an event.
     */
    public enum TaskType {
        TODO, DEADLINE, EVENT
    };

    protected TaskType category;
    protected String objective;
    protected boolean done;
    protected String place;

    /**
     * Constructs a {@code Task} of type TODO with no place.
     *
     * @param s the task description.
     */
    public Task(String s) {
        this(s, null);
    }

    /**
     * Constructs a {@code Task} of type TODO with an optional place to complete it.
     *
     * @param s the task description.
     * @param place the place to go to complete the task, or {@code null}/blank if none.
     */
    public Task(String s, String place) {
        this.category = TaskType.TODO;
        this.objective = s;
        this.done = false;
        this.place = place;
    }

    /**
     * Returns the string representation of this task for display.
     *
     * @return the formatted display string.
     */
    @Override
    public String toString() {
        return String.format("[T] [%s] %s%s",
                this.done ? "X" : " ",
                this.objective,
                hasPlace() ? " (at: " + this.place + ")" : "");
    }

    /**
     * Returns the string representation of this task for file storage.
     * Place field is always written (empty string if unset) to keep column count fixed.
     *
     * @return the formatted save-file string.
     */
    public String fileFormatText() {
        String base = String.format("T | %s", this.objective);
        return hasPlace() ? base + " | " + this.place : base;
    }

    /**
     * Marks this task as done.
     */
    public void setDone() {
        this.done = true;
    }

    /**
     * Marks this task as not done.
     */
    public void setUndone() {
        this.done = false;
    }

    /**
     * Returns the category of this task.
     *
     * @return the task's {@code TaskType}.
     */
    public TaskType getCategory() {
        return this.category;
    }

    /**
     * Returns the place associated with this task.
     *
     * @return the place, or {@code null} if none set.
     */
    public String getPlace() {
        return this.place;
    }

    /**
     * Sets the place associated with this task.
     *
     * @param place the place to go to complete the task.
     */
    public void setPlace(String place) {
        this.place = place;
    }

    /**
     * Checks whether this task has a non-blank associated place.
     *
     * @return {@code true} if a place is set, {@code false} otherwise.
     */
    public boolean hasPlace() {
        return this.place != null && !this.place.isBlank();
    }

    /**
     * Checks whether this task's description contains the given search string.
     *
     * @param s the search string.
     * @return {@code true} if the description contains {@code s}, {@code false} otherwise.
     */
    public boolean contains(String s) {
        return objective.contains(s);
    }
}
