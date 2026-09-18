package alfred.command;

import alfred.AlfredException;
import alfred.task.Task;
import alfred.task.TaskList;
import alfred.ui.Ui;

/**
 * Archives one live task, or every live task.
 */
public class ArchiveCommand extends Command {
    /** Live-list index to archive, or {@code null} to archive all live tasks. */
    private final Integer liveIndex;

    private ArchiveCommand(Integer liveIndex) {
        this.liveIndex = liveIndex;
    }

    /**
     * Creates a command that archives every live task.
     *
     * @return Archive-all command.
     */
    public static ArchiveCommand all() {
        return new ArchiveCommand(null);
    }

    /**
     * Creates a command that archives the live task at {@code liveIndex}.
     *
     * @param liveIndex Zero-based live-list index.
     * @return Archive-one command.
     */
    public static ArchiveCommand at(int liveIndex) {
        return new ArchiveCommand(liveIndex);
    }

    @Override
    public void execute(TaskList tasks, Ui ui) throws AlfredException {
        if (liveIndex == null) {
            int archivedCount = tasks.archiveAll();
            ui.showTasksArchived(archivedCount, tasks.size());
            return;
        }
        Task archived = tasks.archive(liveIndex);
        ui.showTasksArchived(1, tasks.size());
        assert archived.isArchived() : "Archive should hide the selected live task";
    }

    @Override
    public boolean isMutating() {
        return true;
    }
}
