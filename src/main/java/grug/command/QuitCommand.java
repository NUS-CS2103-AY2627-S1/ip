package grug.command;

import grug.storage.TaskStorage;
import grug.task.TaskList;

/**
 * Command to quit the program.
 */
public record QuitCommand() implements GrugCommand {
    /**
     * Does nothing.
     *
     * @param tasks   The task list that is never used. Can be {@code null}.
     * @param storage The task storage that is never used. Can be {@code null}.
     * @return A {@link CommandResult.Ok} result containing the quit {@code message}
     *         and {@code shouldExit: false}.
     */
    @Override
    public CommandResult execute(TaskList tasks, TaskStorage storage) {
        boolean shouldExit = true;
        return new CommandResult.Ok("Unga. Bye. さよなら", shouldExit);
    }
}
