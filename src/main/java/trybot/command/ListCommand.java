package trybot.command;

import trybot.storage.Storage;
import trybot.task.TaskList;
import trybot.ui.Ui;

/**
 * Displays the current task list.
 */
public class ListCommand extends Command {
    /**
     * Displays the current tasks in their stored order.
     *
     * @param tasks current task list.
     * @param ui user-interface handler.
     * @param storage task persistence handler, unused because listing is read-only.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }

    /**
     * Indicates that listing tasks does not end the session.
     *
     * @return always false
     */
    @Override
    public boolean isExit() {
        return false;
    }
}
