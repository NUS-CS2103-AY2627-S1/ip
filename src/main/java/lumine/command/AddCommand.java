package lumine.command;

import lumine.task.Task;
import lumine.task.TaskList;
import lumine.ui.Ui;

/**
 * Adds a task (to-do, deadline, or event) to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that will add the given task to the task list.
     *
     * @param task the pre-built task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * {@inheritDoc}
     * Adds the task, then prints a confirmation message with the updated list size.
     */
    @Override
    public void execute(TaskList taskList, Ui ui) {
        taskList.addTask(task);
        ui.showMessage("Got it. I've added this task:\n  "
                + task + "\nNow, you have " + taskList.size() + " tasks in the list.");
    }
}
