package janet.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import janet.exception.StorageException;
import janet.task.Deadline;
import janet.task.Event;
import janet.task.Task;
import janet.task.TaskList;
import janet.task.Todo;

class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void saveAndLoad_multipleTaskTypes_preservesTaskDetailsAndStatus() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("data/tasks.txt").toString());
        TaskList originalTasks = new TaskList();
        Todo completedTodo = new Todo("read book");
        completedTodo.markAsDone();
        originalTasks.add(completedTodo);
        originalTasks.add(new Deadline("return book", LocalDate.of(2019, 12, 2)));
        originalTasks.add(new Event("project meeting", "14:00", "16:00"));

        storage.save(originalTasks);
        TaskList loadedTasks = new TaskList(storage.load());

        assertEquals(3, loadedTasks.size());
        Task loadedTodo = loadedTasks.get(1);
        assertEquals("read book", loadedTodo.getRawDescription());
        assertTrue(loadedTodo.isDone());
        assertEquals("return book (by: Dec 02 2019)", loadedTasks.get(2).getDescription());
        assertEquals("project meeting (from: 14:00 to: 16:00)", loadedTasks.get(3).getDescription());
        assertEquals(List.of(
                "T\t1\tread book",
                "D\t0\treturn book\t2019-12-02",
                "E\t0\tproject meeting\t14:00\t16:00"), Files.readAllLines(
                        temporaryDirectory.resolve("data/tasks.txt")));
    }

    @Test
    void load_missingFile_returnsEmptyListWithoutWarning() {
        Storage storage = new Storage(temporaryDirectory.resolve("missing/tasks.txt").toString());

        List<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
        assertTrue(storage.getLoadWarnings().isEmpty());
    }

    @Test
    void load_malformedLines_loadsValidTasksAndBlocksSaving() throws IOException {
        Path dataFile = temporaryDirectory.resolve("data/tasks.txt");
        Files.createDirectories(dataFile.getParent());
        String originalData = "T\t0\tread book\ninvalid row\nD\t1\treturn book\t2019-12-02\n";
        Files.writeString(dataFile, originalData);
        Storage storage = new Storage(dataFile.toString());

        List<Task> loadedTasks = storage.load();

        assertEquals(2, loadedTasks.size());
        assertEquals(1, storage.getLoadWarnings().size());
        assertTrue(storage.getLoadWarnings().get(0).contains("line 2"));
        assertThrows(StorageException.class, () -> storage.save(new TaskList(loadedTasks)));
        assertEquals(originalData, Files.readString(dataFile));
    }

    @Test
    void load_allMalformedRowTypes_reportsEveryLineAndProtectsFile() throws IOException {
        Path dataFile = temporaryDirectory.resolve("data/tasks.txt");
        Files.createDirectories(dataFile.getParent());
        String originalData = String.join("\n",
                "T\t0\tvalid task",
                "T\t2\tinvalid status",
                "X\t0\tunknown type",
                "D\t0\tinvalid date\t2019-02-29",
                "E\t0\tmissing end\t14:00",
                "T\t0\t",
                "E\t0\t\t14:00\t16:00",
                "E\t0\tinvalid time\t10:99\t12:00",
                "E\t0\treversed time\t12:00\t11:00") + "\n";
        Files.writeString(dataFile, originalData);
        Storage storage = new Storage(dataFile.toString());

        List<Task> loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertTrue(storage.getLoadWarnings().get(0).contains("lines 2, 3, 4, 5, 6, 7, 8, 9"));
        assertThrows(UnsupportedOperationException.class, () ->
                storage.getLoadWarnings().add("another warning"));
        assertThrows(StorageException.class, () -> storage.save(new TaskList(loadedTasks)));
        assertEquals(originalData, Files.readString(dataFile));
    }

    @Test
    void load_fileFixedAfterWarning_allowsSavingAgain() throws IOException {
        Path dataFile = temporaryDirectory.resolve("data/tasks.txt");
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "invalid row\n");
        Storage storage = new Storage(dataFile.toString());
        storage.load();
        assertThrows(StorageException.class, () -> storage.save(new TaskList()));

        Files.writeString(dataFile, "T\t0\trecovered task\n");
        List<Task> recoveredTasks = storage.load();
        storage.save(new TaskList(recoveredTasks));

        assertTrue(storage.getLoadWarnings().isEmpty());
        assertEquals("T\t0\trecovered task\n", Files.readString(dataFile));
    }

    @Test
    void load_dataPathIsDirectory_warnsAndBlocksSaving() throws IOException {
        Path directoryPath = temporaryDirectory.resolve("tasks-directory");
        Files.createDirectory(directoryPath);
        Storage storage = new Storage(directoryPath.toString());

        List<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
        assertEquals(1, storage.getLoadWarnings().size());
        assertTrue(storage.getLoadWarnings().get(0).contains("couldn't read"));
        assertThrows(StorageException.class, () -> storage.save(new TaskList()));
    }

    @Test
    void save_parentPathIsFile_throwsStorageException() throws IOException {
        Path blockingParent = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(blockingParent, "file blocks directory creation");
        Storage storage = new Storage(blockingParent.resolve("tasks.txt").toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        StorageException exception = assertThrows(StorageException.class, () -> storage.save(tasks));

        assertTrue(exception.getMessage().contains("couldn't save"));
    }
}
