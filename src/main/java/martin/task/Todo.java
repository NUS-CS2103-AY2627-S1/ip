package martin.task;

import martin.exception.IllegalCommandException;
import martin.util.StringParserUtil;

/**
 * Represents a task without a date or time associated with it.
 */
public class Todo extends Task {

    /**
     * Constructs a {@code Todo} task with the specified description.
     * Priority defaults to {@code LOW}.
     *
     * @param description The description of the task.
     */
    public Todo(String description) {
        this(description, Priority.LOW);
    }

    /**
     * Constructs a {@code Todo} task with the specified description and priority.
     *
     * @param description The description of the task.
     * @param priority    The priority of the task.
     */
    public Todo(String description, Priority priority) {
        super(description, TaskType.TODO, priority);
    }

    /**
     * Returns this todo with its completion status and description.
     *
     * @return The display representation of this todo.
     */
    @Override
    public String toString() {
        return String.format("[%s]%s", this.getTaskType().getStorageCode(), super.toString());
    }

    /**
     * Creates a {@code Todo} task from a command input string.
     *
     * @param input The command string entered by the user.
     * @return The created {@code Todo} task.
     * @throws IllegalCommandException If the description is missing.
     */
    public static Todo parseTodoFromInputString(String input) {
        BaseTaskDetails taskDetails = parsePriority(input.substring("todo".length()).trim());
        String description = StringParserUtil.requireValue(taskDetails.details(),
                "A todo needs a non-empty description.");
        return new Todo(description, taskDetails.priority());
    }

    /**
     * Returns this todo in the persistent storage format.
     *
     * @return The storage representation of this todo.
     */
    @Override
    public String toDataFormat() {
        return String.format("%s | %d | %s | %s", this.getTaskType().getStorageCode(), this.isDone ? 1 : 0,
                this.description, this.getPriority().getStorageCode());
    }
}
