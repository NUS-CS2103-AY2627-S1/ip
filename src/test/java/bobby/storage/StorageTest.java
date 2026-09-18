package bobby.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bobby.task.Task;
import bobby.task.Todo;

/** Tests persistence at Bobby's configured save-file location. */
class StorageTest {
    private static final Path SAVE_FILE = Path.of("data", "bobby.txt");
    private byte[] originalSaveFile;

    /** Backs up a user's existing save file before the test writes its own task data. */
    @BeforeEach
    void backUpExistingSaveFile() throws IOException {
        if (Files.exists(SAVE_FILE)) {
            originalSaveFile = Files.readAllBytes(SAVE_FILE);
        }
    }

    /** Restores a user's existing save file after the test completes. */
    @AfterEach
    void restoreOriginalSaveFile() throws IOException {
        Files.deleteIfExists(SAVE_FILE);
        if (originalSaveFile != null) {
            Files.write(SAVE_FILE, originalSaveFile);
        }
    }

    /** Saving tasks creates the Bobby-named save file and makes the tasks available to a new load operation. */
    @Test
    void saveAndLoad_tasksSaved_tasksLoadedFromBobbyFile() throws IOException {
        Todo task = new Todo("read book");
        task.markAsDone(LocalDateTime.of(2026, 9, 10, 15, 42, 18));

        Storage.save(List.of(task));

        assertTrue(Files.exists(SAVE_FILE));
        assertEquals("T | 1 | read book | 2026-09-10T15:42:18",
                Files.readString(SAVE_FILE).strip());
        assertEquals("[T][X] read book", Storage.load().getFirst().toString());
    }

    /** Loading without an existing save file returns an empty mutable task list. */
    @Test
    void load_missingSaveFile_emptyListReturned() throws IOException {
        Files.deleteIfExists(SAVE_FILE);

        List<Task> tasks = Storage.load();
        tasks.add(new Todo("read book"));

        assertEquals(1, tasks.size());
    }

    /** Saving an empty task list creates an empty save file that can be loaded. */
    @Test
    void save_emptyTaskList_emptySaveFileCreated() throws IOException {
        Storage.save(List.of());

        assertTrue(Files.exists(SAVE_FILE));
        assertEquals(List.of(), Files.readAllLines(SAVE_FILE));
        assertEquals(List.of(), Storage.load());
    }

    /**
     * Loading current records restores completion data for every task type.
     */
    @Test
    void load_currentRecords_allTaskTypesAndCompletionDataRestored() throws IOException {
        Files.createDirectories(SAVE_FILE.getParent());
        Files.write(SAVE_FILE, List.of(
                "T | 1 | read book | 2026-09-08T10:15:30",
                "D | 1 | return book | 2026-09-12T18:00 | 2026-09-09T11:20:45",
                "E | 0 | meeting | 2026-09-11T14:00 | 2026-09-11T16:00 | -"));

        List<Task> tasks = Storage.load();
        LocalDateTime weekStart = LocalDateTime.of(2026, 9, 7, 0, 0);
        LocalDateTime nextWeekStart = LocalDateTime.of(2026, 9, 14, 0, 0);

        assertTrue(tasks.get(0).wasCompletedBetween(weekStart, nextWeekStart));
        assertTrue(tasks.get(1).wasCompletedBetween(weekStart, nextWeekStart));
        assertEquals(false, tasks.get(2).isDone());
    }

    /**
     * Loading trims fields and restores known, unknown, and absent completion times.
     */
    @Test
    void load_whitespaceAndCompletionVariants_expectedTaskStatesRestored() throws IOException {
        Files.createDirectories(SAVE_FILE.getParent());
        Files.write(SAVE_FILE, List.of(
                " T | 1 | read book | - ",
                "D | 0 | return book | 2026-09-12T18:00 | -",
                "E | 1 | meeting | 2026-09-11T14:00 | 2026-09-11T16:00 | 2026-09-10T09:08:07"));

        List<Task> tasks = Storage.load();

        assertTrue(tasks.get(0).isDone());
        assertFalse(tasks.get(0).hasKnownCompletionDateTime());
        assertFalse(tasks.get(1).isDone());
        assertEquals(LocalDateTime.of(2026, 9, 10, 9, 8, 7),
                tasks.get(2).getCompletionDateTime().orElseThrow());
    }

    /**
     * Loading legacy records preserves status and a later save migrates them to the current format.
     */
    @Test
    void loadAndSave_legacyRecords_statusPreservedAndRecordsMigrated() throws IOException {
        Files.createDirectories(SAVE_FILE.getParent());
        Files.write(SAVE_FILE, List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2026-09-12T18:00",
                "E | 1 | meeting | 2026-09-11T14:00 | 2026-09-11T16:00"));

        List<Task> tasks = Storage.load();
        Storage.save(tasks);

        assertEquals("[T][X] read book", tasks.get(0).toString());
        assertEquals("[D][ ] return book (appointed for: Sep 12 2026 6:00 PM)", tasks.get(1).toString());
        assertEquals("[E][X] meeting (commencing: Sep 11 2026 2:00 PM; concluding: Sep 11 2026 4:00 PM)",
                tasks.get(2).toString());
        assertEquals(List.of(
                "T | 1 | read book | -",
                "D | 0 | return book | 2026-09-12T18:00 | -",
                "E | 1 | meeting | 2026-09-11T14:00 | 2026-09-11T16:00 | -"),
                Files.readAllLines(SAVE_FILE));
    }

    /** Loading rejects malformed field counts, values, and task types. */
    @Test
    void load_invalidTaskRecords_ioExceptionThrown() throws IOException {
        assertInvalidTaskRecord("   ");
        assertInvalidTaskRecord("T | 0");
        assertInvalidTaskRecord(" | 0 | read book");
        assertInvalidTaskRecord("T | | read book");
        assertInvalidTaskRecord("T | 0 | ");
        assertInvalidTaskRecord("T | 2 | read book");
        assertInvalidTaskRecord("T | 0 | read book | - | extra");
        assertInvalidTaskRecord("D | 0 | return book");
        assertInvalidTaskRecord("D | 0 | return book | ");
        assertInvalidTaskRecord("D | 0 | return book | 2026-09-12T18:00 | - | extra");
        assertInvalidTaskRecord("E | 0 | meeting | | 2026-09-01T16:00");
        assertInvalidTaskRecord("E | 0 | meeting | 2026-09-01T14:00 | ");
        assertInvalidTaskRecord("E | 0 | meeting | 2026-09-01T14:00 | 2026-09-01T16:00 | - | extra");
        assertInvalidTaskRecord("E | 0 | meeting | invalid date | 2026-09-01T16:00");
        assertInvalidTaskRecord("E | 0 | meeting | 2026-09-01T16:00 | 2026-09-01T16:00");
        assertInvalidTaskRecord("E | 0 | meeting | 2026-09-01T17:00 | 2026-09-01T16:00");
        assertInvalidTaskRecord("N | 0 | unknown task");
        assertInvalidTaskRecord("T | 1 | read book | invalid date");
        assertInvalidTaskRecord("T | 1 | read book | 2026-09-10T15:42:18.123");
        assertInvalidTaskRecord("T | 0 | read book | 2026-09-10T15:42:18");
        assertInvalidTaskRecord("T | 0 | read book\n\nT | 0 | write essay");
        assertInvalidTaskRecord("T | 0 | read book\nT | 1 | READ BOOK");
    }

    /** Saving rejects duplicate data before replacing a valid existing save file. */
    @Test
    void save_duplicateTasks_ioExceptionThrownAndExistingFilePreserved() throws IOException {
        Storage.save(List.of(new Todo("existing task")));

        assertThrows(IOException.class, () ->
                Storage.save(List.of(new Todo("read book"), new Todo("READ BOOK"))));

        assertEquals("T | 0 | existing task | -", Files.readString(SAVE_FILE).strip());
    }

    /** Saving rejects a missing list, missing tasks, and records containing line breaks. */
    @Test
    void save_invalidTaskData_ioExceptionThrown() throws IOException {
        Task taskWithLineBreak = new Task("read book") {
            @Override
            public String toFileString() {
                return "T | 0 | read book\nT | 0 | write essay";
            }
        };

        assertThrows(IOException.class, () -> Storage.save(null));
        assertThrows(IOException.class, () -> Storage.save(Arrays.asList((Task) null)));
        assertThrows(IOException.class, () -> Storage.save(List.of(taskWithLineBreak)));
    }

    /**
     * Verifies that loading rejects a malformed saved record.
     *
     * @param record the complete malformed record to load
     */
    private void assertInvalidTaskRecord(String record) throws IOException {
        Files.createDirectories(SAVE_FILE.getParent());
        Files.writeString(SAVE_FILE, record);

        assertThrows(IOException.class, Storage::load);
    }
}
