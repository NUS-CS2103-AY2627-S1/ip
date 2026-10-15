package cheecken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks that storage restores valid records despite missing or malformed data.
 */
class StorageTest {
    @TempDir
    private Path directory;

    /**
     * Verifies load with mixed records: preserves valid tasks in order.
     * @throws Exception if test setup, execution, or file access fails
     */
    @Test
    void load_mixedRecords_preservesValidTasksInOrder() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, """
                T | 1 | read book
                invalid
                T | 2 | invalid status
                D | 0 | missing date
                D | 0 | invalid date | not-a-date
                D | 0 | return book | 2026-10-15
                E | 0 | missing end | 2026-10-15
                E | 0 | invalid end | 2026-10-15 | not-a-date
                X | 0 | unknown type
                E | 1 | meeting | 2026-10-15T09:00 | 2026-10-15T10:00
                T | 0 | last task
                """);
        List<String> errors = new ArrayList<>();
        Storage storage = new Storage(file.toString(), errors::add);

        List<String> tasks = storage.load().stream().map(Task::toString).toList();

        assertEquals(List.of(
                "[T][X] read book",
                "[D][ ] return book (by: Oct 15 2026)",
                "[E][X] meeting (from: Oct 15 2026 9:00 AM to: Oct 15 2026 10:00 AM)",
                "[T][ ] last task"), tasks);
        assertTrue(errors.isEmpty());
    }

    /**
     * Verifies load with missing file: returns empty list without error.
     */
    @Test
    void load_missingFile_returnsEmptyListWithoutError() {
        List<String> errors = new ArrayList<>();
        Storage storage = new Storage(directory.resolve("missing.txt").toString(), errors::add);

        assertTrue(storage.load().isEmpty());
        assertTrue(errors.isEmpty());
    }

    /**
     * Verifies save and load with date only and explicit midnight: remain distinct.
     */
    @Test
    void saveAndLoad_dateOnlyAndExplicitMidnight_remainDistinct() {
        Storage storage = new Storage(directory.resolve("tasks.txt").toString());
        List<Task> tasks = List.of(new Deadline("date only", "2026-09-04"),
                new Deadline("midnight", "2026-09-04T00:00"),
                new Event("mixed", "2026-09-04", "2026-09-05T00:00"));

        storage.save(tasks);

        assertEquals(tasks.stream().map(Task::toString).toList(),
                storage.load().stream().map(Task::toString).toList());
        assertEquals("D | 0 | date only | 2026-09-04", storage.load().get(0).toStorageString());
    }

    /**
     * Verifies load with natural dates: skips records without reinterpreting them.
     * @throws Exception if test setup, execution, or file access fails
     */
    @Test
    void load_naturalDates_skipsRecordsWithoutReinterpretingThem() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, """
                D | 0 | relative | today
                D | 0 | relative time | now
                E | 0 | relative start | Mon | 2026-09-05
                E | 0 | relative end | 2026-09-04 | tomorrow
                D | 1 | legacy midnight | 2026-09-04T00:00
                D | 0 | legacy numeric | 4/9/2026 0900
                T | 0 | survivor
                """);
        List<Task> tasks = new Storage(file.toString()).load();

        assertEquals(List.of("[D][X] legacy midnight (by: Sep 04 2026 12:00 AM)",
                "[D][ ] legacy numeric (by: Sep 04 2026 9:00 AM)", "[T][ ] survivor"),
                tasks.stream().map(Task::toString).toList());
    }

    /**
     * Verifies save with nested path: creates parents and overwrites previous contents.
     * @throws Exception if test setup, execution, or file access fails
     */
    @Test
    void save_nestedPath_createsParentsAndOverwritesPreviousContents() throws Exception {
        Path file = directory.resolve("nested/data/tasks.txt");
        List<String> errors = new ArrayList<>();
        Storage storage = new Storage(file.toString(), errors::add);
        Task todo = new Todo("read book");
        Task deadline = new Deadline("report", "2026-09-18");
        Task event = new Event("meeting", "2026-09-18T09:00", "2026-09-18T10:00");
        todo.mark();
        deadline.mark();
        event.mark();
        List<Task> tasks = List.of(todo, deadline, event);
        storage.save(tasks);
        assertEquals(tasks.stream().map(Task::toStorageString).toList(), Files.readAllLines(file));
        assertEquals(tasks.stream().map(Task::toStorageString).toList(),
                storage.load().stream().map(Task::toStorageString).toList());
        storage.save(List.of(new Todo("replacement")));
        assertEquals("T | 0 | replacement\n", Files.readString(file));
        storage.save(List.of());
        assertEquals("", Files.readString(file));
        assertTrue(storage.load().isEmpty());
        assertTrue(errors.isEmpty());
    }

    /**
     * Verifies save with file blocks parent: reports error and preserves existing file.
     * @throws Exception if test setup, execution, or file access fails
     */
    @Test
    void save_fileBlocksParent_reportsErrorAndPreservesExistingFile() throws Exception {
        Path blocked = directory.resolve("blocked");
        Files.writeString(blocked, "original");
        List<String> errors = new ArrayList<>();
        new Storage(blocked.resolve("tasks.txt").toString(), errors::add).save(List.of(new Todo("unsaved")));
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).startsWith("Unable to save tasks:"));
        assertEquals("original", Files.readString(blocked));
    }

    /**
     * Verifies load with directory instead of file: reports error and returns empty list.
     */
    @Test
    void load_directoryInsteadOfFile_reportsErrorAndReturnsEmptyList() {
        List<String> errors = new ArrayList<>();
        assertTrue(new Storage(directory.toString(), errors::add).load().isEmpty());
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).startsWith("Unable to load tasks:"));
    }

    /**
     * Verifies load with whitespace and unicode: preserves descriptions and skips broken records.
     * @throws Exception if test setup, execution, or file access fails
     */
    @Test
    void load_whitespaceAndUnicode_preservesDescriptionsAndSkipsBrokenRecords() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "\nT|0|买书\nT | invalid | skipped\nE|0|short\n"
                + "D|0|report|2026-09-18\nE|0|bad|invalid|2026-09-18\nT|1|café\n");
        assertEquals(List.of("[T][ ] 买书", "[D][ ] report (by: Sep 18 2026)", "[T][X] café"),
                new Storage(file.toString()).load().stream().map(Task::toString).toList());
    }

}
