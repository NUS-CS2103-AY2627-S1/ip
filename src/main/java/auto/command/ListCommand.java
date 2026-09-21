package auto.command;

import auto.storage.Storage;
import auto.task.TaskList;

/** Displays every task in the list. */
public class ListCommand extends Command {
    /** {@inheritDoc} */
    @Override
    public CommandResult execute(TaskList tasks, Storage storage) {
        return new CommandResult(formatTasks(
                "Here's what you have on your plate:", tasks.asList(), task -> true));
    }
}
