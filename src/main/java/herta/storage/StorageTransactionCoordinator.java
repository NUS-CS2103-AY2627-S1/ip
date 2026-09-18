package herta.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import herta.exception.HertaException;
import herta.task.TaskList;

/**
 * Coordinates durable replacement and rollback of active and archived task files.
 */
final class StorageTransactionCoordinator {
    private final Storage activeStorage;
    private final Storage archiveStorage;
    private final String failurePrefix;

    /** Stores all data captured before a two-file persistence transaction begins. */
    private record StorageTransactionPlan(List<String> activeLines, List<String> archiveLines,
                                          StorageFileManager.FileSnapshot originalActiveFile,
                                          StorageFileManager.FileSnapshot originalArchiveFile,
                                          StorageTransactionJournal transactionJournal) {
    }

    /** Stores a rollback message together with whether transaction cleanup is safe. */
    private record RollbackResult(String failureReason, boolean wasSuccessful) {
    }

    StorageTransactionCoordinator(Storage activeStorage, Storage archiveStorage,
                                  String failurePrefix) {
        this.activeStorage = Objects.requireNonNull(activeStorage);
        this.archiveStorage = Objects.requireNonNull(archiveStorage);
        this.failurePrefix = Objects.requireNonNull(failurePrefix);
    }

    /** Saves both collections while preserving the original state on a failed commit. */
    void save(TaskList activeTasks, TaskList archivedTasks) throws HertaException {
        StorageTransactionPlan transactionPlan = null;
        try (StorageFileLock ignored = StorageFileLock.acquire(activeStorage.getDataFile(),
                archiveStorage.getDataFile())) {
            setInProgress();
            transactionPlan = prepareTransaction(activeTasks, archivedTasks);
            commitTransaction(transactionPlan);
        } catch (HertaException e) {
            setNotAttempted();
            throw withFailurePrefix(e.getMessage());
        } catch (IOException | RuntimeException e) {
            handleUnexpectedFailure(transactionPlan, e);
        }
    }

    /** Marks both storage facades as participating in the current transaction. */
    private void setInProgress() {
        activeStorage.setPersistenceState(PersistenceState.IN_PROGRESS);
        archiveStorage.setPersistenceState(PersistenceState.IN_PROGRESS);
    }

    /** Resets states after a failure that happened before transaction preparation. */
    private void setNotAttempted() {
        activeStorage.setPersistenceState(PersistenceState.NOT_ATTEMPTED);
        archiveStorage.setPersistenceState(PersistenceState.NOT_ATTEMPTED);
    }

    /** Rolls back unexpected failures and retains a journal when rollback is uncertain. */
    private void handleUnexpectedFailure(StorageTransactionPlan transactionPlan,
                                         Exception failure) throws HertaException {
        RollbackResult rollbackResult = rollbackTransaction(transactionPlan,
                StorageFailureMapper.map(failure));
        if (rollbackResult.wasSuccessful()) {
            cleanUpTransaction(transactionPlan);
        }
        throw withFailurePrefix(rollbackResult.failureReason());
    }

    /** Validates both collections and records their original state. */
    private StorageTransactionPlan prepareTransaction(TaskList activeTasks,
                                                      TaskList archivedTasks)
            throws HertaException, IOException {
        activeStorage.ensureConsistent();
        archiveStorage.ensureConsistent();
        List<String> activeLines = activeStorage.serializeTasks(activeTasks);
        List<String> archiveLines = archiveStorage.serializeTasks(archivedTasks);
        StorageFileManager.FileSnapshot originalActiveFile = activeStorage.getFileManager()
                .captureSnapshot();
        StorageFileManager.FileSnapshot originalArchiveFile = archiveStorage.getFileManager()
                .captureSnapshot();
        activeStorage.initializeExpectedSnapshot(originalActiveFile);
        archiveStorage.initializeExpectedSnapshot(originalArchiveFile);
        activeStorage.ensureHasNotChanged();
        archiveStorage.ensureHasNotChanged();
        StorageTransactionJournal transactionJournal = StorageTransactionJournal.prepare(
                activeStorage.getDataFile(), archiveStorage.getDataFile(), originalActiveFile,
                originalArchiveFile);
        return new StorageTransactionPlan(activeLines, archiveLines, originalActiveFile,
                originalArchiveFile, transactionJournal);
    }

    /** Commits both replacements and updates snapshots used by future saves. */
    private void commitTransaction(StorageTransactionPlan transactionPlan) throws IOException {
        writeAndReplaceBothFiles(transactionPlan);
        activeStorage.updateKnownSnapshot(activeStorage.getFileManager().captureSnapshot());
        archiveStorage.updateKnownSnapshot(archiveStorage.getFileManager().captureSnapshot());
        activeStorage.setPersistenceState(PersistenceState.COMMITTED);
        archiveStorage.setPersistenceState(PersistenceState.COMMITTED);
        transactionPlan.transactionJournal().cleanUp();
    }

    /** Stages and replaces both serialized task files. */
    private void writeAndReplaceBothFiles(StorageTransactionPlan transactionPlan)
            throws IOException {
        Path temporaryActiveFile = null;
        Path temporaryArchiveFile = null;
        try {
            temporaryActiveFile = activeStorage.getFileManager().writeTemporaryFile(
                    transactionPlan.activeLines());
            temporaryArchiveFile = archiveStorage.getFileManager().writeTemporaryFile(
                    transactionPlan.archiveLines());
            activeStorage.ensureHasNotChanged();
            archiveStorage.ensureHasNotChanged();
            activeStorage.getFileManager().replaceDataFile(temporaryActiveFile);
            temporaryActiveFile = null;
            transactionPlan.transactionJournal().markActiveCommitted(activeStorage.getDataFile(),
                    archiveStorage.getDataFile(), transactionPlan.originalActiveFile(),
                    transactionPlan.originalArchiveFile());
            archiveStorage.ensureHasNotChanged();
            archiveStorage.getFileManager().replaceDataFile(temporaryArchiveFile);
            temporaryArchiveFile = null;
            transactionPlan.transactionJournal().markCommitted(activeStorage.getDataFile(),
                    archiveStorage.getDataFile(), transactionPlan.originalActiveFile(),
                    transactionPlan.originalArchiveFile());
        } finally {
            activeStorage.getFileManager().deleteTemporaryFile(temporaryActiveFile);
            archiveStorage.getFileManager().deleteTemporaryFile(temporaryArchiveFile);
        }
    }

    /** Restores the captured state after an unexpected two-file commit failure. */
    private RollbackResult rollbackTransaction(StorageTransactionPlan transactionPlan,
                                               String failureReason) {
        if (transactionPlan == null) {
            setNotAttempted();
            return new RollbackResult(failureReason, true);
        }
        try {
            activeStorage.getFileManager().restoreSnapshot(transactionPlan.originalActiveFile());
            archiveStorage.getFileManager().restoreSnapshot(transactionPlan.originalArchiveFile());
            setNotAttempted();
            return new RollbackResult(failureReason, true);
        } catch (IOException | RuntimeException rollbackError) {
            String recoveryMessage = failureReason + "; rollback failed; recovery journal retained at "
                    + transactionPlan.transactionJournal().getRecoveryLocation() + ".";
            StorageFailureMapper.log("Rollback failed while restoring storage.", rollbackError);
            activeStorage.markInconsistent();
            archiveStorage.markInconsistent();
            activeStorage.setPersistenceState(PersistenceState.UNKNOWN);
            archiveStorage.setPersistenceState(PersistenceState.UNKNOWN);
            return new RollbackResult(recoveryMessage, false);
        }
    }

    /** Removes transaction files once a transaction no longer needs recovery. */
    private void cleanUpTransaction(StorageTransactionPlan transactionPlan) {
        if (transactionPlan != null) {
            transactionPlan.transactionJournal().cleanUp();
        }
    }

    /** Adds the operation-specific prefix to a persistence failure. */
    private HertaException withFailurePrefix(String reason) {
        String safeReason = reason == null || reason.isBlank()
                ? "unknown persistence error" : reason;
        return new HertaException(failurePrefix + safeReason);
    }
}
