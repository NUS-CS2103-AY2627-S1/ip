package yuno.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import yuno.exception.FileStorageException;
import yuno.exception.InvalidTaskNumberException;
import yuno.task.Deadline;
import yuno.task.Event;
import yuno.task.TaskList;
import yuno.task.Todo;

class StorageTest {
    @TempDir
    private Path tempDir;

    @Test
    void constructor_missingParentAndFile_createsStorageFile() throws FileStorageException {
        Path filePath = tempDir.resolve("nested/data/tasks.txt");

        new Storage(filePath);

        assertTrue(Files.isRegularFile(filePath));
    }

    @Test
    void constructor_existingFile_preservesContents() throws IOException, FileStorageException {
        Path filePath = tempDir.resolve("tasks.txt");
        Files.writeString(filePath, "existing data");

        new Storage(filePath);

        assertEquals("existing data", Files.readString(filePath));
    }

    @Test
    void constructor_filePathIsDirectory_throwsFileStorageException() throws IOException {
        Path directoryPath = Files.createDirectory(tempDir.resolve("tasks.txt"));

        assertThrows(FileStorageException.class, () -> new Storage(directoryPath));
    }

    @Test
    void constructor_parentPathIsFile_throwsFileStorageException() throws IOException {
        Path parentFile = Files.createFile(tempDir.resolve("not-a-directory"));

        FileStorageException exception = assertThrows(
                FileStorageException.class, () -> new Storage(parentFile.resolve("tasks.txt")));

        assertEquals(
                "I can't initialize your task file. Check the data folder before bothering me again.",
                exception.getMessage());
    }

    @Test
    void saveAndLoad_allTaskTypes_preservesTaskData()
            throws FileStorageException, InvalidTaskNumberException, IOException {
        Path filePath = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(filePath);
        TaskList originalTasks = new TaskList();
        originalTasks.addTask(new Todo("read | book", true));
        originalTasks.addTask(new Deadline(
                "submit | report", false, LocalDateTime.of(2026, 8, 30, 18, 0)));
        originalTasks.addTask(new Event(
                "project | meeting",
                true,
                LocalDateTime.of(2026, 8, 31, 9, 0),
                LocalDateTime.of(2026, 8, 31, 10, 30)));

        storage.save(originalTasks);
        TaskList loadedTasks = new TaskList();
        storage.load(loadedTasks);

        assertEquals(3, loadedTasks.getCount());
        assertInstanceOf(Todo.class, loadedTasks.getTask(1));
        assertInstanceOf(Deadline.class, loadedTasks.getTask(2));
        assertInstanceOf(Event.class, loadedTasks.getTask(3));
        for (int index = 1; index <= originalTasks.getCount(); index++) {
            assertEquals(
                    originalTasks.getTask(index).toStorageString(),
                    loadedTasks.getTask(index).toStorageString());
        }
        assertEquals(List.of(
                "T | X | read | book",
                "D |   | submit | report | Aug 30 2026, 06:00 PM",
                "E | X | project | meeting | Aug 31 2026, 09:00 AM | Aug 31 2026, 10:30 AM"),
                Files.readAllLines(filePath));
    }

    @Test
    void save_emptyTaskList_truncatesStorageFile()
            throws FileStorageException, InvalidTaskNumberException, IOException {
        Path filePath = tempDir.resolve("tasks.txt");
        Files.writeString(filePath, "old task data");
        Storage storage = new Storage(filePath);

        storage.save(new TaskList());

        assertTrue(Files.readString(filePath).isEmpty());
    }

    @Test
    void save_success_leavesNoTemporaryFiles()
            throws FileStorageException, InvalidTaskNumberException, IOException {
        Storage storage = new Storage(tempDir.resolve("tasks.txt"));
        TaskList tasks = new TaskList();
        tasks.addTask("read book");

        storage.save(tasks);

        try (var files = Files.list(tempDir)) {
            assertFalse(files.anyMatch(path -> path.getFileName().toString().startsWith("yuno-")));
        }
    }

    @Test
    void saveAndLoad_unicodeAndLongDescription_preservesText()
            throws FileStorageException, InvalidTaskNumberException {
        Storage storage = new Storage(tempDir.resolve("tasks.txt"));
        String description = "完成报告 🚀 " + "long ".repeat(200);
        TaskList originalTasks = new TaskList();
        originalTasks.addTask(description);

        storage.save(originalTasks);
        TaskList loadedTasks = new TaskList();
        storage.load(loadedTasks);

        assertEquals(description, loadedTasks.getTask(1).getDescription());
    }

    @Test
    void load_malformedLines_throwsFileStorageException() throws IOException, FileStorageException {
        Path filePath = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(filePath);
        List<String> malformedLines = List.of(
                "T",
                "T | ? | task",
                "Z |   | task",
                "T |   | ",
                "D |   | task | invalid date",
                "D |   | task | Feb 30 2026, 10:30 AM",
                "D |   | task | Aug 31 2026, 13:60 PM",
                "E |   | event | Aug 31 2026, 10:30 AM | Aug 31 2026, 09:00 AM",
                "E |   | event | Aug 31 2026, 10:30 AM | Aug 31 2026, 10:30 AM");

        for (String malformedLine : malformedLines) {
            Files.writeString(filePath, malformedLine);
            FileStorageException exception = assertThrows(
                    FileStorageException.class, () -> storage.load(new TaskList()));
            assertEquals(
                    "Why did you change the task file? I can't load line 1.",
                    exception.getMessage());
        }
    }

    @Test
    void load_validLineBeforeMalformedLine_doesNotPartiallyChangeTaskList()
            throws IOException, FileStorageException, InvalidTaskNumberException {
        Path filePath = tempDir.resolve("tasks.txt");
        Files.writeString(filePath, "T |   | valid task\nT | ? | corrupted task\n");
        Storage storage = new Storage(filePath);
        TaskList tasks = new TaskList();
        tasks.addTask("existing task");

        FileStorageException exception = assertThrows(
                FileStorageException.class, () -> storage.load(tasks));

        assertEquals("Why did you change the task file? I can't load line 2.", exception.getMessage());
        assertEquals(1, tasks.getCount());
        assertEquals("existing task", tasks.getTask(1).getDescription());
    }

    @Test
    void load_malformedThirdLine_reportsLineThreeAndPreservesExistingTasks()
            throws IOException, FileStorageException, InvalidTaskNumberException {
        Path filePath = tempDir.resolve("tasks.txt");
        Files.writeString(
                filePath,
                "T |   | first task\nT | X | second task\nD |   | broken | invalid date\n");
        Storage storage = new Storage(filePath);
        TaskList tasks = new TaskList();
        tasks.addTask("existing task");

        FileStorageException exception = assertThrows(
                FileStorageException.class, () -> storage.load(tasks));

        assertEquals("Why did you change the task file? I can't load line 3.", exception.getMessage());
        assertEquals(1, tasks.getCount());
        assertEquals("existing task", tasks.getTask(1).getDescription());
    }

    @Test
    void load_emptyFile_replacesExistingTasksWithEmptyList()
            throws FileStorageException {
        Storage storage = new Storage(tempDir.resolve("tasks.txt"));
        TaskList tasks = new TaskList();
        tasks.addTask("existing task");

        storage.load(tasks);

        assertEquals(0, tasks.getCount());
    }

    @Test
    void load_validFile_replacesExistingTasksInsteadOfAppending()
            throws IOException, FileStorageException, InvalidTaskNumberException {
        Path filePath = tempDir.resolve("tasks.txt");
        Files.writeString(filePath, "T |   | loaded task\n");
        Storage storage = new Storage(filePath);
        TaskList tasks = new TaskList();
        tasks.addTask("existing task");

        storage.load(tasks);

        assertEquals(1, tasks.getCount());
        assertEquals("loaded task", tasks.getTask(1).getDescription());
    }

    @Test
    void load_missingFile_throwsFileStorageException() throws IOException, FileStorageException {
        Path filePath = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(filePath);
        Files.delete(filePath);

        FileStorageException exception = assertThrows(
                FileStorageException.class, () -> storage.load(new TaskList()));

        assertEquals(
                "I can't read your task file. Did you move it while I wasn't looking?",
                exception.getMessage());
    }

    @Test
    void save_filePathBecomesDirectory_throwsFileStorageException()
            throws IOException, FileStorageException {
        Path filePath = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(filePath);
        Files.delete(filePath);
        Files.createDirectory(filePath);

        FileStorageException exception = assertThrows(
                FileStorageException.class, () -> storage.save(new TaskList()));

        assertEquals(
                "I can't save your tasks. Check the task file before bothering me again.",
                exception.getMessage());
        try (var files = Files.list(tempDir)) {
            assertFalse(files.anyMatch(path -> path.getFileName().toString().startsWith("yuno-")));
        }
    }
}
