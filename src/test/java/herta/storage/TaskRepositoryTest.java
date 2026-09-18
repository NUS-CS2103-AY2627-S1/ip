package herta.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import herta.exception.HertaException;
import herta.task.TaskList;
import herta.task.Todo;

/**
 * Tests loading and adapting Herta's active and archived task collections.
 */
class TaskRepositoryTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_bothFilesMissing_returnsTwoEmptyCollections() throws Exception {
        Path activeFile = temporaryDirectory.resolve("data").resolve("tasks.txt");

        TaskRepository repository = TaskRepository.load(activeFile.toString());

        assertEquals(0, repository.getActiveTasks().size());
        assertEquals(0, repository.getArchivedTasks().size());
    }

    @Test
    void load_validActiveAndInvalidArchive_reportsArchiveFailure() throws Exception {
        Path activeFile = temporaryDirectory.resolve("tasks.txt");
        Path archiveFile = activeFile.resolveSibling("archive.txt");
        Files.write(activeFile, List.of("T | 0 | active"), StandardCharsets.UTF_8);
        Files.write(archiveFile, List.of("X | 0 | invalid"), StandardCharsets.UTF_8);

        HertaException exception = assertThrows(HertaException.class, () ->
                TaskRepository.load(activeFile.toString()));

        assertTrue(exception.getMessage().startsWith("Failed to load archived tasks at line 1:"));
    }

    @Test
    void load_invalidActiveAndValidArchive_reportsActiveFailureFirst() throws Exception {
        Path activeFile = temporaryDirectory.resolve("tasks.txt");
        Path archiveFile = activeFile.resolveSibling("archive.txt");
        Files.write(activeFile, List.of("X | 0 | invalid"), StandardCharsets.UTF_8);
        Files.write(archiveFile, List.of("T | 0 | archive"), StandardCharsets.UTF_8);

        HertaException exception = assertThrows(HertaException.class, () ->
                TaskRepository.load(activeFile.toString()));

        assertTrue(exception.getMessage().startsWith("Failed to load tasks at line 1:"));
    }

    @Test
    void load_duplicateRecords_preservesBothCollections() throws Exception {
        Path activeFile = temporaryDirectory.resolve("tasks.txt");
        Path archiveFile = activeFile.resolveSibling("archive.txt");
        Files.write(activeFile, List.of("T | 0 | repeated", "T | 0 | repeated"), StandardCharsets.UTF_8);
        Files.write(archiveFile, List.of("T | 1 | repeated"), StandardCharsets.UTF_8);

        TaskRepository repository = TaskRepository.load(activeFile.toString());

        assertEquals(2, repository.getActiveTasks().size());
        assertEquals(1, repository.getArchivedTasks().size());
        assertTrue(repository.getArchivedTasks().get(0).isCompleted());
    }

    @Test
    void createWithActiveTasks_suppliedCollectionIsRetainedAndArchiveIsLoaded() throws Exception {
        Path activeFile = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Path archiveFile = activeFile.resolveSibling("archive.txt");
        TaskList activeTasks = new TaskList(List.of(new Todo("active")));
        Files.createDirectories(archiveFile.getParent());
        Files.write(archiveFile, List.of("T | 0 | archived"), StandardCharsets.UTF_8);

        TaskRepository repository = TaskRepository.createWithActiveTasks(activeTasks,
                new Storage(activeFile.toString()));

        assertSame(activeTasks, repository.getActiveTasks());
        assertEquals(1, repository.getArchivedTasks().size());
        assertEquals("archived", repository.getArchivedTasks().get(0).getDescription());
        assertEquals(1, activeTasks.size());
    }

    @Test
    void load_activeArchivePathConflict_rejectsNormalizedPaths() throws Exception {
        Path activeFile = temporaryDirectory.resolve("archive.txt");

        HertaException exception = assertThrows(HertaException.class, () ->
                TaskRepository.load(activeFile.toString()));

        assertTrue(exception.getMessage().contains("active and archive paths must be different"));
    }

    @Test
    void resetPersistenceState_resetsActiveAndArchiveStorageOutcomes() throws Exception {
        Storage activeStorage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        Storage archiveStorage = new Storage(temporaryDirectory.resolve("archive.txt").toString());
        TaskRepository repository = new TaskRepository(activeStorage, archiveStorage,
                new TaskList(), new TaskList());
        activeStorage.save(new TaskList());
        archiveStorage.save(new TaskList());

        repository.resetPersistenceState();

        assertEquals(PersistenceState.NOT_ATTEMPTED, activeStorage.getLastPersistenceState());
        assertEquals(PersistenceState.NOT_ATTEMPTED, archiveStorage.getLastPersistenceState());
    }
}
