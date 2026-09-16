package eve.command;

import eve.EveException;
import eve.Storage;
import eve.Ui;
import eve.task.Task;
import eve.task.TaskList;

/** Removes a task from the list, persists the change, and reports it. */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that removes the given 1-based task number. The
     * number is only format-checked at this point; whether it's in range
     * depends on the list size and is checked in {@link #execute}.
     *
     * @param taskNumber the 1-based task number typed by the user.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws EveException {
        int index = tasks.toIndex(taskNumber);
        Task removed = tasks.delete(index);
        storage.save(tasks.asList());
        ui.showTaskDeleted(removed, tasks.size());
    }
}
