package longfrog.command;

import longfrog.task.Task;
import longfrog.task.TaskList;
import longfrog.ui.Ui;

/** Marks the selected task as complete. */
public class MarkCommand implements Command {
    private final TaskList taskList;
    private final int index;

    /**
     * Creates a command that marks the task at the specified index.
     *
     * @param taskList the task list containing the target task
     * @param index the zero-based task index
     */
    public MarkCommand(TaskList taskList, int index) {
        this.taskList = taskList;
        this.index = index;
    }

    /** Marks the task and displays the outcome. */
    @Override
    public boolean execute(Ui ui) {
        Task task = taskList.getTask(index);
        if (task == null) {
            ui.showInvalidTaskIndex(index, taskList.getCount(), "mark");
        } else if (task.isDone()) {
            ui.showMessage("That task is already marked done: " + task.getName());
        } else {
            task.markAsDone();
            ui.showMessage("Caught it. Marked done: " + task.getName());
        }

        return false;
    }
}
