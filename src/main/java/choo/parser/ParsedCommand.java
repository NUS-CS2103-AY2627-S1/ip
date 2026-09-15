package choo.parser;

import choo.task.Task;

/**
 * Carries a parsed command and the data needed to execute it.
 */
public class ParsedCommand {
    private final CommandType type;
    private final int taskNumber;
    private final Task task;
    private final String keyword;

    private ParsedCommand(CommandType type, int taskNumber, Task task, String keyword) {
        this.type = type;
        this.taskNumber = taskNumber;
        this.task = task;
        this.keyword = keyword;
    }

    /**
     * Creates a command that has no additional data.
     *
     * @param type command type.
     * @return parsed command.
     */
    public static ParsedCommand withoutDetails(CommandType type) {
        return new ParsedCommand(type, 0, null, null);
    }

    /**
     * Creates a command that targets a task number.
     *
     * @param type command type.
     * @param taskNumber one-based task number.
     * @return parsed command.
     */
    public static ParsedCommand forTaskNumber(CommandType type, int taskNumber) {
        return new ParsedCommand(type, taskNumber, null, null);
    }

    /**
     * Creates a command that adds a task.
     *
     * @param task task to add.
     * @return parsed command.
     */
    public static ParsedCommand forNewTask(Task task) {
        return new ParsedCommand(CommandType.ADD, 0, task, null);
    }

    /**
     * Creates a command that searches task descriptions.
     *
     * @param keyword Keyword to search for.
     * @return Parsed find command.
     */
    public static ParsedCommand forKeyword(String keyword) {
        return new ParsedCommand(CommandType.FIND, 0, null, keyword);
    }

    /**
     * Returns the command type.
     *
     * @return command type.
     */
    public CommandType getType() {
        return this.type;
    }

    /**
     * Returns the one-based task number for a task-targeting command.
     *
     * @return one-based task number.
     */
    public int getTaskNumber() {
        return this.taskNumber;
    }

    /**
     * Returns the task carried by an add command.
     *
     * @return task to add.
     */
    public Task getTask() {
        return this.task;
    }

    /**
     * Returns the keyword carried by a find command.
     *
     * @return Search keyword.
     */
    public String getKeyword() {
        return this.keyword;
    }
}
