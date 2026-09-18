package herta.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.io.TempDir;

import herta.exception.HertaException;
import herta.task.TaskList;
import herta.task.Todo;

/**
 * Tests two-file transaction coordination, recovery, rollback, and path conflicts.
 */
class StorageTransactionTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void saveActiveAndArchivedTasks_successUpdatesBothFilesAndSnapshots() throws Exception {
        Path activeFile = temporaryDirectory.resolve("transaction").resolve("tasks.txt");
        Path archiveFile = activeFile.resolveSibling("archive.txt");
        Storage activeStorage = new Storage(activeFile.toString());
        Storage archiveStorage = new Storage(archiveFile.toString());

        activeStorage.saveActiveAndArchivedTasks(archiveStorage,
                new TaskList(List.of(new Todo("café"))),
                new TaskList(List.of(new Todo("归档"))), "Failed to save transaction: ");

        assertEquals(PersistenceState.COMMITTED, activeStorage.getLastPersistenceState());
        assertEquals(PersistenceState.COMMITTED, archiveStorage.getLastPersistenceState());
        assertEquals(List.of("T | 0 | café"), Files.readAllLines(activeFile));
        assertEquals(List.of("T | 0 | 归档"), Files.readAllLines(archiveFile));
        assertTrue(Files.notExists(activeFile.resolveSibling(".herta-transaction")));
        StorageTestSupport.assertNoTransactionFiles(activeFile.getParent());

        activeStorage.save(new TaskList(List.of(new Todo("later"))));

        assertEquals(List.of("T | 0 | later"), Files.readAllLines(activeFile));
    }

    @Test
    void startup_recoversPreparedArchiveTransactionFromBackups() throws Exception {
        Path activeFile = temporaryDirectory.resolve("recover-active.txt");
        Path archiveFile = activeFile.resolveSibling("archive.txt");
        Files.writeString(activeFile, "T | 0 | original active\n");
        Files.writeString(archiveFile, "T | 0 | original archive\n");
        StorageFileManager fileManager = new StorageFileManager(activeFile);
        StorageFileManager archiveManager = new StorageFileManager(archiveFile);

        StorageTransactionJournal.prepare(activeFile, archiveFile, fileManager.captureSnapshot(),
                archiveManager.captureSnapshot());
        Files.writeString(activeFile, "T | 0 | incomplete active\n");
        Files.writeString(archiveFile, "T | 0 | incomplete archive\n");

        TaskList recoveredTasks = TaskRepository.load(activeFile.toString()).getActiveTasks();

        assertEquals("original active", recoveredTasks.get(0).getDescription());
        assertEquals("original archive", new Storage(archiveFile.toString()).loadArchived()
                .get(0).getDescription());
        assertTrue(Files.notExists(activeFile.resolveSibling(".herta-transaction")));
    }

    @Test
    void startup_malformedTransactionJournal_preservesFilesAndDisablesRecovery() throws Exception {
        Path activeFile = temporaryDirectory.resolve("journal-active.txt");
        Path archiveFile = activeFile.resolveSibling("archive.txt");
        Files.writeString(activeFile, "T | 0 | original active\n");
        Files.writeString(archiveFile, "T | 0 | original archive\n");
        Files.writeString(activeFile.resolveSibling(".herta-transaction"), "phase=UNKNOWN\n");

        HertaException exception = assertThrows(HertaException.class, () ->
                TaskRepository.load(activeFile.toString()));

        assertEquals("Failed to recover interrupted storage transaction safely.",
                exception.getMessage());
        assertEquals("T | 0 | original active\n", Files.readString(activeFile));
        assertEquals("T | 0 | original archive\n", Files.readString(archiveFile));
        assertTrue(Files.exists(activeFile.resolveSibling(".herta-transaction")));
    }

    @Test
    void startup_activeAndArchivedDuplicate_allowsHistoricalDuplicate() throws Exception {
        Path activeFile = temporaryDirectory.resolve("duplicate-active.txt");
        Path archiveFile = activeFile.resolveSibling("archive.txt");
        Files.writeString(activeFile, "T | 0 | read book\n");
        Files.writeString(archiveFile, "T | 1 | read book\n");

        TaskRepository repository = TaskRepository.load(activeFile.toString());

        assertEquals(1, repository.getActiveTasks().size());
        assertEquals(1, repository.getArchivedTasks().size());
        assertEquals("read book", repository.getActiveTasks().get(0).getDescription());
        assertEquals("read book", repository.getArchivedTasks().get(0).getDescription());
    }

    @Test
    void saveActiveAndArchivedTasks_failureRestoresOriginalAbsence() throws Exception {
        Path activeFile = temporaryDirectory.resolve("tasks.txt");
        Path archiveDirectory = temporaryDirectory.resolve("archive.txt");
        Files.createDirectory(archiveDirectory);
        Storage activeStorage = new Storage(activeFile.toString());
        Storage archiveStorage = new Storage(archiveDirectory.toString());

        Executable action = () -> activeStorage.saveActiveAndArchivedTasks(archiveStorage,
                new TaskList(), new TaskList(), "Failed to archive tasks: ");
        HertaException exception = assertThrows(HertaException.class, action);

        assertTrue(exception.getMessage().startsWith("Failed to archive tasks: "));
        assertTrue(Files.notExists(activeFile));
        assertTrue(Files.isDirectory(archiveDirectory));
    }

    @Test
    void validateDistinctPaths_rejectsNormalizedConflicts() {
        Path activeFile = temporaryDirectory.resolve("data").resolve("herta.txt");
        Path conflictingArchive = temporaryDirectory.resolve("data").resolve(".").resolve("herta.txt");

        HertaException exception = assertThrows(HertaException.class, () ->
                Storage.validateDistinctPaths(activeFile, conflictingArchive));

        assertTrue(exception.getMessage().startsWith("Failed to load archived tasks: "));
    }

    @Test
    void saveActiveAndArchivedTasks_lockHeldBySameJvm_preservesBothFilesAndStates() throws Exception {
        Path activeFile = temporaryDirectory.resolve("locked-active.txt");
        Path archiveFile = temporaryDirectory.resolve("archive.txt");
        Files.writeString(activeFile, "T | 0 | active\n", StandardCharsets.UTF_8);
        Files.writeString(archiveFile, "T | 1 | archived\n", StandardCharsets.UTF_8);
        Storage activeStorage = new Storage(activeFile.toString());
        Storage archiveStorage = new Storage(archiveFile.toString());
        byte[] originalActiveBytes = Files.readAllBytes(activeFile);
        byte[] originalArchiveBytes = Files.readAllBytes(archiveFile);

        try (StorageFileLock sharedLock = StorageFileLock.acquire(activeFile, archiveFile)) {
            HertaException exception = assertThrows(HertaException.class, () ->
                    activeStorage.saveActiveAndArchivedTasks(archiveStorage,
                            new TaskList(List.of(new Todo("new active"))),
                            new TaskList(List.of(new Todo("new archive"))),
                            "Failed to save transaction: "));

            assertEquals("Failed to save transaction: data file is locked.", exception.getMessage());
            assertEquals(PersistenceState.NOT_ATTEMPTED, activeStorage.getLastPersistenceState());
            assertEquals(PersistenceState.NOT_ATTEMPTED, archiveStorage.getLastPersistenceState());
            assertArrayEquals(originalActiveBytes, Files.readAllBytes(activeFile));
            assertArrayEquals(originalArchiveBytes, Files.readAllBytes(archiveFile));
        }
    }

    @Test
    void validateDistinctPaths_hardLinkedFiles_rejectConflictWhenSupported() throws Exception {
        Path activeFile = temporaryDirectory.resolve("active.txt");
        Path archiveFile = temporaryDirectory.resolve("archive.txt");
        Files.writeString(activeFile, "T | 0 | shared\n", StandardCharsets.UTF_8);
        try {
            Files.createLink(archiveFile, activeFile);
        } catch (UnsupportedOperationException | IOException | SecurityException exception) {
            Assumptions.assumeTrue(false, "Hard links are unavailable on this host.");
            return;
        }

        HertaException exception = assertThrows(HertaException.class, () ->
                Storage.validateDistinctPaths(activeFile, archiveFile));

        assertTrue(exception.getMessage().contains("different files"));
    }
}
