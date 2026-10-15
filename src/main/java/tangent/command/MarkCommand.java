package tangent.command;

import java.util.List;

import tangent.task.Task;
import tangent.ui.Ui;

/** Marks one or more tasks as done and saves the resulting task list. */
public class MarkCommand extends StatusCommand {

    /** Creates a command that marks the supplied 0-based task indexes as done. */
    public MarkCommand(List<Integer> taskIndexes) {
        super(taskIndexes);
    }

    @Override
    protected boolean targetStatus() {
        return true;
    }

    @Override
    protected void showConfirmation(Ui ui, List<Task> changedTasks, List<Task> unchangedTasks) {
        ui.showTaskStatusChanged(true, changedTasks, unchangedTasks);
    }
}
