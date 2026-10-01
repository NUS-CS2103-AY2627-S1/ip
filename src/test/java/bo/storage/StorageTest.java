package bo.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bo.command.CommandException;
import bo.task.Deadline;
import bo.task.Event;
import bo.task.Task;
import bo.task.Todo;

/** Tests loading and saving the persistent task format. */
public class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    public void saveAndLoad_allTaskTypes_preservesDetailsAndStatus() throws Exception {
        Storage storage = new Storage(tempDir.resolve("bo.txt"));
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2999, 1, 1));
        Event event = new Event("team meeting",
                LocalDateTime.of(2026, 10, 10, 14, 0),
                LocalDateTime.of(2026, 10, 10, 16, 0));
        deadline.markAsDone();

        storage.save(List.of(todo, deadline, event));

        List<Task> loadedTasks = storage.load();
        assertEquals(List.of(todo.toString(), deadline.toString(), event.toString()),
                loadedTasks.stream().map(Task::toString).toList());
    }

    @Test
    public void load_fileWithBlankLines_ignoresBlankLines() throws IOException, CommandException {
        Path saveFile = tempDir.resolve("bo.txt");
        Files.writeString(saveFile, "\nT | 0 | read book\n  \n", StandardCharsets.UTF_8);

        List<Task> loadedTasks = new Storage(saveFile).load();

        assertEquals(1, loadedTasks.size());
        assertEquals("[T][ ] read book", loadedTasks.get(0).toString());
    }

    @Test
    public void load_fileWithInvalidTaskType_throwsCommandException() throws IOException {
        Path saveFile = writeSaveFile("X | 0 | unknown");

        assertThrows(CommandException.class, () -> new Storage(saveFile).load());
    }

    @Test
    public void load_fileWithInvalidStatus_throwsCommandException() throws IOException {
        Path saveFile = writeSaveFile("T | 2 | read book");

        assertThrows(CommandException.class, () -> new Storage(saveFile).load());
    }

    @Test
    public void load_deadlineWithInvalidDate_throwsCommandException() throws IOException {
        Path saveFile = writeSaveFile("D | 0 | submit report | not-a-date");

        assertThrows(CommandException.class, () -> new Storage(saveFile).load());
    }

    @Test
    public void load_taskWithMissingField_throwsCommandException() throws IOException {
        Path saveFile = writeSaveFile("E | 0 | team meeting | 2pm");

        assertThrows(CommandException.class, () -> new Storage(saveFile).load());
    }

    @Test
    public void save_emptyTaskList_createsEmptySaveFile() throws Exception {
        Path saveFile = tempDir.resolve("bo.txt");
        Storage storage = new Storage(saveFile);

        storage.save(List.of());

        assertEquals(List.of(), Files.readAllLines(saveFile, StandardCharsets.UTF_8));
    }

    @Test
    public void save_calledTwice_replacesPreviousContents() throws Exception {
        Path saveFile = tempDir.resolve("bo.txt");
        Storage storage = new Storage(saveFile);
        storage.save(List.of(new Todo("old task")));

        storage.save(List.of(new Todo("new task")));

        assertEquals(List.of("T | 0 | new task"),
                Files.readAllLines(saveFile, StandardCharsets.UTF_8));
    }

    private Path writeSaveFile(String content) throws IOException {
        Path saveFile = tempDir.resolve("bo.txt");
        Files.writeString(saveFile, content, StandardCharsets.UTF_8);
        return saveFile;
    }
}
