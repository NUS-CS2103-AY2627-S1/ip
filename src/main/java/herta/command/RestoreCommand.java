package herta.command;

import herta.exception.HertaException;
import herta.storage.Storage;
import herta.storage.TaskRepository;
import herta.task.Task;
import herta.task.TaskList;
import herta.ui.UiOutput;

/**
 * Represents the command that restores one archived task to the active list.
 */
public class RestoreCommand extends Command {
    private static final String RESTORE_FAILURE_PREFIX = "Failed to restore task: ";
    private static final String INVALID_ARCHIVE_TASK_ERROR =
            "That number points to nothing in the archive. Check again.";
    private static final String RESTORE_SUCCESS_MESSAGE = "There. I've restored it:";
    private static final String ACTIVE_TASK_COUNT_MESSAGE =
            "That makes %d active %s. Back where it belongs.";
    private final int archiveIndex;

    /**
     * Creates a restore command for a zero-based archive index.
     *
     * @param archiveIndex the zero-based index of the archived task.
     */
    public RestoreCommand(int archiveIndex) {
        if (archiveIndex < 0) {
            throw new IllegalArgumentException("An archive index cannot be negative.");
        }
        this.archiveIndex = archiveIndex;
    }

    /**
     * Restores the selected task and appends it to the active collection.
     *
     * @param repository the repository containing both task collections.
     * @param ui the output interface used to display responses.
     * @throws HertaException if the archive index is invalid or persistence fails.
     */
    @Override
    public void execute(TaskRepository repository, UiOutput ui) throws HertaException {
        TaskList archivedTasks = repository.getArchivedTasks();
        if (archiveIndex < 0 || archiveIndex >= archivedTasks.size()) {
            throw new HertaException(INVALID_ARCHIVE_TASK_ERROR);
        }

        Task task = archivedTasks.get(archiveIndex);
        TaskList updatedActiveTasks = new TaskList(repository.getActiveTasks().asUnmodifiableList());
        TaskList updatedArchivedTasks = new TaskList(archivedTasks.asUnmodifiableList());
        updatedArchivedTasks.remove(archiveIndex);
        updatedActiveTasks.add(task);

        repository.saveActiveAndArchivedTasks(updatedActiveTasks, updatedArchivedTasks,
                RESTORE_FAILURE_PREFIX);
        repository.replaceActiveAndArchivedTasks(updatedActiveTasks, updatedArchivedTasks);
        ui.showMessage(RESTORE_SUCCESS_MESSAGE);
        ui.showTask(task);
        int activeTaskCount = updatedActiveTasks.size();
        ui.showMessage(ACTIVE_TASK_COUNT_MESSAGE.formatted(
                activeTaskCount, UiOutput.getTaskNoun(activeTaskCount)));
    }

    /**
     * Adapts legacy direct command execution to the two-collection repository.
     *
     * @param tasks the active tasks.
     * @param ui the output interface used to display responses.
     * @param storage storage for active tasks.
     * @throws HertaException if the archive cannot be loaded or saved.
     */
    @Override
    public void execute(TaskList tasks, UiOutput ui, Storage storage) throws HertaException {
        execute(TaskRepository.createWithActiveTasks(tasks, storage), ui);
    }
}
