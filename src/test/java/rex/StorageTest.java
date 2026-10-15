package rex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import rex.task.Deadline;
import rex.task.Event;
import rex.task.Task;
import rex.task.TaskDateTime;
import rex.task.ToDo;
import rex.task.WithinPeriod;

/**
 * Tests writing the task list to a file and reading it back.
 *
 * Every test writes inside a temporary folder that JUnit makes and deletes for
 * it, so no test can see another's file and none of them can touch the real
 * save file. What comes back from load() is compared through toSaveFormat(),
 * for the reason given in ParserTest: that one string carries the type, the
 * done flag, the description and every date, so comparing it catches a value
 * landing in the wrong field as readily as a wrong value.
 *
 * Test methods are deliberately not public, for the reason given in
 * TaskDateTimeTest.
 */
public class StorageTest {
    @TempDir
    private Path folder;

    /** Returns a Storage writing to a named file inside the temporary folder. */
    private Storage storageIn(String fileName) {
        return new Storage(folder.resolve(fileName).toString());
    }

    /** Returns the save lines of every task in a list, in order. */
    private List<String> saveLinesOf(List<Task> tasks) {
        return tasks.stream().map(Task::toSaveFormat).toList();
    }

    @Test
    void load_noFileYet_emptyList() throws IOException {
        Storage storage = storageIn("nothing-here.txt");

        assertEquals(List.of(), saveLinesOf(storage.load()));
    }

    @Test
    void saveThenLoad_everyKindOfTask_readBackUnchanged() throws IOException {
        ArrayList<Task> written = new ArrayList<>(List.of(
                new ToDo("read book"),
                new Deadline("return book", TaskDateTime.parse("2019-10-15")),
                new Event("project meeting",
                        TaskDateTime.parse("2019-10-15 1400"), TaskDateTime.parse("2019-10-15 1600")),
                new WithinPeriod("collect certificate",
                        TaskDateTime.parse("2026-01-15"), TaskDateTime.parse("2026-01-25"))));
        Storage storage = storageIn("tasks.txt");

        storage.save(written);

        assertEquals(saveLinesOf(written), saveLinesOf(storage.load()));
    }

    @Test
    void saveThenLoad_doneTask_stillDone() throws IOException {
        // The done flag travels as its own field rather than as part of the
        // description, so it is worth checking that it survives the trip.
        ToDo done = new ToDo("read book");
        done.markAsDone();
        Storage storage = storageIn("tasks.txt");

        storage.save(new ArrayList<>(List.of(done)));

        assertEquals(List.of("T | 1 | read book"), saveLinesOf(storage.load()));
    }

    @Test
    void save_fileAlreadyHasTasks_replacedNotAppended() throws IOException {
        Storage storage = storageIn("tasks.txt");
        storage.save(new ArrayList<>(List.of(new ToDo("first"), new ToDo("second"))));

        storage.save(new ArrayList<>(List.of(new ToDo("only one now"))));

        assertEquals(List.of("T | 0 | only one now"), saveLinesOf(storage.load()));
    }

    @Test
    void save_emptyList_loadsBackEmpty() throws IOException {
        Storage storage = storageIn("tasks.txt");
        storage.save(new ArrayList<>(List.of(new ToDo("read book"))));

        storage.save(new ArrayList<>());

        assertEquals(List.of(), saveLinesOf(storage.load()));
    }

    @Test
    void save_folderDoesNotExist_folderCreated() throws IOException {
        // The program's real save file sits in a "data" folder that does not
        // exist on a first run, so saving has to make it rather than fail.
        Path nested = folder.resolve("data").resolve("rex.txt");
        Storage storage = new Storage(nested.toString());

        storage.save(new ArrayList<>(List.of(new ToDo("read book"))));

        assertTrue(Files.exists(nested), "Saving did not create the missing folder");
    }

    @Test
    void load_blankLines_ignoredAndNotCounted() throws IOException {
        Path file = folder.resolve("tasks.txt");
        Files.write(file, List.of("T | 0 | read book", "", "   ", "T | 0 | write essay"));
        Storage storage = new Storage(file.toString());

        List<String> loaded = saveLinesOf(storage.load());

        assertEquals(List.of("T | 0 | read book", "T | 0 | write essay"), loaded);
        assertEquals(0, storage.getSkippedLineCount(), "A blank line is not damage worth reporting");
    }

    @Test
    void load_damagedLines_skippedAndCountedWhileTheRestSurvive() throws IOException {
        // Each damaged line below breaks a different rule, so this covers every
        // way a line can be refused in one pass: the good lines around them
        // must still come back, since one bad line should not cost the user
        // everything else in the file.
        Path file = folder.resolve("tasks.txt");
        Files.write(file, List.of(
                "T | 0 | read book",
                "T | 2 | done flag is not 0 or 1",
                "X | 0 | unknown type letter",
                "T | 0",
                "T | 0 | too many fields | extra",
                "T | 0 |    ",
                "D | 0 | unreadable date | someday",
                "D | 0 | missing its date",
                "T | 0 | write essay"));
        Storage storage = new Storage(file.toString());

        List<String> loaded = saveLinesOf(storage.load());

        assertEquals(List.of("T | 0 | read book", "T | 0 | write essay"), loaded);
        assertEquals(7, storage.getSkippedLineCount());
    }

    @Test
    void getSkippedLineCount_secondLoadOfACleanFile_countReset() throws IOException {
        // The count belongs to the most recent load, not to the Storage's
        // lifetime, or a file repaired between two loads would still be
        // reported as damaged.
        Path file = folder.resolve("tasks.txt");
        Files.write(file, List.of("nonsense"));
        Storage storage = new Storage(file.toString());
        storage.load();

        Files.write(file, List.of("T | 0 | read book"));
        storage.load();

        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    void getFile_pathGivenInParts_joinedInOrder() {
        Storage storage = new Storage("data", "rex.txt");

        assertEquals(Path.of("data", "rex.txt"), storage.getFile());
    }
}
