package rice;
import java.time.LocalDate;

/**
 * Represents a task with no date attached.
 */
public class Todo extends Task {

    /**
     * Creates a todo task from its description.
     *
     * @param task the task description
     */
    public Todo(String task) {
        super(task);
    }

    /**
     * Returns a sentinel value so todos are sorted last when ordering tasks.
     *
     * @return the greatest possible LocalDate value
     */
    @Override
    public LocalDate getDeadlineDate() {
        return LocalDate.MAX;
    }

    /**
     * Returns the formatted display string for the todo task.
     *
     * @return the task in todo format
     */
    @Override
    public String toString() {
        return "[T]" + super.completionMarker() + " " + super.getTask();
    }
}
