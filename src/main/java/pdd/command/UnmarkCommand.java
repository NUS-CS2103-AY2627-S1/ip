package pdd.command;

import pdd.task.Task;
import pdd.ui.Ui;

/** Marks the task at the given 1-based position as not done. */
public class UnmarkCommand extends MarkStatusCommand {
    /** Creates a command that will mark the task at the given 1-based position (as typed by the user) not done. */
    public UnmarkCommand(String taskNumberArg) {
        super(taskNumberArg);
    }

    @Override
    protected void applyStatus(Task task) {
        task.markAsNotDone();
    }

    @Override
    protected void showResult(Ui ui, Task task) {
        ui.showUnmarked(task);
    }
}
