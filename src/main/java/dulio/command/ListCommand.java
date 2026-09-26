package dulio.command;

import java.io.IOException;

import dulio.task.TaskList;
import dulio.ui.Ui;

/**
 * Displays all tasks in the task list.
 */
public class ListCommand extends Command {
    /**
     * Creates a list command.
     */
    public ListCommand() {
    }

    /**
     * Displays all tasks.
     *
     * @param tasks The task list to display.
     * @param ui The interface used for the response.
     * @throws IOException If persistence fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws IOException {
        ui.showList(tasks.list());
    }
}
