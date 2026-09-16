package eve.command;

import eve.EveException;
import eve.Storage;
import eve.Ui;
import eve.task.TaskList;

/** Marks a task as not done, persists the change, and reports it. */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that marks the given 1-based task number as not
     * done. The number is only format-checked at this point; whether it's
     * in range depends on the list size and is checked in {@link #execute}.
     *
     * @param taskNumber the 1-based task number typed by the user.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws EveException {
        int index = tasks.toIndex(taskNumber);
        tasks.get(index).markAsNotDone();
        storage.save(tasks.asList());
        ui.showTaskUnmarked(tasks.get(index));
    }
}
