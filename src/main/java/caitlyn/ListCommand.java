package caitlyn;

import java.util.List;

/**
 * A command that displays every task in the current list.
 */
public final class ListCommand extends Command {
    /**
     * Creates a command that lists the current tasks.
     */
    public ListCommand() {
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    /**
     * Displays every task currently stored in the task list.
     */
    @Override
    public void execute(List<Task> tasks, Ui ui) {
        ui.showTasks(tasks);
    }
}
