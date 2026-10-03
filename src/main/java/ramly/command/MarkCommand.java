package ramly.command;

import ramly.model.Task;
import ramly.model.TaskList;

/** Command that marks a task as done. */
public class MarkCommand extends TaskStateCommand {
    /** Creates a mark command for a validated zero-based task index. */
    public MarkCommand(int taskIndex) {
        super(taskIndex);
    }

    /** Marks the selected task as completed. */
    @Override
    protected Task update(TaskList tasks, int taskIndex) {
        return tasks.mark(taskIndex);
    }

    /** Returns the confirmation shown after marking a task. */
    @Override
    protected String successMessage() {
        return "Checkpoint reached! Nice work:";
    }
}
