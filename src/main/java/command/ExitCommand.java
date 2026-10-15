package command;

import task.TaskList;

/** Tells the application to end its command loop. */
public class ExitCommand implements Command {
    /**
     * Produces a result that tells the application to exit.
     *
     * @param tasks current task list; not modified.
     * @return result containing the farewell message and exit flag.
     */
    @Override
    public CommandResult execute(TaskList tasks) {
        return new CommandResult("Mission paused. Safe travels!", false, true);
    }
}
