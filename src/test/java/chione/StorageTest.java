package chione;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import chione.task.Deadline;
import chione.task.Event;
import chione.task.Task;
import chione.task.TaskList;
import chione.task.Todo;

/**
 * Tests {@link Storage} against real files.
 *
 * <p>{@code @TempDir} gives each test an empty folder of its own, which JUnit
 * deletes afterwards. Reading and writing genuine files is worth the small cost
 * here: the things most likely to go wrong -- a missing file, a missing folder,
 * a line that cannot be read -- are exactly the things a stand-in would have to
 * pretend about.
 */
public class StorageTest {
    /** A fresh empty folder per test; must not be private for JUnit to fill it in. */
    @TempDir
    Path tempDir;

    @Test
    public void load_fileNeverCreated_emptyListAndNoComplaint() throws ChioneException {
        // This is what every first run looks like, so it must not be an error.
        Storage storage = new Storage(tempDir.resolve("chione.txt").toString());
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void save_folderDoesNotExist_folderCreated() throws ChioneException {
        Path file = tempDir.resolve("data").resolve("chione.txt");
        Storage storage = new Storage(file.toString());

        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        storage.save(tasks);

        assertTrue(Files.exists(file));
    }

    @Test
    public void save_thenLoad_everyTaskTypeSurvives() throws ChioneException {
        Storage storage = new Storage(tempDir.resolve("chione.txt").toString());

        TaskList original = new TaskList();
        original.add(new Todo("read book"),
                new Deadline("return book", LocalDateTime.of(2019, 10, 15, 18, 0)),
                new Event("meeting",
                        LocalDateTime.of(2019, 10, 15, 14, 0),
                        LocalDateTime.of(2019, 10, 17, 16, 0)));
        storage.save(original);

        ArrayList<Task> reloaded = storage.load();
        assertEquals(3, reloaded.size());
        assertEquals("[T][ ] read book", reloaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019, 6:00pm)", reloaded.get(1).toString());
        assertEquals("[E][ ] meeting (from: Oct 15 2019, 2:00pm to: Oct 17 2019, 4:00pm)",
                reloaded.get(2).toString());
    }

    @Test
    public void save_thenLoad_doneFlagSurvives() throws ChioneException {
        Storage storage = new Storage(tempDir.resolve("chione.txt").toString());

        TaskList original = new TaskList();
        Todo done = new Todo("read book");
        done.markAsDone();
        original.add(done, new Todo("write essay"));
        storage.save(original);

        ArrayList<Task> reloaded = storage.load();
        assertEquals("[T][X] read book", reloaded.get(0).toString());
        assertEquals("[T][ ] write essay", reloaded.get(1).toString());
    }

    @Test
    public void save_emptyList_fileEmptiedRatherThanLeftStale() throws ChioneException {
        Storage storage = new Storage(tempDir.resolve("chione.txt").toString());

        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        storage.save(tasks);
        storage.save(new TaskList());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void load_blankLines_skipped() throws ChioneException, IOException {
        // A text editor can easily leave one of these behind.
        Path file = writeSaveFile("T | 0 | read book", "", "", "T | 1 | write essay");
        assertEquals(2, new Storage(file.toString()).load().size());
    }

    @Test
    public void load_unknownTypeLetter_lineQuotedBack() throws IOException {
        Path file = writeSaveFile("X | 0 | mystery");
        ChioneException e = assertThrows(ChioneException.class, () ->
                new Storage(file.toString()).load());
        assertEquals("My save file has a line I don't understand: \"X | 0 | mystery\". "
                + "Fix or delete that line and start me again.", e.getMessage());
    }

    @Test
    public void load_doneFlagThatIsNotZeroOrOne_exceptionThrown() throws IOException {
        Path file = writeSaveFile("T | 9 | read book");
        assertThrows(ChioneException.class, () -> new Storage(file.toString()).load());
    }

    @Test
    public void load_todoWithAnExtraField_exceptionThrown() throws IOException {
        // Strict field counts are what stop a description containing " | " from
        // being read back as something other than what was saved.
        Path file = writeSaveFile("T | 0 | read | book");
        assertThrows(ChioneException.class, () -> new Storage(file.toString()).load());
    }

    @Test
    public void load_deadlineMissingItsDate_exceptionThrown() throws IOException {
        Path file = writeSaveFile("D | 0 | return book");
        assertThrows(ChioneException.class, () -> new Storage(file.toString()).load());
    }

    @Test
    public void load_eventMissingItsEnd_exceptionThrown() throws IOException {
        Path file = writeSaveFile("E | 0 | meeting | 2019-10-15 1400");
        assertThrows(ChioneException.class, () -> new Storage(file.toString()).load());
    }

    @Test
    public void load_unreadableDate_reportedAsADamagedLine() throws IOException {
        // The advice for a mistyped date tells the user what to type, which makes
        // no sense about a file, so this has to be reported the other way.
        Path file = writeSaveFile("D | 0 | return book | next Tuesday");
        ChioneException e = assertThrows(ChioneException.class, () ->
                new Storage(file.toString()).load());
        assertTrue(e.getMessage().startsWith("My save file has a line I don't understand"));
    }

    /**
     * Writes a save file into this test's own folder, one line per argument.
     *
     * <p>Taking the lines separately rather than as one string keeps the escaped
     * newlines out of the tests, so what each test is describing -- a damaged
     * line, a blank one -- can be read at a glance.
     *
     * @param lines the lines of the file, without their line breaks
     * @return the path it was written to
     * @throws IOException if the temporary file cannot be written
     */
    private Path writeSaveFile(String... lines) throws IOException {
        Path file = tempDir.resolve("chione.txt");
        Files.writeString(file, String.join("\n", lines) + "\n");
        return file;
    }
    @Test
    public void save_folderNameTakenByAFile_exceptionThrownRatherThanCrash() throws IOException {
        // "data" exists, but as a file, so the folder the save file needs cannot
        // be created. That is the kind of environment problem a user can cause
        // by accident and should be told about, not shown a stack trace for.
        Path notAFolder = tempDir.resolve("data");
        Files.writeString(notAFolder, "in the way");
        Storage storage = new Storage(notAFolder.resolve("chione.txt").toString());

        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        ChioneException e = assertThrows(ChioneException.class, () -> storage.save(tasks));
        assertTrue(e.getMessage().startsWith("I could not save your tasks to "));
    }

    @Test
    public void load_deadlineWithImpossibleDate_reportedAsADamagedLine() throws IOException {
        Path file = tempDir.resolve("chione.txt");
        Files.writeString(file, "D | 0 | return book | 2019-02-30 1800");
        Storage storage = new Storage(file.toString());

        ChioneException e = assertThrows(ChioneException.class, storage::load);
        assertEquals("My save file has a line I don't understand: \"D | 0 | return book | 2019-02-30 1800\". "
                + "Fix or delete that line and start me again.", e.getMessage());
    }
}
