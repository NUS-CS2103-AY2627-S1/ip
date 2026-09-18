package alfred.command;

import alfred.task.TaskList;
import alfred.ui.Ui;

/**
 * Lists live tasks, or archived tasks, in insertion order.
 */
public class ListCommand extends Command {
    /** {@code true} to list archived tasks instead of live tasks. */
    private final boolean isArchiveList;

    /** Creates a command that lists live tasks. */
    public ListCommand() {
        this(false);
    }

    /**
     * Creates a command that lists live or archived tasks.
     *
     * @param isArchiveList {@code true} to list archived tasks.
     */
    public ListCommand(boolean isArchiveList) {
        this.isArchiveList = isArchiveList;
    }

    @Override
    public void execute(TaskList tasks, Ui ui) {
        if (isArchiveList) {
            ui.showArchivedTaskList(tasks.getArchivedTasks());
            return;
        }
        ui.showTaskList(tasks.getTasks());
    }
}
