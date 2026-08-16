/**
 * Represents a task with a description and completion status.
 */
public class Task {
    private final String description;
    private boolean complete;
    private TaskClass taskType;

    public enum TaskClass {
        TODO("T"),
        DEADLINE("D"),
        EVENT("E");

        private final String abbrev;

        TaskClass(String abbrev) {
            this.abbrev = abbrev;
        }

        public String helper() {
            return abbrev;
        }
    }

    /**
     * Creates an incomplete task with the provided description.
     *
     * @param description text describing the task
     */
    public Task(String taskType, String description) {
        try {
            this.taskType = TaskClass.valueOf(taskType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Please enter a valid task type");
        }

        this.description = description;
        this.complete = false;
    }

    /**
     * Marks this task as complete.
     */
    public void markComplete() {
        complete = true;
    }

    public void markIncomplete() {
        complete = false;
    }

    /**
     * Returns a display form that includes the task's completion status.
     *
     * @return the task description prefixed with {@code [ ]} or {@code [X]}
     */
    @Override
    public String toString() {
        return "[" + taskType.helper() + "]"
                + (complete ? "[X] " : "[ ] ")
                + description;
    }
}