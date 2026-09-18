package mochi.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import mochi.exception.MochiException;
import mochi.task.Deadline;
import mochi.task.Event;
import mochi.task.Task;
import mochi.task.TaskList;
import mochi.task.Todo;

class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    void load_missingFile_returnsEmptyList() throws MochiException {
        Storage storage = new Storage(tempDir.resolve("nested").resolve("tasks.txt"));

        assertTrue(storage.load().isEmpty());
    }

    @Test
    void saveThenLoad_allTaskDetailsAndStatuses_arePreserved() throws MochiException {
        Storage storage = new Storage(tempDir.resolve("nested").resolve("tasks.txt"));
        TaskList originalTasks = new TaskList();
        Todo todo = new Todo("borrow book");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 8, 30));
        Event event = new Event("meeting", "2pm", "4pm");
        deadline.markAsDone();
        originalTasks.add(todo);
        originalTasks.add(deadline);
        originalTasks.add(event);

        storage.save(originalTasks);
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals(todo.toString(), loadedTasks.get(0).toString());
        assertEquals(deadline.toString(), loadedTasks.get(1).toString());
        assertEquals(event.toString(), loadedTasks.get(2).toString());
    }

    @Test
    void loadMalformedRecord_reportsFriendlyError() throws Exception {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | missing | unexpected\n", StandardCharsets.UTF_8);

        MochiException exception = org.junit.jupiter.api.Assertions.assertThrows(
                MochiException.class, () -> new Storage(file).load());

        assertEquals("Saved task data is invalid at line 1.", exception.getMessage());
    }

    @Test
    void saveFileWithoutParentDirectory_succeeds() throws Exception {
        Path file = Files.createTempFile(Path.of("."), "mochi-test-", ".txt").getFileName();
        Storage storage = new Storage(file);
        TaskList tasks = new TaskList();
        tasks.add(new Todo("portable save"));

        try {
            storage.save(tasks);
            assertEquals(1, storage.load().size());
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void loadBlankTaskField_reportsFriendlyError() throws Exception {
        Path file = tempDir.resolve("blank.txt");
        Files.writeString(file, "T | 0 | \n", StandardCharsets.UTF_8);

        MochiException exception = org.junit.jupiter.api.Assertions.assertThrows(
                MochiException.class, () -> new Storage(file).load());

        assertEquals("Saved task data is invalid at line 1.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "X | 0 | unknown", "T | 2 | bad status", "T | 0", "T | 0 |   ",
        "T | 0 | text | extra", "T | 0 | unsafe|text", "T | 0 | unsafe\ttext",
        "D | 0 | report | 2026-02-30", "D | 0 | report", "E | 0 | meeting | noon",
        "E | 0 | meeting |  | evening", "E | 0 | meeting | noon | "})
    void load_invalidRecord_reportsCorrectLineAndPreservesData(String invalidRecord) throws Exception {
        Path file = tempDir.resolve("invalid.txt");
        String contents = "T | 0 | valid\n" + invalidRecord + "\n";
        Files.writeString(file, contents);
        MochiException error = assertThrows(MochiException.class, () -> new Storage(file).load());
        assertEquals("Saved task data is invalid at line 2.", error.getMessage());
        assertEquals(contents, Files.readString(file));
    }

    @Test
    void load_directoryInsteadOfFile_reportsReadError() {
        MochiException error = assertThrows(MochiException.class, () -> new Storage(tempDir).load());
        assertTrue(error.getMessage().startsWith("I couldn't read your saved tasks:"));
    }

    @Test
    void save_parentIsFile_reportsErrorWithoutReplacingParent() throws Exception {
        Path parent = tempDir.resolve("data");
        Files.writeString(parent, "keep this file");
        Storage storage = new Storage(parent.resolve("tasks.txt"));
        assertThrows(MochiException.class, () -> storage.save(new TaskList()));
        assertEquals("keep this file", Files.readString(parent));
    }

    @Test
    void save_unicodeAndEmptyList_roundTripsAndLeavesNoTemporaryFiles() throws Exception {
        Path file = tempDir.resolve("unicode.txt");
        Storage storage = new Storage(file);
        TaskList tasks = new TaskList();
        tasks.add(new Todo("买书 🍡 café"));
        storage.save(tasks);
        assertEquals("买书 🍡 café", storage.load().get(0).getDescription());
        storage.save(new TaskList());
        assertTrue(storage.load().isEmpty());
        try (var files = Files.list(tempDir)) {
            assertEquals(1, files.count());
        }
    }
}
