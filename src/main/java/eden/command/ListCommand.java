package eden.command;

import eden.task.TaskList;
import eden.ui.Ui;

/**
 * Displays every task in the task list.
 */
public class ListCommand extends Command {
    private final TaskList tasks;

    /**
     * Creates a command that displays the current task list.
     *
     * @param tasks task list to display.
     */
    public ListCommand(TaskList tasks) {
        this.tasks = tasks;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String execute(Ui ui) {
        return ui.formatTaskList(tasks.asList());
    }
}
