package friday.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import friday.exception.FridayException;
import friday.model.Task;
import friday.model.TaskList;
import friday.storage.Storage;
import friday.ui.Ui;

/**
 * Moves one or all archived tasks back into the active list.
 */
public class UnarchiveCommand extends Command {
    private final Integer archiveNumber;

    /**
     * Creates a command that restores one archived task.
     *
     * @param archiveNumber One-based number of the archived task to restore.
     */
    public UnarchiveCommand(int archiveNumber) {
        this.archiveNumber = archiveNumber;
    }

    /**
     * Creates a command that restores all archived tasks.
     */
    public UnarchiveCommand() {
        this.archiveNumber = null;
    }

    /**
     * Returns the selected archive number, or {@code null} when restoring all tasks.
     *
     * @return The selected archive number or {@code null} for all tasks.
     */
    public Integer getArchiveNumber() {
        return archiveNumber;
    }

    /**
     * Moves selected archived tasks into active storage before changing either list.
     *
     * @param tasks Current active task list.
     * @param archivedTasks Current archived task list.
     * @param ui UI used to display the result.
     * @param storage Storage for active tasks.
     * @param archiveStorage Storage for archived tasks.
     * @throws FridayException If the command cannot restore the selected tasks.
     */
    @Override
    public void execute(TaskList tasks, TaskList archivedTasks, Ui ui, Storage storage,
            Storage archiveStorage) throws FridayException {
        requireStorage(storage, archiveStorage);
        if (archiveNumber == null) {
            unarchiveAll(tasks, archivedTasks, ui, storage, archiveStorage);
            return;
        }

        Task task = getArchivedTask(archivedTasks, archiveNumber);
        List<Task> updatedTasks = new ArrayList<>(tasks.asList());
        List<Task> updatedArchivedTasks = new ArrayList<>(archivedTasks.asList());
        updatedTasks.add(task);
        updatedArchivedTasks.remove(archiveNumber - 1);
        saveMovedTasks(storage, archiveStorage, updatedTasks, updatedArchivedTasks);
        tasks.replaceWith(updatedTasks);
        archivedTasks.replaceWith(updatedArchivedTasks);
        ui.showTaskUnarchived(task);
    }

    private static void unarchiveAll(TaskList tasks, TaskList archivedTasks, Ui ui, Storage storage,
            Storage archiveStorage) throws FridayException {
        if (archivedTasks.size() == 0) {
            ui.showNoArchivedTasksToRestore();
            return;
        }

        List<Task> updatedTasks = new ArrayList<>(tasks.asList());
        updatedTasks.addAll(archivedTasks.asList());
        List<Task> updatedArchivedTasks = new ArrayList<>();
        saveMovedTasks(storage, archiveStorage, updatedTasks, updatedArchivedTasks);
        int restoredCount = archivedTasks.size();
        tasks.replaceWith(updatedTasks);
        archivedTasks.replaceWith(updatedArchivedTasks);
        ui.showTasksUnarchived(restoredCount);
    }

    private static Task getArchivedTask(TaskList archivedTasks, int archiveNumber) throws FridayException {
        int index = archiveNumber - 1;
        if (index < 0 || index >= archivedTasks.size()) {
            throw new FridayException("Please enter a valid archived task number.");
        }
        return archivedTasks.get(index);
    }

    private static void saveMovedTasks(Storage storage, Storage archiveStorage, List<Task> updatedTasks,
            List<Task> updatedArchivedTasks) throws FridayException {
        try {
            storage.save(new TaskList(updatedTasks));
            archiveStorage.save(new TaskList(updatedArchivedTasks));
        } catch (IOException e) {
            throw new FridayException("I couldn't update your archive. Your task list was not changed.");
        }
    }

    private static void requireStorage(Storage storage, Storage archiveStorage) throws FridayException {
        if (storage == null || archiveStorage == null) {
            throw new FridayException("Archive storage is unavailable. Please check the task data files.");
        }
    }
}
