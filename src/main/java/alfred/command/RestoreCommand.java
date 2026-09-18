package alfred.command;

import alfred.AlfredException;
import alfred.task.Task;
import alfred.task.TaskList;
import alfred.ui.Ui;

/**
 * Restores one archived task, or every archived task, to the live list.
 */
public class RestoreCommand extends Command {
    /** Archive-list index to restore, or {@code null} to restore all archived tasks. */
    private final Integer archiveIndex;

    private RestoreCommand(Integer archiveIndex) {
        this.archiveIndex = archiveIndex;
    }

    /**
     * Creates a command that restores every archived task.
     *
     * @return Restore-all command.
     */
    public static RestoreCommand all() {
        return new RestoreCommand(null);
    }

    /**
     * Creates a command that restores the archived task at {@code archiveIndex}.
     *
     * @param archiveIndex Zero-based archive-list index.
     * @return Restore-one command.
     */
    public static RestoreCommand at(int archiveIndex) {
        return new RestoreCommand(archiveIndex);
    }

    @Override
    public void execute(TaskList tasks, Ui ui) throws AlfredException {
        if (archiveIndex == null) {
            int restoredCount = tasks.restoreAll();
            ui.showTasksRestored(restoredCount, tasks.size());
            return;
        }
        Task restored = tasks.restore(archiveIndex);
        ui.showTasksRestored(1, tasks.size());
        assert !restored.isArchived() : "Restore should return the task to the live list";
    }

    @Override
    public boolean isMutating() {
        return true;
    }
}
