package anders.command;

import anders.AndersException;
import anders.collection.TaskList;
import anders.storage.Storage;
import anders.task.Task;
import anders.ui.Ui;

/** Deletes a selected task. */
public class DeleteCommand extends Command {
    private final String taskNumberText;

    /** Creates a delete command. */
    public DeleteCommand(String taskNumberText) {
        this.taskNumberText = taskNumberText;
    }
    /** Deletes the selected task when its user-facing number is valid. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws AndersException {
        try {
            int index = Integer.parseInt(taskNumberText) - 1;
            if (index < 0 || index >= tasks.size()) {
                ui.showInvalidTaskNumber(tasks.size());
                return;
            }
            Task removed = tasks.remove(index);
            storage.save(tasks);
            ui.showDeleted(removed, tasks.size());
        } catch (NumberFormatException e) {
            ui.showInvalidTaskNumberFormat();
        }
    }
}
