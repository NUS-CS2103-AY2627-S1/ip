package task;

import java.util.Arrays;

import exception.FriedbergException;
import exception.FriedbergInternalException;

/**
 * Represents the shared state and behavior of a task.
 */
public abstract class Task {
    /** Number of common storage fields: name, completion status, and task type. */
    private static final int COMMON_FIELD_COUNT = 3;

    private String name;
    private TaskStatus status;

    /**
     * Creates an in-progress task with the given name.
     *
     * @param name name of the task
     */
    public Task(String name) {
        this.name = name;
        this.status = TaskStatus.IN_PROGRESS;
    }

    /**
     * Creates a task with a known name and completion status.
     *
     * @param name task description
     * @param status completion status
     */
    public Task(String name, TaskStatus status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return this.name;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    /**
     * Converts this task's common fields into their storage representation.
     *
     * @return serialized task name and status
     */
    public String serialize() {
        // name,status
        return String.format("%s,%s", this.name, this.status.serialize());
    }

    /**
     * Recreates a task from its storage representation.
     *
     * @param taskString serialized task data
     * @return task represented by the serialized data
     * @throws FriedbergException if the task data is invalid or unsupported
     */
    public static Task deserialize(String taskString) throws FriedbergException {
        // Storage fields are name, status, type, followed by type-specific date fields.
        String[] tokens = taskString.split(",");
        if (tokens.length < COMMON_FIELD_COUNT) {
            throw new FriedbergInternalException(
                    "Expected serialized task to contain name, status, and type");
        }
        String name = tokens[0];
        TaskStatus status = TaskStatus.deserialize(tokens[1]);
        String taskType = tokens[2];
        String[] otherTokens = Arrays.copyOfRange(tokens, COMMON_FIELD_COUNT, tokens.length);
        if (taskType.equals("T")) {
            return new ToDo(name, status);
        } else if (taskType.equals("D")) {
            return new Deadline(name, status, otherTokens);
        } else if (taskType.equals("E")) {
            return new Event(name, status, otherTokens);
        } else {
            throw new FriedbergInternalException(String.format("Unknown taskType: %s", taskType));
        }
    }

    /**
     * Marks the Task as done
     */
    public void mark() {
        this.status = TaskStatus.DONE;
    }

    /**
     * Unmarks the Task
     */
    public void unmark() {
        this.status = TaskStatus.IN_PROGRESS;
    }

    /**
     * Returns the task description and completion status for either frontend.
     *
     * @return formatted task text
     */
    public String renderTask() {
        return String.format("%s %s", this.status.renderTaskStatus(), this.name);
    }
}
