package auto.command;

import auto.storage.Storage;
import auto.task.TaskList;

/** Ends the current Auto session. */
public class ExitCommand extends Command {
    /** {@inheritDoc} */
    @Override
    public CommandResult execute(TaskList tasks, Storage storage) {
        return new CommandResult("Okay, bye lah! Go take a kopi break.");
    }
}
