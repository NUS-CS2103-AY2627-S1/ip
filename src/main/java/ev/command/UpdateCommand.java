package ev.command;

import ev.EvException;
import ev.storage.Storage;
import ev.task.Task;
import ev.task.TaskList;
import ev.ui.Ui;

/**
 * Changes one detail of a task that is already in the list.
 *
 * <p>Which details can be changed depends on the kind of task, and the task itself
 * decides that, so this command works the same whatever it is handed.
 */
public class UpdateCommand extends Command {

    private final int taskNumber;
    private final String option;
    private final String value;

    /**
     * Creates a command that will change one detail of the given task.
     *
     * @param taskNumber the number the user gave, counting from 1.
     * @param option the option naming the detail to change, such as {@code /by}.
     * @param value the new value, already trimmed and not empty.
     */
    public UpdateCommand(int taskNumber, String option, String value) {
        this.taskNumber = taskNumber;
        this.option = option;
        this.value = value;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws EvException {
        Task task = tasks.getByNumber(taskNumber);
        task.applyUpdate(option, value);
        ui.showUpdated(task);
        storage.save(tasks.getTasks());
    }
}
