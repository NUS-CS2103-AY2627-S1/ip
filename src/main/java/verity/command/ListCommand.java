package verity.command;

import verity.storage.Storage;
import verity.task.TaskList;

/**
 * Lists every task currently in the task list.
 */
public class ListCommand extends Command {

    /**
     * Lists every task currently in the task list.
     *
     * @param tasks {@inheritDoc}
     * @param storage {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String execute(TaskList tasks, Storage storage) {
        if (tasks.isEmpty()) {
            return "Nothing. You have zero tasks - impressive, in a bad way.";
        }
        return "Here's all " + tasks.size() + " tasks, since you clearly forgot:\n"
                + formatNumberedList(tasks.getTasks());
    }
}
