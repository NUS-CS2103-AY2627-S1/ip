package chudgpt.command;

import chudgpt.exception.ChudException;
import chudgpt.storage.Storage;
import chudgpt.task.Priority;
import chudgpt.task.Task;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/** Changes one task's priority and invokes the existing automatic-save behavior. */
public class ChangeTaskPriorityCommand extends Command {
    private final int taskIndex;
    private final Priority priority;

    /**
     * Creates a command to assign a priority to an existing task.
     *
     * @param taskIndex zero-based index of the task to update.
     * @param priority priority to assign, including NONE to clear it.
     */
    public ChangeTaskPriorityCommand(int taskIndex, Priority priority) {
        this.taskIndex = taskIndex;
        this.priority = priority;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        Task updatedTask = tasks.updatePriority(taskIndex, priority);
        storage.save(tasks);
        return ui.getTaskPriorityMessage(updatedTask);
    }
}
