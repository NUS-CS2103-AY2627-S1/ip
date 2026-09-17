package jonathan.command;

import jonathan.storage.Storage;
import jonathan.task.TaskList;
import jonathan.ui.UI;
/** Displays all tasks in the task list. */
public class ListCommand extends Command {
    /**
     * Executes the command by adding the task to the task list, saving the updated
     * list to storage, and displaying the success message to the user.
     *
     * @param tasks   The current list of tasks.
     * @param ui      The user interface for displaying messages.
     * @param storage The storage handler for saving tasks to the disk.
     * @throws JonathanException If the task list has reached its maximum capacity.
     * @throws IOException       If an error occurs while saving the tasks to the file.
     */
    @Override
    public void execute(TaskList tasks, UI ui, Storage storage) {
        ui.showTaskList(tasks.getAllTasks(), tasks.getSize());
    }
}
