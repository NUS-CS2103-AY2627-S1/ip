package bibi.command;

import bibi.BibiException;
import bibi.Storage;
import bibi.Ui;
import bibi.task.Task;
import bibi.task.TaskList;

/**
 * Removes one task from the list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that will remove the given task.
     *
     * @param taskNumber the one-based number shown to the user
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Removes the numbered task, shows what was removed, and saves the
     * shortened list.
     *
     * <p>Naming the task matters more here than for any other command: once it
     * is gone every later task moves up a number, so the number alone can no
     * longer tell the user which task they removed.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BibiException {
        Task removedTask = tasks.remove(taskNumber);
        ui.showMessage("Eeeeesh, another one bites the dust. Task " + taskNumber + " is off the list:");
        ui.showDetails(removedTask.toString());
        ui.showPlain("Your list holds " + Ui.describeCount(tasks.size()) + ".");
        saveTasks(tasks, ui, storage);
    }
}
