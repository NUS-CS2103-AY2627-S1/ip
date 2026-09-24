package murphy.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import murphy.task.Deadline;
import murphy.task.Event;
import murphy.task.TaskList;
import murphy.task.Todo;
import murphy.ui.Ui;

/** Tests Murphy's persistence of valid and malformed task data. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    /** Verifies round-trip persistence, including escaping and completion state. */
    @Test
    void saveThenLoad_preservesTaskTypesDataAndStatus() throws Exception {
        Path file = temporaryDirectory.resolve("nested/data.txt");
        Storage storage = new Storage(file, 10, new Ui());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk | oat"));
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 8, 26));
        deadline.markAsDone();
        tasks.add(deadline);
        tasks.add(new Event("team meeting", "2026-08-26", "2026-08-27"));

        storage.save(tasks);
        TaskList loaded = storage.load();

        assertTrue(Files.exists(file));
        assertEquals(3, loaded.size());
        assertEquals("buy milk | oat", loaded.get(0).getDescription());
        assertEquals("[T][ ] buy milk | oat", loaded.get(0).toString());
        assertEquals("[D][X] submit report (by: Aug 26 2026)", loaded.get(1).toString());
        assertEquals("[E][ ] team meeting (from: 2026-08-26 to: 2026-08-27)", loaded.get(2).toString());
    }

    /** Verifies that malformed records are skipped while valid records remain available. */
    @Test
    void load_corruptedRecords_skipsThemAndKeepsValidTasks() throws Exception {
        Path file = temporaryDirectory.resolve("data.txt");
        Files.writeString(file, "T | 0 | valid task\n"
                + "T | maybe | invalid status\n"
                + "D | 0 | broken date | 26-08-2026\n"
                + "E | 0 | valid event | 2pm | 4pm\n");
        Storage storage = new Storage(file, 10, new Ui());

        TaskList loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("valid task", loaded.get(0).getDescription());
        assertEquals("valid event", loaded.get(1).getDescription());
    }

    /** Verifies that existing duplicate records remain backward compatible. */
    @Test
    void load_existingDuplicates_preservesEveryRecord() throws Exception {
        Path file = temporaryDirectory.resolve("duplicates.txt");
        Files.writeString(file, "T | 0 | buy milk\nT | 1 | buy milk\n");

        TaskList loaded = new Storage(file, 10, new Ui()).load();

        assertEquals(2, loaded.size());
        assertTrue(loaded.get(1).isDone());
    }

    /** Verifies that unreadable save-file problems are reported to the caller. */
    @Test
    void load_unreadableFile_reportsRecoverableError() throws Exception {
        Path directory = temporaryDirectory.resolve("not-a-file");
        Files.createDirectories(directory);
        List<String> messages = new ArrayList<>();

        TaskList loaded = new Storage(directory, 10, messages::add).load();

        assertEquals(0, loaded.size());
        assertTrue(messages.stream().anyMatch(message -> message.contains("couldn't read")));
    }
}
