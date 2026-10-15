package alzara.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import alzara.task.Deadline;
import alzara.task.Event;
import alzara.task.Task;
import alzara.task.ToDo;
import alzara.ui.Ui;

/**
 * Tests for {@link Storage}. Each test points {@link Storage} at a fresh JUnit
 * {@code @TempDir}, so none of them ever touch the real {@code data/alzara.txt}
 * save file. A GUI-mode {@link Ui} is passed to {@link Storage#save}/{@link
 * Storage#load} so its status reports (e.g. "A flawed memory ... was
 * discarded") can be read back via {@link Ui#getAndClearResponse()} instead
 * of capturing {@code System.out}.
 */
class StorageTest {
    private static final String SAVE_FILE_NAME = "alzara.txt";

    /**
     * Writes {@code lines} (already in save-file format) directly into
     * {@code dataDir}'s {@code alzara.txt}, simulating a save file that
     * already existed before {@link Storage} touched it.
     */
    private void writeSaveFile(File dataDir, String... lines) throws IOException {
        File saveFile = new File(dataDir, SAVE_FILE_NAME);
        Files.write(saveFile.toPath(), List.of(lines), StandardCharsets.UTF_8);
    }

    // --- save() / load() round trip ---

    // A freshly created data directory has no save file yet, so load() should
    // return an empty list without reporting anything.
    @Test
    void load_noSaveFileYet_returnsEmptyList(@TempDir File dataDir) {
        Storage storage = new Storage(dataDir);
        Ui ui = new Ui(true);

        ArrayList<Task> loaded = storage.load(ui);

        assertTrue(loaded.isEmpty());
        assertEquals("", ui.getAndClearResponse());
    }

    // save() followed by load() should reconstruct an equivalent ToDo,
    // including its done flag.
    @Test
    void saveThenLoad_singleToDo_roundTrips(@TempDir File dataDir) {
        Storage storage = new Storage(dataDir);
        Ui ui = new Ui(true);
        ArrayList<Task> memory = new ArrayList<>();
        memory.add(new ToDo("read book"));

        storage.save(memory, ui);
        ArrayList<Task> loaded = storage.load(ui);

        assertEquals(1, loaded.size());
        assertEquals("T | N | read book", loaded.get(0).toSaveFormat());
    }

    // save() should persist every task type (ToDo/Deadline/Event), their
    // dates, and their done flags, and load() should reconstruct them in the
    // same order.
    @Test
    void saveThenLoad_allTaskTypesAndDoneStates_preservesOrderAndDetails(@TempDir File dataDir) {
        Storage storage = new Storage(dataDir);
        Ui ui = new Ui(true);
        ArrayList<Task> memory = new ArrayList<>();
        ToDo toDo = new ToDo("read book");
        toDo.mark();
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        Event event = new Event("trip", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 16));
        memory.add(toDo);
        memory.add(deadline);
        memory.add(event);

        storage.save(memory, ui);
        ArrayList<Task> loaded = storage.load(ui);

        assertEquals(3, loaded.size());
        assertEquals("T | Y | read book", loaded.get(0).toSaveFormat());
        assertEquals("D | N | return book | 2019-10-15", loaded.get(1).toSaveFormat());
        assertEquals("E | N | trip | 2019-10-15 | 2019-10-16", loaded.get(2).toSaveFormat());
    }

    // save(null) should do nothing - no file, no exception.
    @Test
    void save_nullMemory_doesNothing(@TempDir File dataDir) {
        Storage storage = new Storage(dataDir);
        Ui ui = new Ui(true);

        storage.save(null, ui);

        assertFalse(new File(dataDir, SAVE_FILE_NAME).exists());
    }

    // save() should create the data directory itself if it doesn't exist yet.
    @Test
    void save_dataDirectoryDoesNotExistYet_isCreated(@TempDir File tempDir) {
        File dataDir = new File(tempDir, "nested/data/dir");
        Storage storage = new Storage(dataDir);
        Ui ui = new Ui(true);
        ArrayList<Task> memory = new ArrayList<>();
        memory.add(new ToDo("read book"));

        storage.save(memory, ui);

        assertTrue(new File(dataDir, SAVE_FILE_NAME).exists());
    }

    // A second save() should fully replace the first save's content, not append to it.
    @Test
    void save_calledTwice_secondSaveReplacesFirst(@TempDir File dataDir) {
        Storage storage = new Storage(dataDir);
        Ui ui = new Ui(true);
        ArrayList<Task> firstMemory = new ArrayList<>();
        firstMemory.add(new ToDo("read book"));
        firstMemory.add(new ToDo("borrow book"));
        storage.save(firstMemory, ui);

        ArrayList<Task> secondMemory = new ArrayList<>();
        secondMemory.add(new ToDo("walk dog"));
        storage.save(secondMemory, ui);
        ArrayList<Task> loaded = storage.load(ui);

        assertEquals(1, loaded.size());
        assertEquals("T | N | walk dog", loaded.get(0).toSaveFormat());
    }

    // save() writes to a temp file before moving it into place - after a
    // successful save, that temp file should not be left behind.
    @Test
    void save_afterSuccess_leavesNoLeftoverTempFile(@TempDir File dataDir) {
        Storage storage = new Storage(dataDir);
        Ui ui = new Ui(true);
        ArrayList<Task> memory = new ArrayList<>();
        memory.add(new ToDo("read book"));

        storage.save(memory, ui);

        assertFalse(new File(dataDir, SAVE_FILE_NAME + ".tmp").exists());
    }

    // --- load() corrupted-entry handling ---

    // Blank lines in the save file should be silently skipped - no task, no report.
    @Test
    void load_blankLinesInFile_areSkippedSilently(@TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir, "T | N | read book", "", "   ", "T | N | borrow book");
        Storage storage = new Storage(dataDir);
        Ui ui = new Ui(true);

        ArrayList<Task> loaded = storage.load(ui);

        assertEquals(2, loaded.size());
        assertEquals("", ui.getAndClearResponse());
    }

    // A line with too few "|"-separated fields should be reported and skipped.
    @Test
    void load_notEnoughFields_reportsAndSkipsLine(@TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir, "T | N");
        Storage storage = new Storage(dataDir);
        Ui ui = new Ui(true);

        ArrayList<Task> loaded = storage.load(ui);

        assertTrue(loaded.isEmpty());
        assertEquals("A flawed memory on line 1 was discarded: not enough fields",
                ui.getAndClearResponse());
    }

    // A done flag that isn't "N" or "Y" should be reported and skipped.
    @Test
    void load_invalidDoneFlag_reportsAndSkipsLine(@TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir, "T | Z | read book");
        Storage storage = new Storage(dataDir);
        Ui ui = new Ui(true);

        ArrayList<Task> loaded = storage.load(ui);

        assertTrue(loaded.isEmpty());
        assertEquals("A flawed memory on line 1 was discarded: invalid done flag",
                ui.getAndClearResponse());
    }

    // A blank description should be reported and skipped.
    @Test
    void load_missingDescription_reportsAndSkipsLine(@TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir, "T | N |   ");
        Storage storage = new Storage(dataDir);
        Ui ui = new Ui(true);

        ArrayList<Task> loaded = storage.load(ui);

        assertTrue(loaded.isEmpty());
        assertEquals("A flawed memory on line 1 was discarded: missing description",
                ui.getAndClearResponse());
    }

    // An unrecognised type letter should be reported (including the offending
    // letter) and skipped.
    @Test
    void load_unrecognisedTaskType_reportsAndSkipsLine(@TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir, "X | N | mystery task");
        Storage storage = new Storage(dataDir);
        Ui ui = new Ui(true);

        ArrayList<Task> loaded = storage.load(ui);

        assertTrue(loaded.isEmpty());
        assertEquals("A flawed memory on line 1 was discarded: unrecognised task type 'X'",
                ui.getAndClearResponse());
    }

    // A "D" line with no date field at all should be reported and skipped.
    @Test
    void load_deadlineMissingDateField_reportsAndSkipsLine(@TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir, "D | N | return book");
        Storage storage = new Storage(dataDir);
        Ui ui = new Ui(true);

        ArrayList<Task> loaded = storage.load(ui);

        assertTrue(loaded.isEmpty());
        assertEquals("A flawed memory on line 1 was discarded: missing deadline field",
                ui.getAndClearResponse());
    }

    // A "D" line whose date field doesn't parse as yyyy-MM-dd should be reported and skipped.
    @Test
    void load_deadlineInvalidDate_reportsAndSkipsLine(@TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir, "D | N | return book | Sunday");
        Storage storage = new Storage(dataDir);
        Ui ui = new Ui(true);

        ArrayList<Task> loaded = storage.load(ui);

        assertTrue(loaded.isEmpty());
        assertEquals("A flawed memory on line 1 was discarded: invalid deadline date",
                ui.getAndClearResponse());
    }

    // An "E" line missing its start/end fields should be reported and skipped.
    @Test
    void load_eventMissingDateFields_reportsAndSkipsLine(@TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir, "E | N | trip");
        Storage storage = new Storage(dataDir);
        Ui ui = new Ui(true);

        ArrayList<Task> loaded = storage.load(ui);

        assertTrue(loaded.isEmpty());
        assertEquals("A flawed memory on line 1 was discarded: missing event start/end field",
                ui.getAndClearResponse());
    }

    // An "E" line whose start or end date doesn't parse should be reported and skipped.
    @Test
    void load_eventInvalidDate_reportsAndSkipsLine(@TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir, "E | N | trip | 2019-10-15 | Sunday");
        Storage storage = new Storage(dataDir);
        Ui ui = new Ui(true);

        ArrayList<Task> loaded = storage.load(ui);

        assertTrue(loaded.isEmpty());
        assertEquals("A flawed memory on line 1 was discarded: invalid event date",
                ui.getAndClearResponse());
    }

    // An "E" line whose start date is after its end date should be reported and skipped,
    // even though both dates parse fine individually.
    @Test
    void load_eventStartDateAfterEndDate_reportsAndSkipsLine(@TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir, "E | N | trip | 2019-10-20 | 2019-10-15");
        Storage storage = new Storage(dataDir);
        Ui ui = new Ui(true);

        ArrayList<Task> loaded = storage.load(ui);

        assertTrue(loaded.isEmpty());
        assertEquals("A flawed memory on line 1 was discarded: event start date after end date",
                ui.getAndClearResponse());
    }

    // A save file mixing valid and corrupted lines should keep every valid
    // task, in order, and report every corrupted line, all joined into one
    // combined status message, in line-number order.
    @Test
    void load_mixOfValidAndCorruptedLines_keepsValidTasksAndReportsEachCorruptedLine(
            @TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir,
                "T | N | read book",
                "X | N | mystery task",
                "D | N | return book",
                "T | Y | walk dog");
        Storage storage = new Storage(dataDir);
        Ui ui = new Ui(true);

        ArrayList<Task> loaded = storage.load(ui);

        assertEquals(2, loaded.size());
        assertEquals("T | N | read book", loaded.get(0).toSaveFormat());
        assertEquals("T | Y | walk dog", loaded.get(1).toSaveFormat());
        assertEquals("A flawed memory on line 2 was discarded: unrecognised task type 'X'\n"
                + "A flawed memory on line 3 was discarded: missing deadline field",
                ui.getAndClearResponse());
    }
}
