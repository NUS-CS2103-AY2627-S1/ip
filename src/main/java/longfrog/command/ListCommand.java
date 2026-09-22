package longfrog.command;

import java.util.List;

import longfrog.task.Task;
import longfrog.task.TaskList;
import longfrog.ui.Ui;

/** Displays every task currently in the task list. */
public class ListCommand implements Command {
    private final TaskList taskList;

    public ListCommand(TaskList taskList) {
        this.taskList = taskList;
    }

    /** Displays the task list or its empty-list message. */
    @Override
    public boolean execute(Ui ui) {
        ui.showMessage("Tasks currently on the lily pads:");

        List<Task> tasks = taskList.getTasks();
        if (tasks.isEmpty()) {
            ui.showMessage("The pond is clear—no tasks waiting.");
        } else {
            ui.showNumberedTasks(tasks);
        }

        return false;
    }
}
