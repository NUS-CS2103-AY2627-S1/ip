package sophon.logic;

import sophon.model.Task;

/**
 * Represents a user command after it has been parsed.
 */
public class Command {
    /**
     * Categories of commands that Sophon can execute.
     */
    public enum Type {
        BYE,
        LIST,
        FIND,
        ADD_TODO,
        ADD_DEADLINE,
        ADD_EVENT,
        MARK,
        UNMARK,
        DELETE,
        UNKNOWN
    }

    private final Type type;
    private final Task task;
    private final int taskIndex;
    private final String keyword;

    /**
     * Creates a command without extra data.
     *
     * @param type category of command.
     */
    public Command(Type type) {
        this(type, null, -1, null);
    }

    /**
     * Creates a command that contains a task.
     *
     * @param type category of command.
     * @param task task carried by the command.
     */
    public Command(Type type, Task task) {
        this(type, task, -1, null);
    }

    /**
     * Creates a command that refers to an existing task.
     *
     * @param type category of command.
     * @param taskIndex zero-based index of the task.
     */
    public Command(Type type, int taskIndex) {
        this(type, null, taskIndex, null);
    }

    /**
     * Creates a command that carries a keyword.
     *
     * @param type category of command
     * @param keyword keyword carried by the command
     */
    public Command(Type type, String keyword) {
        this(type, null, -1, keyword);
    }

    private Command(Type type, Task task, int taskIndex, String keyword) {
        assert type != null : "Command type must not be null";
        assert type != Type.FIND || keyword != null
                : "Find command must contain a keyword";
        assert type != Type.ADD_TODO
                && type != Type.ADD_DEADLINE
                && type != Type.ADD_EVENT
                || task != null
                : "Add command must contain a task";

        this.type = type;
        this.task = task;
        this.taskIndex = taskIndex;
        this.keyword = keyword;
    }

    public Type getType() {
        return type;
    }

    public Task getTask() {
        return task;
    }

    public int getTaskIndex() {
        return taskIndex;
    }

    public String getKeyword() {
        return keyword;
    }
}
