package yourhand.commands;

import yourhand.exceptions.YourHandException;
import yourhand.storage.Storage;
import yourhand.tasks.Task;
import yourhand.tasks.TaskList;
import yourhand.ui.Ui;

/** Adds one parsed task to the task list. */
public class AddTaskCommand extends Command {
    private final Task task;

    /** Creates a command that adds the given task. */
    public AddTaskCommand(Task task) {
        this.task = task;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws YourHandException {
        int existingTaskNumber = taskList.findTaskNumberByDescription(task.getDescription());
        if (existingTaskNumber != -1) {
            ui.showDuplicateTaskWarning(existingTaskNumber);
        }
        int taskNumber = taskList.size();
        taskList.add(task);
        try {
            saveTasks(taskList, storage);
        } catch (YourHandException exception) {
            taskList.removeTask(taskNumber + 1);
            throw exception;
        }
        ui.showTaskAdded(task, taskList.size());
    }
}
