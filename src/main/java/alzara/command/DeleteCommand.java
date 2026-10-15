package alzara.command;

import alzara.AlzaraException;
import alzara.storage.Storage;
import alzara.task.Task;
import alzara.task.TaskList;
import alzara.ui.Ui;

/**
 * Removes a task from the task list, persists the change, and reports it.
 */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command that deletes the task at the given 0-based index.
     *
     * @param taskIndex 0-based index parsed from the command text, not yet
     *         validated against the task list's current size
     */
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Removes the task at {@code taskIndex} from {@code memory}, persists the
     * updated list, and reports it.
     *
     * @throws AlzaraException if {@code taskIndex} doesn't refer to an existing task
     */
    @Override
    public void execute(TaskList memory, Ui ui, Storage storage) throws AlzaraException {
        if (taskIndex < 0 || taskIndex >= memory.size()) {
            throw new AlzaraException(AlzaraException.TASK_DOES_NOT_EXIST_MESSAGE);
        }
        Task deletedTask = memory.delete(taskIndex);
        storage.save(memory.getTasks(), ui);
        ui.showTaskDeleted(deletedTask, memory.size());
    }
}
