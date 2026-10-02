package floppy;

/**
 * Carries a parsed command and the task data needed to execute it.
 */
public class Command {
    private static final int NO_TASK_INDEX = -1;

    private final CommandType commandType;
    private final int taskIndex;
    private final Task task;
    private final String keyword;

    /**
     * Creates a command that has no task argument.
     *
     * @param commandType Operation represented by the command.
     */
    public Command(CommandType commandType) {
        this(commandType, NO_TASK_INDEX, null, null);
    }

    /**
     * Creates a command that targets a task by its zero-based index.
     *
     * @param commandType Operation represented by the command.
     * @param taskIndex Zero-based task index supplied by the user.
     */
    public Command(CommandType commandType, int taskIndex) {
        this(commandType, taskIndex, null, null);
    }

    /**
     * Creates a command that adds a new task.
     *
     * @param task Task to add.
     */
    public Command(Task task) {
        this(CommandType.ADD, NO_TASK_INDEX, task, null);
    }

    /**
     * Creates a command that searches task descriptions for a keyword.
     *
     * @param keyword Keyword to find.
     */
    public Command(String keyword) {
        this(CommandType.FIND, NO_TASK_INDEX, null, keyword);
    }

    private Command(CommandType commandType, int taskIndex, Task task, String keyword) {
        this.commandType = commandType;
        this.taskIndex = taskIndex;
        this.task = task;
        this.keyword = keyword;
    }

    /**
     * Returns the operation represented by this command.
     *
     * @return Parsed command type.
     */
    public CommandType getCommandType() {
        return commandType;
    }

    /**
     * Returns the zero-based task index for an indexed command.
     *
     * @return Target task index.
     */
    public int getTaskIndex() {
        return taskIndex;
    }

    /**
     * Returns the task carried by an add command.
     *
     * @return Task to add.
     */
    public Task getTask() {
        return task;
    }

    /**
     * Returns the keyword carried by a find command.
     *
     * @return Search keyword.
     */
    public String getKeyword() {
        return keyword;
    }
}
