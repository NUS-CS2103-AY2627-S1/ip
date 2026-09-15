package monkey.command;

import monkey.model.TaskList;
import monkey.storage.Storage;
import monkey.ui.Ui;

/** Command object for marking a task done or not done. */
public class MarkCommand extends CommandAction {
    private final String taskNumber;
    private final boolean markDone;

    /** Creates a command that changes the completion state of a task. */
    public MarkCommand(String taskNumber, boolean markDone) {
        this.taskNumber = taskNumber;
        this.markDone = markDone;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        int index;
        try {
            index = Integer.parseInt(taskNumber) - 1;
        } catch (NumberFormatException e) {
            ui.showMessage(markDone
                    ? "OOPS! Monkey says: That banana-shaped task number does not look right. "
                            + "Use a number after 'mark'."
                    : "OOPS! Monkey says: This monkey needs a valid task number after 'unmark'.");
            return;
        }
        if (index < 0 || index >= tasks.size()) {
            ui.showMessage("That task number does not exist.");
            return;
        }
        boolean wasDone = tasks.get(index).isDone();
        if (markDone) {
            tasks.mark(index);
            if (!saveTasks(tasks.asList(), ui, storage)) {
                restoreCompletionState(tasks, index, wasDone);
                return;
            }
            ui.showMessage("Nice! I've marked this task as done:");
        } else {
            tasks.unmark(index);
            if (!saveTasks(tasks.asList(), ui, storage)) {
                restoreCompletionState(tasks, index, wasDone);
                return;
            }
            ui.showMessage("OK, I've marked this task as not done yet:");
        }
        ui.showMessage("  " + tasks.get(index));
    }

    private static void restoreCompletionState(TaskList tasks, int index, boolean wasDone) {
        if (wasDone) {
            tasks.mark(index);
        } else {
            tasks.unmark(index);
        }
    }
}
