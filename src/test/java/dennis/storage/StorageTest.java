package dennis.storage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dennis.DennisException;
import dennis.task.Deadline;
import dennis.task.Event;
import dennis.task.Task;
import dennis.task.Todo;
import dennis.task.WithinPeriodTask;

/**
 * Tests for {@link Storage#save} and {@link Storage#load}: the save-file
 * text format, malformed-line recovery, and the environment cases described
 * in {@link Storage}'s own class Javadoc (missing file, directory in place
 * of a file, non-UTF-8 content).
 *
 * <p>Every test uses {@link Storage#Storage(Path)} pointed at a JUnit
 * {@code @TempDir}, never the real {@code data/dennis.txt}, so running this
 * suite never touches the user's actual save file.</p>
 */
public class StorageTest {

    @TempDir
    private Path tempDir;

    private Path file() {
        return tempDir.resolve("dennis.txt");
    }

    // ------------------------------------------------------------------
    // save()
    // ------------------------------------------------------------------

    @Test
    public void save_missingParentFolder_createsIt() throws DennisException {
        Path nested = tempDir.resolve("data").resolve("dennis.txt");
        new Storage(nested).save(List.of(new Todo("read book")));

        assertTrue(Files.exists(nested));
    }

    @Test
    public void save_eachTaskType_writesExpectedLine() throws DennisException, IOException {
        List<Task> tasks = List.of(
                new Todo("read book"),
                new Deadline("return book", "2019-12-01"),
                new Event("project meeting", "2019-12-02", "2019-12-05"),
                new WithinPeriodTask("collect certificate", "2019-01-15", "2019-01-25"));
        Path file = file();

        new Storage(file).save(tasks);

        assertEquals(List.of(
                "T | 0 | read book",
                "D | 0 | return book | 2019-12-01",
                "E | 0 | project meeting | 2019-12-02 | 2019-12-05",
                "W | 0 | collect certificate | 2019-01-15 | 2019-01-25"),
                Files.readAllLines(file, StandardCharsets.UTF_8));
    }

    @Test
    public void save_doneTask_writesOneFlag() throws DennisException, IOException {
        Todo done = new Todo("read book");
        done.markAsDone();
        Path file = file();

        new Storage(file).save(List.of(done));

        assertEquals(List.of("T | 1 | read book"),
                Files.readAllLines(file, StandardCharsets.UTF_8));
    }

    @Test
    public void save_calledTwice_overwritesRatherThanAppends() throws DennisException, IOException {
        Path file = file();
        Storage storage = new Storage(file);

        storage.save(List.of(new Todo("first save")));
        storage.save(List.of(new Todo("second save")));

        assertEquals(List.of("T | 0 | second save"),
                Files.readAllLines(file, StandardCharsets.UTF_8));
    }

    @Test
    public void save_null_doesNothingAndDoesNotThrow() {
        Path file = file();

        assertDoesNotThrow(() -> new Storage(file).save(null));

        assertFalse(Files.exists(file));
    }

    // ------------------------------------------------------------------
    // load(): missing / empty / environment cases
    // ------------------------------------------------------------------

    @Test
    public void load_missingFile_returnsEmptyList() {
        assertTrue(new Storage(file()).load().isEmpty());
    }

    @Test
    public void load_emptyFile_returnsEmptyList() throws IOException {
        Path file = file();
        Files.createFile(file);

        assertTrue(new Storage(file).load().isEmpty());
    }

    @Test
    public void load_directoryInPlaceOfFile_returnsEmptyListNoCrash() throws IOException {
        Path dir = file();
        Files.createDirectory(dir);

        assertTrue(new Storage(dir).load().isEmpty());
    }

    @Test
    public void load_nonUtf8Bytes_returnsEmptyListNoCrash() throws IOException {
        Path file = file();
        // 0xFF is not a valid byte anywhere in a UTF-8 sequence.
        Files.write(file, new byte[] {(byte) 0xFF, (byte) 0xFE});

        assertTrue(new Storage(file).load().isEmpty());
    }

    @Test
    public void load_leadingBom_isStrippedFromFirstLine() throws IOException {
        Path file = file();
        Files.write(file, ("﻿T | 0 | read book").getBytes(StandardCharsets.UTF_8));

        List<Task> tasks = new Storage(file).load();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    // ------------------------------------------------------------------
    // load(): reconstructing each task type
    // ------------------------------------------------------------------

    @Test
    public void load_todoLine_reconstructsTask() throws IOException {
        writeLines("T | 0 | read book");

        List<Task> tasks = new Storage(file()).load();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void load_deadlineLine_reconstructsTask() throws IOException {
        writeLines("D | 0 | return book | 2019-12-01");

        List<Task> tasks = new Storage(file()).load();

        assertEquals(1, tasks.size());
        assertEquals("[D][ ] return book (by: Dec 01 2019)", tasks.get(0).toString());
    }

    @Test
    public void load_eventLine_reconstructsTask() throws IOException {
        writeLines("E | 0 | project meeting | 2019-12-02 | 2019-12-05");

        List<Task> tasks = new Storage(file()).load();

        assertEquals(1, tasks.size());
        assertEquals("[E][ ] project meeting (from: Dec 02 2019 to: Dec 05 2019)",
                tasks.get(0).toString());
    }

    @Test
    public void load_withinPeriodLine_reconstructsTask() throws IOException {
        writeLines("W | 0 | collect certificate | 2019-01-15 | 2019-01-25");

        List<Task> tasks = new Storage(file()).load();

        assertEquals(1, tasks.size());
        assertEquals("[W][ ] collect certificate (within: Jan 15 2019 to: Jan 25 2019)",
                tasks.get(0).toString());
    }

    @Test
    public void load_doneFlagOne_marksTaskDone() throws IOException {
        writeLines("T | 1 | read book");

        assertEquals("[T][X] read book", new Storage(file()).load().get(0).toString());
    }

    @Test
    public void load_multipleLines_preservesOrder() throws IOException {
        writeLines("T | 0 | first", "T | 0 | second", "T | 0 | third");

        List<Task> tasks = new Storage(file()).load();

        assertEquals(3, tasks.size());
        assertEquals("[T][ ] first", tasks.get(0).toString());
        assertEquals("[T][ ] second", tasks.get(1).toString());
        assertEquals("[T][ ] third", tasks.get(2).toString());
    }

    @Test
    public void load_blankLinesBetweenTasks_areSkipped() throws IOException {
        writeLines("T | 0 | first", "", "   ", "T | 0 | second");

        List<Task> tasks = new Storage(file()).load();

        assertEquals(2, tasks.size());
    }

    // ------------------------------------------------------------------
    // load(): malformed lines are skipped, not fatal
    // ------------------------------------------------------------------

    @Test
    public void load_lineWithTooFewFields_isSkippedRestStillLoad() throws IOException {
        writeLines("T | 0", "T | 0 | second");

        List<Task> tasks = new Storage(file()).load();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] second", tasks.get(0).toString());
    }

    @Test
    public void load_unknownTypeTag_isSkipped() throws IOException {
        writeLines("X | 0 | mystery task");

        assertTrue(new Storage(file()).load().isEmpty());
    }

    @Test
    public void load_badDoneFlag_isSkipped() throws IOException {
        writeLines("T | 2 | read book");

        assertTrue(new Storage(file()).load().isEmpty());
    }

    @Test
    public void load_badDateInDeadlineLine_isSkipped() throws IOException {
        writeLines("D | 0 | return book | not-a-date");

        assertTrue(new Storage(file()).load().isEmpty());
    }

    @Test
    public void load_wrongFieldCountForEventLine_isSkipped() throws IOException {
        writeLines("E | 0 | project meeting | 2019-12-02");

        assertTrue(new Storage(file()).load().isEmpty());
    }

    // ------------------------------------------------------------------
    // save() then load(): full round trip
    // ------------------------------------------------------------------

    @Test
    public void saveThenLoad_mixedTaskTypesAndDoneStatus_roundTripsExactly()
            throws DennisException {
        Todo doneTodo = new Todo("read book");
        doneTodo.markAsDone();
        List<Task> original = List.of(
                doneTodo,
                new Deadline("return book", "2019-12-01"),
                new Event("project meeting", "2019-12-02", "2019-12-05"),
                new WithinPeriodTask("collect certificate", "2019-01-15", "2019-01-25"));
        Storage storage = new Storage(file());

        storage.save(original);
        List<Task> reloaded = storage.load();

        assertEquals(original.size(), reloaded.size());
        for (int i = 0; i < original.size(); i++) {
            assertEquals(original.get(i), reloaded.get(i));
            assertEquals(original.get(i).toString(), reloaded.get(i).toString());
        }
    }

    /** Writes {@code lines} to this test's save file, one per line, as UTF-8. */
    private void writeLines(String... lines) throws IOException {
        Files.write(file(), List.of(lines), StandardCharsets.UTF_8);
    }
}
