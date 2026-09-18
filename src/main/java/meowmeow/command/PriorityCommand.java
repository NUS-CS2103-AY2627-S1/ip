package meowmeow.command;

import meowmeow.MeowmeowException;
import meowmeow.storage.Storage;
import meowmeow.task.Task;
import meowmeow.task.TaskList;
import meowmeow.task.TaskPriority;
import meowmeow.ui.Ui;

/**
 * Sets or clears a task's priority - the "priority" command.
 */
public class PriorityCommand extends Command {
    private final int taskNumber;
    private final TaskPriority priority;

    /**
     * Constructs a command that switches the given task to the given priority.
     *
     * @param taskNumber the 1-based position (as shown by "list") to change.
     * @param priority   the priority to switch that task to, or {@link TaskPriority#NONE} to clear it.
     */
    public PriorityCommand(int taskNumber, TaskPriority priority) {
        this.taskNumber = taskNumber;
        this.priority = priority;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws MeowmeowException {
        // tasks.get rejects an out-of-range position with the "doesn't
        // exist" message.
        Task task = tasks.get(taskNumber);
        task.setPriority(priority);
        storage.save(tasks.asList());
        return ui.showPriorityChange(task);
    }
}
