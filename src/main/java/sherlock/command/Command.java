package sherlock.command;

import sherlock.task.Task;

/**
 * Represents a validated action that Sherlock can perform.
 */
public class Command {
    /** Identifies the action represented by a command. */
    public enum Type {
        BYE,
        HELP,
        LIST,
        FIND,
        MARK,
        UNMARK,
        DELETE,
        ADD
    }

    private final Type type;
    private final int taskNumber;
    private final Task task;
    private final String keyword;

    private Command(Type type, int taskNumber, Task task, String keyword) {
        this.type = type;
        this.taskNumber = taskNumber;
        this.task = task;
        this.keyword = keyword;
    }

    /**
     * Creates a command that has no task-specific argument.
     *
     * @param type action to perform
     * @return command for the action
     */
    public static Command of(Type type) {
        assert type == Type.BYE || type == Type.HELP || type == Type.LIST
                : "Only commands without arguments can be created without a payload";
        return new Command(type, 0, null, null);
    }

    /**
     * Creates a command that refers to an existing one-based task number.
     *
     * @param type action to perform
     * @param taskNumber one-based number of the target task
     * @return command for the action and task number
     */
    public static Command withTaskNumber(Type type, int taskNumber) {
        assert type == Type.MARK || type == Type.UNMARK || type == Type.DELETE
                : "A task number can only be attached to an indexed task command";
        assert taskNumber > 0 : "Task numbers must be one-based";
        return new Command(type, taskNumber, null, null);
    }

    /**
     * Creates an add command for a newly created task.
     *
     * @param task task to add
     * @return add command for the task
     */
    public static Command withTask(Task task) {
        assert task != null : "An add command must contain a task";
        return new Command(Type.ADD, 0, task, null);
    }

    /**
     * Creates a find command that searches task descriptions for a keyword.
     *
     * @param keyword text to find in task descriptions
     * @return find command for the keyword
     */
    public static Command withKeyword(String keyword) {
        assert keyword != null && !keyword.isBlank() : "A find command must contain a keyword";
        return new Command(Type.FIND, 0, null, keyword);
    }

    /**
     * Returns the action represented by this command.
     *
     * @return command action
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the command's one-based task number, when it has one.
     *
     * @return referenced task number
     */
    public int getTaskNumber() {
        return taskNumber;
    }

    /**
     * Returns the task carried by an add command, when it has one.
     *
     * @return task to add
     */
    public Task getTask() {
        return task;
    }

    /**
     * Returns the keyword carried by a find command, when it has one.
     *
     * @return task-description keyword
     */
    public String getKeyword() {
        return keyword;
    }
}
