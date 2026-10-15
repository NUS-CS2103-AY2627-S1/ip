package alzara.command;

import alzara.AlzaraException;
import alzara.storage.Storage;
import alzara.task.Task;
import alzara.task.TaskList;
import alzara.ui.Ui;

/**
 * Marks a task as not done, persists the change, and reports it.
 */
public class UnmarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command that marks the task at the given 0-based index as not done.
     *
     * @param taskIndex 0-based index parsed from the command text, not yet
     *         validated against the task list's current size
     */
    public UnmarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Marks the task at {@code taskIndex} as not done, persists the updated
     * list, and reports it.
     *
     * @throws AlzaraException if {@code taskIndex} doesn't refer to an existing task
     */
    @Override
    public void execute(TaskList memory, Ui ui, Storage storage) throws AlzaraException {
        if (taskIndex < 0 || taskIndex >= memory.size()) {
            throw new AlzaraException(AlzaraException.TASK_DOES_NOT_EXIST_MESSAGE);
        }
        Task task = memory.get(taskIndex);
        task.unmark();
        storage.save(memory.getTasks(), ui);
        ui.showTaskUnmarked(task);
    }
}
