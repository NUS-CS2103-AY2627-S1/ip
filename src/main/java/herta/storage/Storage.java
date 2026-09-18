package herta.storage;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import herta.exception.HertaException;
import herta.task.TaskList;

/**
 * Provides the public storage facade for one active or archived task file.
 */
public class Storage {
    private static final String ACTIVE_LOAD_PREFIX = "Failed to load tasks: ";
    private static final String ARCHIVE_LOAD_PREFIX = "Failed to load archived tasks: ";
    private static final String RECORD_LOAD_PREFIX = "Failed to load tasks ";
    private static final String ARCHIVE_RECORD_LOAD_PREFIX = "Failed to load archived tasks ";
    private final Path dataFile;
    private final TaskStorageConverter converter;
    private final StorageFileManager fileManager;
    private StorageFileManager.FileSnapshot lastKnownSnapshot;
    private boolean isConsistent = true;
    private PersistenceState lastPersistenceState = PersistenceState.NOT_ATTEMPTED;

    /** Creates storage backed by the given file path. */
    public Storage(String filePath) {
        try {
            dataFile = Path.of(Objects.requireNonNull(filePath));
        } catch (InvalidPathException | NullPointerException e) {
            throw new IllegalArgumentException("Configured data path is invalid.", e);
        }
        converter = new TaskStorageConverter();
        fileManager = new StorageFileManager(dataFile);
    }

    /** Returns the path managed by this storage. */
    public Path getDataFile() {
        return dataFile;
    }

    /** Resets the save outcome before a new command is parsed or executed. */
    public void resetPersistenceState() {
        lastPersistenceState = PersistenceState.NOT_ATTEMPTED;
    }

    /** Returns the outcome of the most recent save attempt. */
    public PersistenceState getLastPersistenceState() {
        return lastPersistenceState;
    }

    /** Resolves the archive path that belongs beside an active data file. */
    public static Path resolveArchivePath(String activeFilePath) {
        return StoragePathPolicy.resolveArchivePath(activeFilePath);
    }

    /** Rejects active and archive paths that identify the same file. */
    public static void validateDistinctPaths(Path activeFile, Path archiveFile)
            throws HertaException {
        StoragePathPolicy.validateDistinctPaths(activeFile, archiveFile);
    }

    /** Loads all saved tasks from the data file. */
    public TaskList load() throws HertaException {
        return loadWithPrefix(ACTIVE_LOAD_PREFIX, RECORD_LOAD_PREFIX);
    }

    /** Loads archived tasks from this storage file. */
    public TaskList loadArchived() throws HertaException {
        return loadWithPrefix(ARCHIVE_LOAD_PREFIX, ARCHIVE_RECORD_LOAD_PREFIX);
    }

    /** Loads tasks while retaining wording appropriate to the file's role. */
    private TaskList loadWithPrefix(String loadPrefix, String recordPrefix) throws HertaException {
        try {
            StorageFileManager.PathStatus pathStatus = fileManager.getPathStatus();
            if (pathStatus == StorageFileManager.PathStatus.MISSING) {
                lastKnownSnapshot = fileManager.captureSnapshot();
                return new TaskList();
            }
            if (pathStatus == StorageFileManager.PathStatus.DIRECTORY
                    || pathStatus == StorageFileManager.PathStatus.OTHER) {
                throw new HertaException(loadPrefix + "data path is not a regular file.");
            }
            if (pathStatus == StorageFileManager.PathStatus.INACCESSIBLE) {
                throw new HertaException(loadPrefix + "data path is inaccessible.");
            }
            TaskList loadedTasks = converter.parseStorageLines(fileManager.readLines(), recordPrefix);
            lastKnownSnapshot = fileManager.captureSnapshot();
            return loadedTasks;
        } catch (NoSuchFileException e) {
            throw new HertaException(loadPrefix + "data file is missing.");
        } catch (IOException | RuntimeException e) {
            StorageFailureMapper.log("Unable to load storage.", e);
            throw new HertaException(loadPrefix + StorageFailureMapper.map(e));
        }
    }

    /** Saves the complete in-memory task list to the data file. */
    public void save(TaskList tasks) throws HertaException {
        lastPersistenceState = PersistenceState.NOT_ATTEMPTED;
        List<String> lines = converter.serializeTasks(tasks);
        try (StorageFileLock ignored = StorageFileLock.acquire(dataFile)) {
            saveWhileLocked(lines);
        } catch (IOException | RuntimeException e) {
            StorageFailureMapper.log("Unable to acquire the storage lock.", e);
            throw new HertaException("Failed to save tasks: " + StorageFailureMapper.map(e));
        }
    }

    /** Saves one file while the adjacent lock excludes cooperating writers. */
    private void saveWhileLocked(List<String> lines) throws HertaException {
        ensureConsistent();
        StorageFileManager.FileSnapshot originalSnapshot = null;
        boolean wasReplacementStarted = false;
        Path temporaryFile = null;
        try {
            lastPersistenceState = PersistenceState.IN_PROGRESS;
            originalSnapshot = fileManager.captureSnapshot();
            initializeExpectedSnapshot(originalSnapshot);
            ensureHasNotChanged();
            temporaryFile = fileManager.writeTemporaryFile(lines);
            ensureHasNotChanged();
            wasReplacementStarted = true;
            fileManager.replaceDataFile(temporaryFile);
            temporaryFile = null;
            lastKnownSnapshot = fileManager.captureSnapshot();
            lastPersistenceState = PersistenceState.COMMITTED;
        } catch (IOException | RuntimeException e) {
            handleSingleFileSaveFailure(e, wasReplacementStarted, originalSnapshot, temporaryFile);
            throw new HertaException("Failed to save tasks: " + StorageFailureMapper.map(e));
        }
    }

    /** Restores and cleans up after an unexpected single-file save failure. */
    private void handleSingleFileSaveFailure(Exception failure, boolean wasReplacementStarted,
                                             StorageFileManager.FileSnapshot originalSnapshot,
                                             Path temporaryFile) throws HertaException {
        try {
            if (wasReplacementStarted) {
                restoreAfterFailedSave(originalSnapshot);
            } else {
                lastPersistenceState = PersistenceState.NOT_ATTEMPTED;
            }
        } finally {
            fileManager.deleteTemporaryFile(temporaryFile);
        }
        StorageFailureMapper.log("Unable to save storage.", failure);
    }

    /** Stages both task files as one logical operation. */
    public void saveActiveAndArchivedTasks(Storage archiveStorage, TaskList activeTasks,
                                           TaskList archivedTasks, String failurePrefix)
            throws HertaException {
        Objects.requireNonNull(archiveStorage, "Archive storage cannot be null.");
        Objects.requireNonNull(failurePrefix, "A storage failure prefix cannot be null.");
        lastPersistenceState = PersistenceState.NOT_ATTEMPTED;
        archiveStorage.lastPersistenceState = PersistenceState.NOT_ATTEMPTED;
        new StorageTransactionCoordinator(this, archiveStorage, failurePrefix)
                .save(activeTasks, archivedTasks);
    }

    /** Restores the original file if an unexpected failure occurs after replacement. */
    private void restoreAfterFailedSave(StorageFileManager.FileSnapshot originalSnapshot)
            throws HertaException {
        try {
            fileManager.restoreSnapshot(originalSnapshot);
            lastKnownSnapshot = originalSnapshot;
            lastPersistenceState = PersistenceState.NOT_ATTEMPTED;
        } catch (IOException | RuntimeException rollbackError) {
            isConsistent = false;
            lastPersistenceState = PersistenceState.UNKNOWN;
            StorageFailureMapper.log("Rollback failed while restoring storage.", rollbackError);
            throw new HertaException("Failed to save tasks: storage consistency is uncertain; "
                    + "restart Herta before writing again.");
        }
    }

    /** Prevents further writes after a rollback failure until startup recovery. */
    void ensureConsistent() throws HertaException {
        if (!isConsistent) {
            throw new HertaException("Storage consistency is uncertain; restart Herta to recover "
                    + "the preserved transaction files before writing again.");
        }
    }

    /** Aborts a save when the file no longer matches the state Herta loaded. */
    void ensureHasNotChanged() throws IOException {
        if (!fileManager.hasSameContents(lastKnownSnapshot)) {
            throw new IOException(StorageFailureMapper.EXTERNAL_CHANGE_ERROR);
        }
    }

    /** Records the first known state used for detecting edits made by another process. */
    void initializeExpectedSnapshot(StorageFileManager.FileSnapshot currentSnapshot) {
        if (lastKnownSnapshot == null) {
            lastKnownSnapshot = currentSnapshot;
        }
    }

    /** Updates the snapshot used by future external-change checks. */
    void updateKnownSnapshot(StorageFileManager.FileSnapshot snapshot) {
        lastKnownSnapshot = snapshot;
    }

    /** Sets the state visible to command execution and response classification. */
    void setPersistenceState(PersistenceState persistenceState) {
        lastPersistenceState = persistenceState;
    }

    /** Marks this storage unsafe for further writes after a failed rollback. */
    void markInconsistent() {
        isConsistent = false;
    }

    /** Returns this storage's file manager to transaction coordination. */
    StorageFileManager getFileManager() {
        return fileManager;
    }

    /** Serializes tasks using this storage's converter. */
    List<String> serializeTasks(TaskList tasks) throws HertaException {
        return converter.serializeTasks(tasks);
    }
}
