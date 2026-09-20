package steph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import steph.task.Deadline;
import steph.task.Event;
import steph.task.Task;
import steph.task.ToDo;

/**
 * Unit tests for {@link Storage}.
 *
 * <p>{@code Storage} is the program's persistence layer, so its logic is
 * critical: {@code save} must write a form that {@code load} can read back
 * exactly, and {@code load} must survive a partly corrupted file rather than
 * throwing away the whole list. The private {@code parseTask} line parser
 * (type switch, done flag, malformed-line handling) is exercised here through
 * {@code load}.
 *
 * <p>The file I/O is made safe and fast for a unit test with JUnit's
 * {@link TempDir}: each test gets a fresh empty directory that is deleted
 * afterwards, so nothing touches the real {@code ./data/steph.txt}.
 */
public class StorageTest {

    @TempDir
    private Path tempDir;

    // ====================================================================
    // save
    // ====================================================================

    @Test
    public void save_tasks_writesOneToFileFormatLinePerTask() throws IOException {
        Path file = tempDir.resolve("steph.txt");
        ToDo done = new ToDo("read book");
        done.complete();
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 10, 15, 18, 0));

        new Storage(file.toString()).save(List.of(done, deadline));

        assertEquals(
                List.of("T | 1 | read book", "D | 0 | return book | 2019-10-15T18:00"),
                Files.readAllLines(file));
    }

    @Test
    public void save_parentDirectoryMissing_createsItThenWrites() throws IOException {
        Path nested = tempDir.resolve("a/b/c/steph.txt");

        new Storage(nested.toString()).save(List.of(new ToDo("x")));

        assertTrue(Files.exists(nested));
    }

    @Test
    public void save_calledAgain_overwritesPreviousContents() throws IOException {
        Path file = tempDir.resolve("steph.txt");
        Storage storage = new Storage(file.toString());

        storage.save(List.of(new ToDo("first"), new ToDo("second")));
        storage.save(List.of(new ToDo("only")));

        assertEquals(List.of("T | 0 | only"), Files.readAllLines(file));
    }

    // ====================================================================
    // load
    // ====================================================================

    @Test
    public void load_noFileAtPath_returnsEmptyList() throws IOException {
        Storage storage = new Storage(tempDir.resolve("does-not-exist.txt").toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void load_emptyFile_returnsEmptyList() throws IOException {
        Path file = tempDir.resolve("steph.txt");
        Files.writeString(file, "");

        assertTrue(new Storage(file.toString()).load().isEmpty());
    }

    @Test
    public void load_fileWrittenBySave_roundTripsEveryTask() throws IOException {
        Path file = tempDir.resolve("steph.txt");
        Storage storage = new Storage(file.toString());

        ToDo todo = new ToDo("read book");
        todo.complete();
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 10, 15, 18, 0));
        Event event = new Event("camp",
                LocalDateTime.of(2019, 10, 15, 0, 0), LocalDateTime.of(2019, 10, 16, 0, 0));
        List<Task> original = List.of(todo, deadline, event);

        storage.save(original);
        List<Task> loaded = storage.load();

        assertEquals(original.size(), loaded.size());
        for (int i = 0; i < original.size(); i++) {
            // toFileFormat captures type, done flag, name, and dates in one string.
            assertEquals(original.get(i).toFileFormat(), loaded.get(i).toFileFormat());
            assertEquals(original.get(i).isDone(), loaded.get(i).isDone());
        }
    }

    @Test
    public void load_doneFlag_restoredFromTheStatusField() throws IOException {
        Path file = tempDir.resolve("steph.txt");
        Files.write(file, List.of("T | 1 | done one", "T | 0 | not done"));

        List<Task> loaded = new Storage(file.toString()).load();

        assertTrue(loaded.get(0).isDone());
        assertFalse(loaded.get(1).isDone());
    }

    @Test
    public void load_corruptedLines_skippedButValidLinesStillLoaded() throws IOException {
        Path file = tempDir.resolve("steph.txt");
        Files.write(file, List.of(
                "T | 0 | good todo", // valid
                "X | 0 | unknown type letter", // unknown type -> skipped
                "D | 1 | no date field", // missing the date column -> skipped
                "not a task line at all", // no type letter -> skipped
                "E | 0 | camp | 2019-10-15T00:00 | 2019-10-16T00:00")); // valid

        List<Task> loaded = new Storage(file.toString()).load();

        assertEquals(2, loaded.size());
        assertEquals("good todo", loaded.get(0).getName());
        assertEquals("camp", loaded.get(1).getName());
    }

    @Test
    public void load_unparseableDateInOtherwiseWellFormedLine_lineSkipped() throws IOException {
        Path file = tempDir.resolve("steph.txt");
        Files.write(file, List.of("D | 0 | return book | not-a-date"));

        assertTrue(new Storage(file.toString()).load().isEmpty());
    }

    @Test
    public void load_missingFieldsForEachType_linesSkipped() throws IOException {
        Path file = tempDir.resolve("steph.txt");
        Files.write(file, List.of("T | 0", "D | 0 | x", "E | 0 | x | 2019-10-15T00:00", "", "T"));

        assertTrue(new Storage(file.toString()).load().isEmpty());
    }

    @Test
    public void load_noSpacesAroundSeparator_stillParsed() throws IOException {
        Path file = tempDir.resolve("steph.txt");
        Files.write(file, List.of("T|1|read book"));

        List<Task> loaded = new Storage(file.toString()).load();

        assertEquals(1, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
    }

    @Test
    public void load_plainDateInDateField_treatedAsMidnight() throws IOException {
        Path file = tempDir.resolve("steph.txt");
        Files.write(file, List.of("D | 0 | return book | 2019-10-15"));

        List<Task> loaded = new Storage(file.toString()).load();

        assertEquals("D | 0 | return book | 2019-10-15T00:00", loaded.get(0).toFileFormat());
    }

    @Test
    public void load_statusFlagOtherThanOne_taskNotDone() throws IOException {
        Path file = tempDir.resolve("steph.txt");
        Files.write(file, List.of("T | 0 | a", "T | x | b", "T | 2 | c"));

        List<Task> loaded = new Storage(file.toString()).load();

        assertEquals(3, loaded.size());
        assertFalse(loaded.get(0).isDone());
        assertFalse(loaded.get(1).isDone());
        assertFalse(loaded.get(2).isDone());
    }

    @Test
    public void load_pathIsADirectory_throwsIoException() throws IOException {
        Path directory = tempDir.resolve("steph.txt");
        Files.createDirectory(directory);

        assertThrows(IOException.class, () -> new Storage(directory.toString()).load());
    }

    @Test
    public void save_emptyList_writesEmptyFile() throws IOException {
        Path file = tempDir.resolve("steph.txt");

        new Storage(file.toString()).save(List.of());

        assertTrue(Files.exists(file));
        assertTrue(Files.readAllLines(file).isEmpty());
    }

    @Test
    public void save_parentIsARegularFile_throwsIoException() throws IOException {
        Path blocker = tempDir.resolve("blocker");
        Files.writeString(blocker, "x");

        assertThrows(IOException.class, () -> new Storage(blocker.resolve("steph.txt").toString()).save(List.of()));
    }

    @Test
    public void saveThenLoad_doneFlagsOfAllTaskTypes_preserved() throws IOException {
        Path file = tempDir.resolve("steph.txt");
        Storage storage = new Storage(file.toString());
        ToDo todo = new ToDo("a");
        Deadline deadline = new Deadline("b", LocalDateTime.of(2019, 10, 15, 18, 0));
        Event event = new Event("c", LocalDateTime.of(2019, 10, 15, 0, 0), LocalDateTime.of(2019, 10, 16, 0, 0));
        todo.complete();
        event.complete();

        storage.save(List.of(todo, deadline, event));
        List<Task> loaded = storage.load();

        assertEquals(List.of(true, false, true), loaded.stream().map(Task::isDone).toList());
    }
}
