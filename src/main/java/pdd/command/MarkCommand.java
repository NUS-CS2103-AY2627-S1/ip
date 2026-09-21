package pdd.command;

import pdd.task.Task;
import pdd.ui.Ui;

/** Marks the task at the given 1-based position as done. */
public class MarkCommand extends MarkStatusCommand {
    /** Creates a command that will mark the task at the given 1-based position (as typed by the user) done. */
    public MarkCommand(String taskNumberArg) {
        super(taskNumberArg);
    }

    @Override
    protected void applyStatus(Task task) {
        task.markAsDone();
    }

    @Override
    protected void showResult(Ui ui, Task task) {
        ui.showMarked(task);
    }
}
