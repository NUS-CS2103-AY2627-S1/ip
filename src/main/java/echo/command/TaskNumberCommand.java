package echo.command;

import java.util.List;

/**
 * Base class for commands that operate on one task number.
 */
public abstract class TaskNumberCommand extends Command {
    /**
     * Creates a task-number command.
     *
     * @param type Command type.
     * @param taskNumber One-based task number.
     */
    protected TaskNumberCommand(Type type, int taskNumber) {
        super(type, List.of(String.valueOf(taskNumber)));
        assert taskNumber >= 1 : "Task-number commands must use one-based positive numbers";
    }

    /**
     * Returns the task number supplied to this command.
     *
     * @return One-based task number.
     */
    protected int getTaskNumber() {
        return Integer.parseInt(getArgument(0));
    }
}
