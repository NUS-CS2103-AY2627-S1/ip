package anders.command;

import anders.AndersException;
import anders.collection.TaskList;
import anders.storage.Storage;
import anders.ui.Ui;

/** Changes the completion state of a selected task. */
public class MarkCommand extends Command {
    private final String taskNumberText;
    private final boolean isDone;

    /**
     * Creates a mark or unmark command.
     *
     * @param taskNumberText the one-based task number supplied by the user
     * @param isDone whether the task should be marked as done
     */
    public MarkCommand(String taskNumberText, boolean isDone) {
        this.taskNumberText = taskNumberText;
        this.isDone = isDone;
    }

    /** Updates the selected task's completion state and saves the task list. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws AndersException {
        try {
            int index = Integer.parseInt(taskNumberText) - 1;
            if (index < 0 || index >= tasks.size()) {
                ui.showInvalidTaskNumber(tasks.size());
                return;
            }
            if (isDone) {
                tasks.get(index).markAsDone();
            } else {
                tasks.get(index).markAsNotDone();
            }
            storage.save(tasks);
            ui.showMarked(tasks.get(index), isDone);
        } catch (NumberFormatException e) {
            ui.showInvalidTaskNumberFormat();
        }
    }
}
