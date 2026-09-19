package nudge.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nudge.exception.NudgeException;
import nudge.task.Deadline;
import nudge.task.Event;
import nudge.task.Task;
import nudge.task.Todo;

/**
 * Tests loading and saving tasks using isolated temporary files.
 */
class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void load_missingFile_returnsEmptyList() throws IOException, NudgeException {
        Path storagePath = temporaryDirectory.resolve("data/nudge.txt");

        assertEquals(List.of(), Storage.load(storagePath));
    }

    @Test
    void saveAndLoad_mixedTasks_preservesTaskData() throws IOException, NudgeException {
        Path storagePath = temporaryDirectory.resolve("data/nudge.txt");
        Todo todo = new Todo("read | review notes");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 20));
        Event event = new Event("project meeting", LocalDate.of(2026, 9, 21),
                LocalDate.of(2026, 9, 22));
        deadline.markAsDone();

        Storage.save(List.of(todo, deadline, event), storagePath);
        List<Task> loadedTasks = Storage.load(storagePath);

        assertEquals(List.of(todo.toString(), deadline.toString(), event.toString()),
                loadedTasks.stream().map(Task::toString).toList());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertInstanceOf(Event.class, loadedTasks.get(2));
    }

    @Test
    void save_existingFile_replacesOldContent() throws IOException, NudgeException {
        Path storagePath = temporaryDirectory.resolve("data/nudge.txt");
        Storage.save(List.of(new Todo("old task")), storagePath);

        Storage.save(List.of(new Todo("new task")), storagePath);

        assertEquals(List.of("[T][ ] new task"), Storage.load(storagePath).stream()
                .map(Task::toString).toList());
    }

    @Test
    void load_blankLines_ignoresBlankLines() throws IOException, NudgeException {
        Path storagePath = temporaryDirectory.resolve("data/nudge.txt");
        Files.createDirectories(storagePath.getParent());
        Files.writeString(storagePath, "\nT|0|cmVhZCBib29r\n\n", StandardCharsets.UTF_8);

        assertEquals(List.of("[T][ ] read book"), Storage.load(storagePath).stream()
                .map(Task::toString).toList());
    }

    @Test
    void load_invalidCurrentFormat_exceptionThrown() throws IOException {
        Path storagePath = temporaryDirectory.resolve("data/nudge.txt");
        Files.createDirectories(storagePath.getParent());
        Files.writeString(storagePath, "T|0|not-base64!\n", StandardCharsets.UTF_8);

        NudgeException exception = assertThrows(
                NudgeException.class, () -> Storage.load(storagePath));

        assertEquals("The saved task list is invalid.", exception.getMessage());
    }

    @Test
    void load_legacyFormat_preservesTaskData() throws IOException, NudgeException {
        Path storagePath = temporaryDirectory.resolve("data/nudge.txt");
        Files.createDirectories(storagePath.getParent());
        String savedTasks = "[T][ ] read book\n"
                + "[D][X] submit report (by: 2026-09-20)\n"
                + "[E][ ] meeting (from: Sep 21 2026 to: Sep 22 2026)\n";
        Files.writeString(storagePath, savedTasks, StandardCharsets.UTF_8);

        assertEquals(List.of(
                "[T][ ] read book",
                "[D][X] submit report (by: Sep 20 2026)",
                "[E][ ] meeting (from: Sep 21 2026 to: Sep 22 2026)"),
                Storage.load(storagePath).stream().map(Task::toString).toList());
    }

    @Test
    void load_invalidLegacyStatus_exceptionThrown() throws IOException {
        Path storagePath = temporaryDirectory.resolve("data/nudge.txt");
        Files.createDirectories(storagePath.getParent());
        Files.writeString(storagePath, "[T][?] read book\n", StandardCharsets.UTF_8);

        NudgeException exception = assertThrows(
                NudgeException.class, () -> Storage.load(storagePath));

        assertEquals("The saved task list is invalid.", exception.getMessage());
    }
}
