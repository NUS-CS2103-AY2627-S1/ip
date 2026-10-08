package rudra.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import rudra.exception.RudraException;
import rudra.task.Deadline;
import rudra.task.Event;
import rudra.task.Task;
import rudra.task.TaskDateTime;
import rudra.task.ToDo;

/**
 * Tests for {@link Storage}.
 */
public class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    public void loadResult_negativeSkippedTaskCount_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Storage.LoadResult(new ArrayList<>(), -1));
    }

    @Test
    public void loadTasks_missingFile_returnsEmptyResult() throws RudraException {
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());

        Storage.LoadResult loadResult = storage.loadTasks();

        assertEquals(0, loadResult.getTasks().size());
        assertEquals(0, loadResult.getSkippedTaskCount());
    }

    @Test
    public void saveTasks_thenLoadTasks_roundTripsTaskData() throws RudraException {
        Path saveFile = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(saveFile.toString());
        ToDo todo = new ToDo("borrow book");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2026-08-28"));
        Event event = new Event("project | sync", TaskDateTime.parse("2026-08-28 1400"),
                TaskDateTime.parse("2026-08-28 1600"));
        List<Task> tasks = List.of(todo, deadline, event);

        storage.saveTasks(tasks);
        Storage.LoadResult loadResult = storage.loadTasks();

        assertEquals(List.of(
                "[T][X] borrow book",
                "[D][ ] return book (by: Aug 28 2026)",
                "[E][ ] project | sync (from: Aug 28 2026 2:00pm to: Aug 28 2026 4:00pm)"),
                loadResult.getTasks().stream().map(Task::toString).toList());
        assertEquals(0, loadResult.getSkippedTaskCount());
    }

    @Test
    public void saveTasks_taskContainsSeparator_writesEscapedFields() throws RudraException, IOException {
        Path saveFile = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(saveFile.toString());
        List<Task> tasks = List.of(new ToDo("revise | review notes"));

        storage.saveTasks(tasks);

        assertEquals("T | 0 | revise \\| review notes" + System.lineSeparator(), Files.readString(saveFile));
    }

    @Test
    public void loadTasks_corruptedLines_skipsOnlyInvalidRecords() throws IOException, RudraException {
        Path saveFile = tempDir.resolve("tasks.txt");
        Files.writeString(saveFile,
                String.join(System.lineSeparator(),
                        "T | 1 | read book",
                        "INVALID",
                        "D | 0 | return book | 2026-08-28",
                        "E | 2 | project meeting | 2026-08-28 1400 | 2026-08-28 1600")
                        + System.lineSeparator());
        Storage storage = new Storage(saveFile.toString());

        Storage.LoadResult loadResult = storage.loadTasks();

        assertEquals(List.of(
                "[T][X] read book",
                "[D][ ] return book (by: Aug 28 2026)"),
                loadResult.getTasks().stream().map(Task::toString).toList());
        assertEquals(2, loadResult.getSkippedTaskCount());
    }

    @Test
    public void loadTasks_malformedTaskDetails_skipsEachInvalidTaskType() throws IOException, RudraException {
        Path saveFile = tempDir.resolve("tasks.txt");
        Files.writeString(saveFile,
                String.join(System.lineSeparator(),
                        "T | 0 | ",
                        "D | 0 | return book",
                        "E | 0 | project meeting | 2026-08-28 1400",
                        "T | 0 | read book")
                        + System.lineSeparator());
        Storage storage = new Storage(saveFile.toString());

        Storage.LoadResult loadResult = storage.loadTasks();

        assertEquals(List.of("[T][ ] read book"),
                loadResult.getTasks().stream().map(Task::toString).toList());
        assertEquals(3, loadResult.getSkippedTaskCount());
    }
}
