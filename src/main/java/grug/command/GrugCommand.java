package grug.command;

import grug.storage.TaskStorage;
import grug.task.TaskList;

/**
 * Sum Type that represents the user's command.
 */
public sealed interface GrugCommand permits
        EmptyCommand,
        QuitCommand,
        ListTasksCommand,
        AddTaskCommand,
        MarkTaskCommand,
        UnmarkTaskCommand,
        DeleteTaskCommand,
        FindTasksByDateCommand,
        FindTasksByDetailsCommand,
        SetPriorityCommand {
    /**
     * Executes the command, modifying the {@link TaskList} or using the
     * {@link TaskStorage} if necessary.
     *
     * @param tasks   The task list that the command may modify.
     * @param storage The task storage that the command may need to save data.
     * @return The result of the execution. Either {@code Ok}, {@code Partial} (when
     *         the primary execution is a success, but some secondary execution
     *         failed, e.g. deleted task successfully, but failed to save to disk)
     *         or {@code Err}.
     */
    public abstract CommandResult execute(TaskList tasks, TaskStorage storage);
}
