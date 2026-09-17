package jonathan.command;

import java.io.IOException;

import jonathan.JonathanException;
import jonathan.parser.Parser;
import jonathan.storage.Storage;
import jonathan.task.Task;
import jonathan.task.TaskList;
import jonathan.ui.UI;
/** Marks a task as done or not done. */
public class MarkCommand extends Command {
    private int taskIndex;
    private boolean isMarkingAsDone;

    /**
     * Creates a command that updates the completion status of a task.
     *
     * @param taskIndex zero-based index of the task
     * @param isMarkingAsDone whether the task should be marked as done
     */
    public MarkCommand(int taskIndex, boolean isMarkingAsDone) {
        this.taskIndex = taskIndex;
        this.isMarkingAsDone = isMarkingAsDone;
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
        Parser.require(tasks.getSize() > 0, "There are no tasks to modify.");
        Parser.require(taskIndex >= 0 && taskIndex < tasks.getSize(),
                "Choose a task number from 1 to " + tasks.getSize() + ".");

        Task task = tasks.getTask(taskIndex);
        if (isMarkingAsDone) {
            task.markAsDone();
            ui.showMarked(task);
        } else {
            task.markAsNotDone();
            ui.showUnmarked(task);
        }
        storage.save(tasks.getAllTasks(), tasks.getSize());
    }
}
