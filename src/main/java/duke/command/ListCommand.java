package duke.command;

import duke.task.TaskList;
import duke.ui.Ui;

/**
 * Lists all tasks in the current task list.
 */
public class ListCommand extends Command {

    /**
     * Shows the current task list via the UI.
     *
     * @param tasks Task list to display.
     * @param ui UI used to display the task list.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showTaskList(tasks.asList());
    }
}
