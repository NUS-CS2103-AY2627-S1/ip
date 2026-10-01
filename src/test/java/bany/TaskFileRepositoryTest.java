package bany;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bany.tags.Tag;
import bany.tasks.Deadline;
import bany.tasks.Task;
import bany.tasks.ToDo;

/** Tests loading and saving directly between {@link TaskFileRepository} and {@link TaskStorage}. */
class TaskFileRepositoryTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void saveThenLoad_tasksAreRestoredIntoStorage() throws IOException {
        TaskStorage savedStorage = new TaskStorage();
        Task todo = new ToDo("Read book", 1);
        Task deadline = new Deadline("Submit report", "21-03-2026 21:03", 2);
        deadline.mark();
        savedStorage.addTask(todo);
        savedStorage.addTask(deadline);
        TaskFileRepository repository = new TaskFileRepository(
                temporaryDirectory.resolve("bany.txt"));

        repository.save(savedStorage);

        TaskStorage loadedStorage = new TaskStorage();
        repository.load(loadedStorage);

        assertEquals(2, loadedStorage.getSize());
        assertEquals("Read book", loadedStorage.getTask(0).get().getDescription());
        assertEquals("Submit report", loadedStorage.getTask(1).get().getDescription());
        assertTrue(loadedStorage.getTask(1).get().isDone());
    }

    @Test
    void load_missingFile_replacesStorageWithNoTasks() throws IOException {
        TaskStorage taskStorage = new TaskStorage();
        taskStorage.addTask(new ToDo("Old task", 1));
        TaskFileRepository repository = new TaskFileRepository(
                temporaryDirectory.resolve("missing.txt"));

        repository.load(taskStorage);

        assertEquals(0, taskStorage.getSize());
        assertFalse(temporaryDirectory.resolve("missing.txt").toFile().exists());
    }

    @Test
    void load_existingFile_reconstructsTasksInFileOrder() throws IOException {
        Path taskFile = temporaryDirectory.resolve("bany.txt");
        Files.writeString(taskFile, """
                [{"id":99,"type":"TODO","description":"Read book","done":false},
                 {"id":42,"type":"DEADLINE","description":"Submit report","done":true,
                 "by":"21-03-2026 21:03"}]
                """);
        TaskStorage taskStorage = new TaskStorage();
        TaskFileRepository repository = new TaskFileRepository(taskFile);

        repository.load(taskStorage);

        assertEquals(2, taskStorage.getSize());
        assertEquals(1, taskStorage.getTask(0).get().getId());
        assertEquals("Read book", taskStorage.getTask(0).get().getDescription());
        assertEquals(2, taskStorage.getTask(1).get().getId());
        assertEquals("Submit report", taskStorage.getTask(1).get().getDescription());
        assertTrue(taskStorage.getTask(1).get().isDone());
    }

    @Test
    void saveThenLoad_preservesGenericTags() throws IOException {
        TaskStorage savedStorage = new TaskStorage();
        Task todo = new ToDo("Read book", 1);
        todo.updateTags(List.of(new Tag("urgent"), new Tag("priority", "high")));
        savedStorage.addTask(todo);
        TaskFileRepository repository = new TaskFileRepository(
                temporaryDirectory.resolve("bany.txt"));

        repository.save(savedStorage);

        TaskStorage loadedStorage = new TaskStorage();
        repository.load(loadedStorage);

        assertEquals(todo.getTags(), loadedStorage.getTask(0).get().getTags());
    }

    @Test
    void loadWithRecovery_validFile_loadsWithoutCreatingReport() throws IOException {
        Path taskFile = temporaryDirectory.resolve("bany.txt");
        String content = "[{\"type\":\"TODO\",\"description\":\"Read book\"}]";
        Files.writeString(taskFile, content);
        Path reportDirectory = temporaryDirectory.resolve("report");
        TaskStorage storage = new TaskStorage();

        assertFalse(new TaskFileRepository(taskFile).loadWithRecovery(storage, reportDirectory));

        assertEquals("Read book", storage.getTask(0).orElseThrow().getDescription());
        assertEquals(content, Files.readString(taskFile));
        assertFalse(Files.exists(reportDirectory));
    }

    @Test
    void loadWithRecovery_missingFile_startsNormallyWithoutWarning() throws IOException {
        TaskStorage storage = new TaskStorage();
        Path reportDirectory = temporaryDirectory.resolve("report");
        TaskFileRepository repository = new TaskFileRepository(temporaryDirectory.resolve("bany.txt"));

        assertFalse(repository.loadWithRecovery(storage, reportDirectory));
        assertEquals(0, storage.getSize());
        assertFalse(Files.exists(reportDirectory));
    }

    @Test
    void loadWithRecovery_invalidFiles_archivesEveryOriginalAndCreatesEmptyFile() throws IOException {
        Path dataDirectory = Files.createDirectory(temporaryDirectory.resolve("data"));
        Path taskFile = dataDirectory.resolve("bany.txt");
        Path reportDirectory = dataDirectory.resolve("report");
        TaskFileRepository repository = new TaskFileRepository(taskFile);
        List<String> invalidContents = List.of(
                "not JSON",
                "[{\"type\":\"UNKNOWN\",\"description\":\"Read book\"}]",
                "[{\"type\":\"TODO\",\"description\":\"Valid task\"},"
                        + "{\"type\":\"DEADLINE\",\"description\":\"Invalid task\","
                        + "\"by\":\"30-02-2026 12:00\"}]",
                "{\"unexpected\":true}");
        TaskStorage storage = new TaskStorage();

        for (String content : invalidContents) {
            storage.addTask(new ToDo("Old task", 99));
            Files.writeString(taskFile, content);

            assertTrue(repository.loadWithRecovery(storage, reportDirectory));

            assertEquals(0, storage.getSize());
            assertEquals(1, Task.allocateId());
            assertEquals("[]", Files.readString(taskFile));
            repository.load(storage);
            assertEquals(0, storage.getSize());
        }

        try (var reports = Files.list(reportDirectory)) {
            List<Path> archivedFiles = reports.toList();
            assertEquals(invalidContents.size(), archivedFiles.size());
            for (Path archivedFile : archivedFiles) {
                assertTrue(invalidContents.contains(Files.readString(archivedFile)));
            }
        }
    }

    @Test
    void loadWithRecovery_reportCannotBeCreated_preservesOriginalFile() throws IOException {
        Path taskFile = temporaryDirectory.resolve("bany.txt");
        Files.writeString(taskFile, "corrupted data");
        Path reportDirectory = temporaryDirectory.resolve("report");
        Files.writeString(reportDirectory, "Existing file blocks report directory");
        TaskStorage storage = new TaskStorage();
        storage.addTask(new ToDo("Old task", 99));

        assertThrows(IOException.class, () ->
                new TaskFileRepository(taskFile).loadWithRecovery(storage, reportDirectory));

        assertEquals("corrupted data", Files.readString(taskFile));
        assertEquals("Existing file blocks report directory", Files.readString(reportDirectory));
        assertEquals(0, storage.getSize());
        assertEquals(1, Task.allocateId());
    }

    @Test
    void loadWithRecovery_taskPathIsDirectory_doesNotMoveDirectory() throws IOException {
        Path taskFile = Files.createDirectory(temporaryDirectory.resolve("bany.txt"));
        Path existingFile = taskFile.resolve("keep.txt");
        Files.writeString(existingFile, "Keep this file");

        assertThrows(IOException.class, () -> new TaskFileRepository(taskFile).loadWithRecovery(
                new TaskStorage(), temporaryDirectory.resolve("report")));

        assertEquals("Keep this file", Files.readString(existingFile));
    }
}
