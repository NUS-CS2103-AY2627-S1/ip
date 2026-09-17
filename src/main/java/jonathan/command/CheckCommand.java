package jonathan.command;

import jonathan.storage.Storage;
import jonathan.task.TaskList;
import jonathan.ui.UI;
/** Displays tasks that occur on a specified date. */
public class CheckCommand extends Command {
    private String dateString;

    /**
     * Creates a command that checks tasks on the given date.
     *
     * @param dateString date to check
     */
    public CheckCommand(String dateString) {
        this.dateString = dateString;
    }

    /**
     * Executes the command by displaying tasks that occur on the requested date.
     *
     * @param tasks current list of tasks
     * @param ui user interface for displaying messages
     * @param storage storage handler, not used by this command
     */
    @Override
    public void execute(TaskList tasks, UI ui, Storage storage) {
        ui.showActivitiesOn(dateString, tasks.getAllTasks(), tasks.getSize());
    }
}
