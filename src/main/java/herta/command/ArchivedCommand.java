package herta.command;

import herta.exception.HertaException;
import herta.storage.Storage;
import herta.storage.TaskRepository;
import herta.task.TaskList;
import herta.ui.UiOutput;

/**
 * Represents the command that displays archived tasks.
 */
public class ArchivedCommand extends Command {
    private static final String EMPTY_ARCHIVE_MESSAGE =
            "The archive has nothing to show. Complete a task before archiving it.";
    private static final String ARCHIVE_HEADING = "The archive, as requested:";

    /**
     * Displays archived tasks in archive-file order with independent numbering.
     *
     * @param repository the repository containing the archive.
     * @param ui the output interface used to display responses.
     */
    @Override
    public void execute(TaskRepository repository, UiOutput ui) {
        TaskList archivedTasks = repository.getArchivedTasks();
        if (archivedTasks.size() == 0) {
            ui.showMessage(EMPTY_ARCHIVE_MESSAGE);
            return;
        }
        ui.showMessage(ARCHIVE_HEADING);
        for (int index = 0; index < archivedTasks.size(); index++) {
            ui.showTask(index + 1, archivedTasks.get(index));
        }
    }

    /**
     * Displays archived tasks for legacy direct command execution.
     *
     * @param tasks unused because this command reads the archive.
     * @param ui the output interface used to display responses.
     * @param storage storage for active tasks, used to locate the archive.
     */
    @Override
    public void execute(TaskList tasks, UiOutput ui, Storage storage) throws HertaException {
        execute(TaskRepository.createWithActiveTasks(tasks, storage), ui);
    }
}
