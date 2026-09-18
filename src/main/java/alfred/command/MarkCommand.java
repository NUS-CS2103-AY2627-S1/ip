package alfred.command;

import alfred.AlfredException;
import alfred.task.Task;
import alfred.task.TaskList;
import alfred.ui.Ui;

/**
 * Marks or unmarks a task identified by a zero-based index.
 */
public class MarkCommand extends Command {
    /** Zero-based index of the task to update. */
    private final int index;

    /** {@code true} to mark done, {@code false} to mark not done. */
    private final boolean isDone;

    /**
     * Creates a command that updates completion status.
     *
     * @param index Zero-based index parsed from the user command.
     * @param isDone {@code true} to mark done, {@code false} to unmark.
     */
    public MarkCommand(int index, boolean isDone) {
        this.index = index;
        this.isDone = isDone;
    }

    @Override
    public void execute(TaskList tasks, Ui ui) throws AlfredException {
        Task task = isDone ? tasks.mark(index) : tasks.unmark(index);
        assert isDone == "X".equals(task.getStatusIcon())
                : "Marking should update the task completion status";
        ui.showTaskMarked(task, isDone);
    }

    @Override
    public boolean isMutating() {
        return true;
    }
}
