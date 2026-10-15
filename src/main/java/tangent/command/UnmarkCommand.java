package tangent.command;

import java.util.List;

import tangent.task.Task;
import tangent.ui.Ui;

/** Marks one or more tasks as undone and saves the resulting task list. */
public class UnmarkCommand extends StatusCommand {

    /** Creates a command that marks the supplied 0-based task indexes as undone. */
    public UnmarkCommand(List<Integer> taskIndexes) {
        super(taskIndexes);
    }

    @Override
    protected boolean targetStatus() {
        return false;
    }

    @Override
    protected void showConfirmation(Ui ui, List<Task> changedTasks, List<Task> unchangedTasks) {
        ui.showTaskStatusChanged(false, changedTasks, unchangedTasks);
    }
}
