package sace;

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
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests saving tasks to disk and loading them back into the application.
 */
class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    void saveAndLoad_allTaskTypes_preservesTaskData() throws SaceException {
        Path dataFile = tempDir.resolve("data").resolve("sace.txt");
        Storage storage = new Storage(dataFile);

        Todo todo = new Todo("read | chapter \\ notes\nreview\rplan");
        todo.markAsDone();
        Deadline deadline = new Deadline(
                "submit report", LocalDate.of(2026, 9, 15));
        Event event = new Event(
                "project meeting", "Aug | 31 at 2pm", "Aug \\ 31 at 4pm\nsharp");

        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());

        Todo loadedTodo = assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals("read | chapter \\ notes\nreview\rplan", loadedTodo.getDescription());
        assertTrue(loadedTodo.isDone());

        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals("submit report", loadedDeadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 15), loadedDeadline.getBy());
        assertFalse(loadedDeadline.isDone());

        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("project meeting", loadedEvent.getDescription());
        assertEquals("Aug | 31 at 2pm", loadedEvent.getFrom());
        assertEquals("Aug \\ 31 at 4pm\nsharp", loadedEvent.getTo());
        assertFalse(loadedEvent.isDone());
    }

    @Test
    void load_missingDataFile_returnsEmptyTaskList() throws SaceException {
        Storage storage = new Storage(tempDir.resolve("missing").resolve("sace.txt"));

        List<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    void load_corruptedTaskData_throwsExceptionWithLineNumber() throws IOException {
        Path dataFile = tempDir.resolve("sace.txt");
        Files.writeString(
                dataFile, "T | invalid-status | read book", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        SaceException exception = assertThrows(SaceException.class, storage::load);

        assertEquals("The royal archive is damaged at line 1.", exception.getMessage());
    }

    @Test
    void load_blankLines_ignoresBlankLines() throws IOException, SaceException {
        Path dataFile = tempDir.resolve("sace.txt");
        Files.writeString(
                dataFile,
                "\nT | 0 | read book\n   \n",
                StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        List<Task> loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertEquals("read book", loadedTasks.get(0).getDescription());
    }

    @Test
    void load_corruptedTaskShapes_throwsExceptionForEachShape() throws IOException {
        Path dataFile = tempDir.resolve("sace.txt");
        Storage storage = new Storage(dataFile);
        List<String> corruptedLines = List.of(
                "T | 0",
                "X | 0 | unknown type",
                "T | 0 | extra field | unexpected",
                "T | 0 | ",
                "D | 0 | report | 2026-02-30",
                "E | 0 | meeting | Monday | ",
                "T | 0 | invalid\\qescape",
                "T | 0 | unfinished\\");

        for (String corruptedLine : corruptedLines) {
            Files.writeString(dataFile, corruptedLine, StandardCharsets.UTF_8);

            SaceException exception = assertThrows(SaceException.class, storage::load);

            assertEquals("The royal archive is damaged at line 1.", exception.getMessage());
        }
    }

    @Test
    void load_corruptionOnSecondLine_reportsPhysicalLineNumber() throws IOException {
        Path dataFile = tempDir.resolve("sace.txt");
        Files.writeString(
                dataFile,
                "T | 0 | valid task\nD | 0 | broken deadline | not-a-date",
                StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        SaceException exception = assertThrows(SaceException.class, storage::load);

        assertEquals("The royal archive is damaged at line 2.", exception.getMessage());
    }

    @Test
    void save_unknownTaskSubtype_throwsExceptionWithoutCreatingFile() {
        Path dataFile = tempDir.resolve("sace.txt");
        Storage storage = new Storage(dataFile);

        SaceException exception = assertThrows(SaceException.class, () ->
                storage.save(List.of(new Task("plain task"))));

        assertEquals(
                "The royal archive cannot preserve an unknown quest type.",
                exception.getMessage());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void save_existingArchive_replacesContentAndCleansTemporaryFile()
            throws SaceException, IOException {
        Path dataFile = tempDir.resolve("sace.txt");
        Storage storage = new Storage(dataFile);
        storage.save(List.of(new Todo("old task")));

        storage.save(List.of(new Todo("new task")));

        List<Task> loadedTasks = storage.load();
        assertEquals(1, loadedTasks.size());
        assertEquals("new task", loadedTasks.get(0).getDescription());
        try (Stream<Path> files = Files.list(tempDir)) {
            assertFalse(files.anyMatch(path ->
                    path.getFileName().toString().startsWith(".sace-")));
        }
    }
}
