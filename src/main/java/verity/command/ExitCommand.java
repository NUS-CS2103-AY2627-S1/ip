package verity.command;

import verity.storage.Storage;
import verity.task.TaskList;

/**
 * Ends the program, after printing the farewell message.
 */
public class ExitCommand extends Command {

    /**
     * Returns the farewell message shown when the user exits.
     *
     * @param tasks {@inheritDoc}
     * @param storage {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String execute(TaskList tasks, Storage storage) {
        return "Finally. Try not to forget anything important. Bye! ;)";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
