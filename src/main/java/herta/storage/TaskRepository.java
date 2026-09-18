package herta.storage;

import java.nio.file.Path;
import java.util.Objects;

import herta.exception.HertaException;
import herta.task.TaskList;

/**
 * Owns Herta's separate active and archived task collections and their storage.
 */
public final class TaskRepository {
    private final Storage activeStorage;
    private final Storage archiveStorage;
    private final TaskList activeTasks;
    private final TaskList archivedTasks;

    /**
     * Creates a repository from already loaded collections.
     *
     * @param activeStorage storage for active tasks.
     * @param archiveStorage storage for archived tasks.
     * @param activeTasks the active tasks.
     * @param archivedTasks the archived tasks.
     */
    public TaskRepository(Storage activeStorage, Storage archiveStorage,
                          TaskList activeTasks, TaskList archivedTasks) {
        this.activeStorage = Objects.requireNonNull(activeStorage);
        this.archiveStorage = Objects.requireNonNull(archiveStorage);
        this.activeTasks = Objects.requireNonNull(activeTasks);
        this.archivedTasks = Objects.requireNonNull(archivedTasks);
    }

    /**
     * Loads active tasks first and archived tasks second.
     *
     * @param activeFilePath the configured active-task file.
     * @return a repository containing both loaded collections.
     * @throws HertaException if either collection cannot be loaded.
     */
    public static TaskRepository load(String activeFilePath) throws HertaException {
        Storage activeStorage = new Storage(activeFilePath);
        Path archivePath = Storage.resolveArchivePath(activeFilePath);
        Storage archiveStorage = new Storage(archivePath.toString());
        Storage.validateDistinctPaths(activeStorage.getDataFile(), archiveStorage.getDataFile());
        StorageTransactionJournal.recoverPendingTransaction(activeStorage.getDataFile(), archivePath);

        TaskList activeTasks = activeStorage.load();
        TaskList archivedTasks = archiveStorage.loadArchived();
        try {
            return new TaskRepository(activeStorage, archiveStorage, activeTasks, archivedTasks);
        } catch (IllegalArgumentException e) {
            throw new HertaException("Failed to load archived tasks: " + e.getMessage());
        }
    }

    /**
     * Creates a repository using an existing active collection and loads its archive.
     * This adapter keeps direct command execution convenient for existing callers.
     *
     * @param activeTasks the active tasks to use.
     * @param activeStorage storage for active tasks.
     * @return a repository containing the supplied active tasks and loaded archive.
     * @throws HertaException if the archive cannot be loaded.
     */
    public static TaskRepository createWithActiveTasks(TaskList activeTasks,
                                                       Storage activeStorage) throws HertaException {
        Path archivePath = Storage.resolveArchivePath(activeStorage.getDataFile().toString());
        Storage archiveStorage = new Storage(archivePath.toString());
        Storage.validateDistinctPaths(activeStorage.getDataFile(), archiveStorage.getDataFile());
        TaskList archivedTasks = archiveStorage.loadArchived();
        try {
            return new TaskRepository(activeStorage, archiveStorage, activeTasks, archivedTasks);
        } catch (IllegalArgumentException e) {
            throw new HertaException("Failed to load archived tasks: " + e.getMessage());
        }
    }

    /**
     * Returns the active task collection.
     *
     * @return the active tasks.
     */
    public TaskList getActiveTasks() {
        return activeTasks;
    }

    /**
     * Returns the archived task collection.
     *
     * @return the archived tasks.
     */
    public TaskList getArchivedTasks() {
        return archivedTasks;
    }

    /**
     * Returns storage for active tasks.
     *
     * @return active-task storage.
     */
    public Storage getActiveStorage() {
        return activeStorage;
    }

    /** Resets persistence status before a command begins. */
    public void resetPersistenceState() {
        activeStorage.resetPersistenceState();
        archiveStorage.resetPersistenceState();
    }

    /** Returns the active storage's latest persistence outcome. */
    public PersistenceState getPersistenceState() {
        return activeStorage.getLastPersistenceState();
    }

    /**
     * Saves the resulting active and archived collections as one logical operation.
     *
     * @param updatedActiveTasks the resulting active collection.
     * @param updatedArchivedTasks the resulting archive collection.
     * @param failurePrefix the required user-facing persistence-error prefix.
     * @throws HertaException if validation, staging, commit, or rollback fails.
     */
    public void saveActiveAndArchivedTasks(TaskList updatedActiveTasks,
                                           TaskList updatedArchivedTasks, String failurePrefix)
            throws HertaException {
        activeStorage.saveActiveAndArchivedTasks(archiveStorage, updatedActiveTasks,
                updatedArchivedTasks, failurePrefix);
    }

    /**
     * Replaces both live collections after their corresponding files are saved.
     *
     * @param updatedActiveTasks the newly saved active collection.
     * @param updatedArchivedTasks the newly saved archive collection.
     */
    public void replaceActiveAndArchivedTasks(TaskList updatedActiveTasks,
                                              TaskList updatedArchivedTasks) {
        activeTasks.replaceWith(updatedActiveTasks);
        archivedTasks.replaceWith(updatedArchivedTasks);
    }
}
