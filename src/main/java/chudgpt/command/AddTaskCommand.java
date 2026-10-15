package chudgpt.command;

import chudgpt.exception.ChudException;
import chudgpt.storage.Storage;
import chudgpt.task.Task;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/** Adds one task to the current task list. */
public class AddTaskCommand extends Command {
    private final Task taskToAdd;

    /**
     * Creates a command that adds the specified task.
     *
     * @param taskToAdd task to add when the command executes.
     */
    public AddTaskCommand(Task taskToAdd) {
        this.taskToAdd = taskToAdd;
    }

    /**
     * Adds the task, saves the updated task list, and returns a confirmation message.
     *
     * @param tasks the current task list
     * @param ui the UI handler used to create the confirmation
     * @param storage the storage handler used to save the updated list
     * @return the response confirming that the task was added
     * @throws ChudException if the task is a duplicate or cannot be saved.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        tasks.addUniqueTask(taskToAdd);
        storage.save(tasks);
        return ui.getTaskAddedMessage(taskToAdd, tasks.size());
    }
}
