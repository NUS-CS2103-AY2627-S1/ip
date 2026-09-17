package jonathan.command;

import java.io.IOException;

import jonathan.JonathanException;
import jonathan.parser.Parser;
import jonathan.storage.Storage;
import jonathan.task.Task;
import jonathan.task.TaskList;
import jonathan.ui.UI;

/**
 * Adds a task to the task list and persists the updated list.
 */
public class AddCommand extends Command {
    private Task task;

    /**
     * Constructs an {@code AddCommand} with the specified task to be added.
     *
     * @param task task to be added to the task list
     */
    public AddCommand(Task task) {
        this.task = task;
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
        Parser.require(!tasks.isFull(), "The task list is full");
        tasks.addTask(task);
        storage.save(tasks.getAllTasks(), tasks.getSize());
        ui.showAdded(this.task, tasks.getSize());

    }

}
