package yapatron.task;

/**
 * Represents a task with only a description
 */

public class Todo extends Task {

    /**
     * Creates a Todo task instance with a description
     *
     * @param desc Details of task
     */
    public Todo(String desc) {
        super(desc);
    }

    /** 
     * Returns the string representation of the task
     *
     * @return Formatted string with status icon and task description
     */
    @Override
        public String toString() {
            return "[T]" + super.toString();
        }

    /**
     * Formats the task data into a pipe-delimted string for file storage
     *
     * @return Formatted string for file storage
     */
    @Override
        public String toFileFormat() {
            return "[TASK] | " + super.toFileFormat();
        }
}
