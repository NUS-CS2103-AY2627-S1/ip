package coco;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    public void load_missingFile_returnsEmptyList() {
        Storage storage = new Storage(tempDir.resolve("nonexistent/coco.txt").toString());

        List<Task> tasks = storage.load();

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void saveThenLoad_allTaskTypesAndDoneStatus_roundTripsExactly() {
        Storage storage = new Storage(tempDir.resolve("coco.txt").toString());
        List<Task> original = new ArrayList<>();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        original.add(todo);
        original.add(new Deadline("return book", LocalDate.of(2019, 10, 15)));
        original.add(new Event("project meeting", "Mon 2pm", "4pm"));

        storage.save(original);
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", loaded.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", loaded.get(2).toString());
    }

    @Test
    public void save_parentFolderMissing_createsItAutomatically() {
        Path filePath = tempDir.resolve("nested/folder/coco.txt");
        Storage storage = new Storage(filePath.toString());

        storage.save(List.of(new Todo("a task")));

        assertTrue(Files.exists(filePath));
    }

    @Test
    public void load_lineWithUnknownTypeLetter_skipsOnlyThatLine() throws IOException {
        Path filePath = tempDir.resolve("coco.txt");
        Files.writeString(filePath, "T | 1 | fine task\nX | 0 | unknown type\n");
        Storage storage = new Storage(filePath.toString());

        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("[T][X] fine task", loaded.get(0).toString());
    }

    @Test
    public void load_deadlineLineWithUnparseableDate_skipsOnlyThatLine() throws IOException {
        Path filePath = tempDir.resolve("coco.txt");
        Files.writeString(filePath, "D | 0 | broken deadline | not-a-date\nT | 0 | fine task\n");
        Storage storage = new Storage(filePath.toString());

        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("[T][ ] fine task", loaded.get(0).toString());
    }

    @Test
    public void load_lineWithTooFewFields_skipsOnlyThatLine() throws IOException {
        Path filePath = tempDir.resolve("coco.txt");
        Files.writeString(filePath, "T | 1\nT | 0 | fine task\n");
        Storage storage = new Storage(filePath.toString());

        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
    }

    @Test
    public void saveThenLoad_recurringDeadline_roundTripsRecurrence() {
        Storage storage = new Storage(tempDir.resolve("coco.txt").toString());
        Deadline recurring = new Deadline("standup", LocalDate.of(2019, 10, 15), Recurrence.DAILY);

        storage.save(List.of(recurring));
        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("[D][ ] standup (by: Oct 15 2019) (every: daily)", loaded.get(0).toString());
    }

    @Test
    public void load_deadlineLineFromBeforeRecurrenceExisted_loadsAsNonRecurring() throws IOException {
        Path filePath = tempDir.resolve("coco.txt");
        Files.writeString(filePath, "D | 0 | old-format deadline | 2019-10-15\n");
        Storage storage = new Storage(filePath.toString());

        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("[D][ ] old-format deadline (by: Oct 15 2019)", loaded.get(0).toString());
    }

    @Test
    public void load_deadlineLineWithUnknownRecurrence_skipsOnlyThatLine() throws IOException {
        Path filePath = tempDir.resolve("coco.txt");
        Files.writeString(filePath,
                "D | 0 | broken deadline | 2019-10-15 | FORTNIGHTLY\nT | 0 | fine task\n");
        Storage storage = new Storage(filePath.toString());

        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("[T][ ] fine task", loaded.get(0).toString());
    }
}
