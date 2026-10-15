package chocolate.parser;

import chocolate.task.Task;

/**
 * Holds the structured information extracted from one user command.
 */
public class ParsedCommand {
    private final CommandType type;
    private final Task task;
    private final int taskIndex;
    private final String keyword;

    private ParsedCommand(CommandType type, Task task, int taskIndex, String keyword) {
        this.type = type;
        this.task = task;
        this.taskIndex = taskIndex;
        this.keyword = keyword;
    }

    /**
     * Creates a command that does not require a task or index.
     *
     * @param type Type of command.
     * @return Parsed command containing only its type.
     */
    public static ParsedCommand createSimple(CommandType type) {
        return new ParsedCommand(type, null, -1, null);
    }

    /**
     * Creates a command containing a newly parsed task.
     *
     * @param type Type of add command.
     * @param task Task to add.
     * @return Parsed command containing the task.
     */
    public static ParsedCommand createWithTask(CommandType type, Task task) {
        return new ParsedCommand(type, task, -1, null);
    }

    /**
     * Creates a command referring to a zero-based task index.
     *
     * @param type Type of indexed command.
     * @param taskIndex Zero-based index of the target task.
     * @return Parsed command containing the task index.
     */
    public static ParsedCommand createWithIndex(CommandType type, int taskIndex) {
        return new ParsedCommand(type, null, taskIndex, null);
    }

    /**
     * Creates a command containing a search keyword.
     *
     * @param type Type of search command.
     * @param keyword Keyword to find in task descriptions.
     * @return Parsed command containing the keyword.
     */
    public static ParsedCommand createWithKeyword(CommandType type, String keyword) {
        return new ParsedCommand(type, null, -1, keyword);
    }

    /**
     * Returns the command type.
     *
     * @return Command type.
     */
    public CommandType getType() {
        return type;
    }

    /**
     * Returns the task carried by an add command.
     *
     * @return Parsed task, or null when the command has no task.
     */
    public Task getTask() {
        return task;
    }

    /**
     * Returns the zero-based target task index.
     *
     * @return Task index, or -1 when the command has no index.
     */
    public int getTaskIndex() {
        return taskIndex;
    }

    /**
     * Returns the keyword carried by a search command.
     *
     * @return Search keyword, or null when the command has no keyword.
     */
    public String getKeyword() {
        return keyword;
    }
}
