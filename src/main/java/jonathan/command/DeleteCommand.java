package jonathan.command;

import java.io.IOException;

import jonathan.JonathanException;
import jonathan.parser.Parser;
import jonathan.storage.Storage;
import jonathan.task.Task;
import jonathan.task.TaskList;
import jonathan.ui.UI;
/** Deletes a task from the task list. */
public class DeleteCommand extends Command {
    private int taskIndex;

    /**
     * Constructs a command for deleting a task at the specified index.
     *
     * @param taskIndex zero-based index of the task to be deleted
     */
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

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
    public void execute(TaskList tasks, UI ui, Storage storage) throws JonathanException, IOException {
        Parser.require(tasks.getSize() > 0, "There are no tasks to delete.");
        Parser.require(taskIndex >= 0 && taskIndex < tasks.getSize(),
                "Choose a task number from 1 to " + tasks.getSize() + ".");
        Task deletedTask = tasks.deleteTask(taskIndex);
        storage.save(tasks.getAllTasks(), tasks.getSize());
        ui.showDeleted(deletedTask, tasks.getSize());

    }
}
