package jonathan.command;

import java.io.IOException;

import jonathan.JonathanException;
import jonathan.parser.Parser;
import jonathan.storage.Storage;
import jonathan.task.Priority;
import jonathan.task.Task;
import jonathan.task.TaskList;
import jonathan.ui.UI;

/** Assigns a priority to an existing task. */
public class PriorityCommand extends Command {
    private final int taskIndex;
    private final Priority priority;

    /**
     * Creates a priority command.
     *
     * @param taskIndex zero-based task index
     * @param priority priority to assign
     */
    public PriorityCommand(int taskIndex, Priority priority) {
        this.taskIndex = taskIndex;
        this.priority = priority;
    }

    /**
     * Assigns the selected priority and saves the updated task list.
     *
     * @param tasks task list containing the target task
     * @param ui user interface used to display the result
     * @param storage storage used to persist the updated task list
     * @throws JonathanException if the task index is invalid
     * @throws IOException if the updated task list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, UI ui, Storage storage)
            throws JonathanException, IOException {
        Parser.require(tasks.getSize() > 0, "There are no tasks to prioritize.");
        Parser.require(taskIndex >= 0 && taskIndex < tasks.getSize(),
                "Choose a task number from 1 to " + tasks.getSize() + ".");

        Task task = tasks.getTask(taskIndex);
        task.setPriority(priority);

        storage.save(tasks.getAllTasks(), tasks.getSize());
        ui.showPriorityUpdated(task);
    }
}
