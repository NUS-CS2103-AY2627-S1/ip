package ramly.command;

import ramly.model.Task;
import ramly.model.TaskList;

/** Command that marks a task as not done. */
public class UnmarkCommand extends TaskStateCommand {
    /** Creates an unmark command for a validated zero-based task index. */
    public UnmarkCommand(int taskIndex) {
        super(taskIndex);
    }

    /** Marks the selected task as incomplete. */
    @Override
    protected Task update(TaskList tasks, int taskIndex) {
        return tasks.unmark(taskIndex);
    }

    /** Returns the confirmation shown after unmarking a task. */
    @Override
    protected String successMessage() {
        return "This one is back on the trail:";
    }
}
