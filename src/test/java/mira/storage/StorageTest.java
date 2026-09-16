package mira.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;

import mira.exception.MiraException;
import mira.task.Deadline;
import mira.task.Event;
import mira.task.Task;
import mira.task.TaskList;
import mira.task.Todo;

class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void load_missingDataPath_createsEmptyFile() throws MiraException {
        Path dataFile = tempDirectory.resolve("nested").resolve("mira.txt");
        Storage storage = new Storage(dataFile);

        TaskList loadedTasks = storage.load();

        assertTrue(loadedTasks.asList().isEmpty());
        assertTrue(Files.isRegularFile(dataFile));
    }

    @Test
    void saveAndLoad_mixedTasks_preservesTaskData() throws MiraException {
        Path dataFile = tempDirectory.resolve("data").resolve("mira.txt");
        Storage storage = new Storage(dataFile);
        TaskList originalTasks = new TaskList();

        Todo todo = new Todo("đọc sách | chương 1\\2");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 8, 28));
        deadline.setDone(true);
        Event event = new Event("demo", "Mon 9am", "Tue 10am");
        originalTasks.add(todo);
        originalTasks.add(deadline);
        originalTasks.add(event);

        storage.save(originalTasks);
        TaskList loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        Task loadedTodo = loadedTasks.asList().get(0);
        assertInstanceOf(Todo.class, loadedTodo);
        assertEquals("đọc sách | chương 1\\2", loadedTodo.getDescription());
        assertFalse(loadedTodo.isDone());

        Deadline loadedDeadline = assertInstanceOf(
                Deadline.class, loadedTasks.asList().get(1));
        assertEquals(LocalDate.of(2026, 8, 28), loadedDeadline.getBy());
        assertTrue(loadedDeadline.isDone());

        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.asList().get(2));
        assertEquals("Mon 9am", loadedEvent.getFrom());
        assertEquals("Tue 10am", loadedEvent.getTo());
    }

    @Test
    void load_invalidEncodedTask_throwsMiraException() throws IOException {
        Path dataFile = tempDirectory.resolve("mira.txt");
        Files.writeString(
                dataFile,
                "D | 0 | not-base64 | also-invalid",
                StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        assertThrows(MiraException.class, storage::load);
    }

    @TestFactory
    Stream<DynamicTest> load_corruptRecords_rejectsAndPreservesOriginal() {
        return Stream.of(
                "T | 2 | dGFzaw==", "X | 0 | dGFzaw==", "T | 0 | ",
                "T | 0 | ICA=", "T | 0 | dGFzaw== | extra", "T | 0",
                "D | 0 | dGFzaw== | MjAyNi0wMi0yOQ==",
                "E | 0 | dGFzaw== | | ", "E | 0 | dGFzaw== | YQ== | IA==")
                .map(record -> DynamicTest.dynamicTest(record, () -> {
                    Path dataFile = tempDirectory.resolve("corrupt.txt");
                    Files.writeString(dataFile, record);
                    assertThrows(MiraException.class, new Storage(dataFile)::load);
                    assertEquals(record, Files.readString(dataFile));
                }));
    }

    @Test
    void load_directoryInsteadOfFile_reportsRecoverableError() throws IOException {
        Path dataDirectory = Files.createDirectory(tempDirectory.resolve("mira.txt"));
        assertThrows(MiraException.class, new Storage(dataDirectory)::load);
    }
}
