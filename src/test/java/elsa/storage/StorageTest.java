package elsa.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import elsa.ElsaException;
import elsa.task.Deadline;
import elsa.task.Event;
import elsa.task.TaskList;
import elsa.task.Todo;

/**
 * Tests {@link Storage}, which keeps the task list on the disk between runs.
 *
 * <p>Storage does touch the disk, so unlike the other classes tested here it
 * cannot be exercised with values alone. JUnit's {@code @TempDir} makes that
 * cheap: it hands each test a folder of its own and deletes it afterwards, so
 * the tests never see each other's files, never see a real user's tasks, and
 * leave nothing behind. Storage is written to be testable this way, because the
 * file it looks after is named by whoever builds it rather than fixed inside.
 *
 * <p>This class is worth testing despite the extra machinery, because a mistake
 * here loses work the user has already done, and does it quietly: a save that
 * did not happen looks exactly like a save that did until the next start.
 */
public class StorageTest {

    /** Builds a Storage pointing at a file inside the folder JUnit provided. */
    private static Storage storageIn(Path folder, String name) {
        return new Storage(folder.resolve(name).toString());
    }

    @Test
    public void getFileName_anyPath_returnsThePathAsGiven() {
        Storage storage = new Storage("data/elsa.txt");
        assertEquals("data/elsa.txt", storage.getFileName());
    }

    // ------------------------------------------------------------------
    // Loading
    // ------------------------------------------------------------------

    /**
     * A missing file is not an error: it means this is the first run. Reporting it
     * as a failure would greet every new user with a complaint.
     */
    @Test
    public void load_fileDoesNotExist_returnsEmptyListWithNoProblems(@TempDir Path folder)
            throws ElsaException {
        Storage.LoadResult result = storageIn(folder, "elsa.txt").load();
        assertTrue(result.tasks().isEmpty());
        assertTrue(result.problems().isEmpty());
    }

    @Test
    public void load_fileWithGoodLines_returnsThoseTasksInOrder(@TempDir Path folder)
            throws ElsaException, IOException {
        Path file = folder.resolve("elsa.txt");
        Files.write(file, List.of("T | 1 | read book", "D | 0 | return book | 2999-01-01"));

        Storage.LoadResult result = new Storage(file.toString()).load();
        assertEquals(2, result.tasks().size());
        assertEquals("[T][X] read book", result.tasks().get(0).toString());
        assertEquals("[D][ ] return book (by: Jan 01 2999)", result.tasks().get(1).toString());
        assertTrue(result.problems().isEmpty());
    }

    /**
     * One damaged line must not cost the user every other task they had saved, so
     * the good lines are kept and the bad one is described instead.
     */
    @Test
    public void load_fileWithOneBadLine_keepsTheGoodTasksAndReportsTheBadLine(
            @TempDir Path folder) throws ElsaException, IOException {
        Path file = folder.resolve("elsa.txt");
        Files.write(file, List.of("T | 0 | read book", "nonsense", "T | 0 | return book"));

        Storage.LoadResult result = new Storage(file.toString()).load();
        assertEquals(2, result.tasks().size());
        assertEquals(1, result.problems().size());
    }

    /** Line numbers are counted from 1, as they are in an editor. */
    @Test
    public void load_badLine_namesItsLineNumberCountingFromOne(@TempDir Path folder)
            throws ElsaException, IOException {
        Path file = folder.resolve("elsa.txt");
        Files.write(file, List.of("T | 0 | read book", "nonsense"));

        Storage.LoadResult result = new Storage(file.toString()).load();
        assertTrue(result.problems().get(0).startsWith("Line 2:"),
                "the problem should name line 2, but said: " + result.problems().get(0));
    }

    /** A blank line carries no task, so it is skipped rather than complained about. */
    @Test
    public void load_fileWithBlankLines_skipsThemWithoutComplaining(@TempDir Path folder)
            throws ElsaException, IOException {
        Path file = folder.resolve("elsa.txt");
        Files.write(file, List.of("T | 0 | read book", "", "   ", "T | 0 | return book"));

        Storage.LoadResult result = new Storage(file.toString()).load();
        assertEquals(2, result.tasks().size());
        assertTrue(result.problems().isEmpty());
    }

    /**
     * A path that is really a folder cannot be read as a file at all, and nothing
     * can be salvaged, so this is the one case load reports as a failure.
     */
    @Test
    public void load_pathIsAFolder_throwsException(@TempDir Path folder) throws IOException {
        Path notAFile = folder.resolve("elsa.txt");
        Files.createDirectory(notAFile);
        assertThrows(ElsaException.class, () -> new Storage(notAFile.toString()).load());
    }

    // ------------------------------------------------------------------
    // Saving
    // ------------------------------------------------------------------

    @Test
    public void save_tasks_writesOneLinePerTask(@TempDir Path folder)
            throws ElsaException, IOException {
        Path file = folder.resolve("elsa.txt");
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", LocalDate.of(2019, 10, 15)));

        new Storage(file.toString()).save(tasks);

        assertEquals(List.of("T | 0 | read book", "D | 0 | return book | 2019-10-15"),
                Files.readAllLines(file));
    }

    /** The data folder is not part of the repository, so the first save has to make it. */
    @Test
    public void save_folderDoesNotExistYet_createsIt(@TempDir Path folder) throws ElsaException {
        Path file = folder.resolve("data").resolve("elsa.txt");
        new Storage(file.toString()).save(new TaskList());
        assertTrue(Files.exists(file));
    }

    @Test
    public void save_emptyList_writesAnEmptyFile(@TempDir Path folder)
            throws ElsaException, IOException {
        Path file = folder.resolve("elsa.txt");
        new Storage(file.toString()).save(new TaskList());
        assertTrue(Files.readAllLines(file).isEmpty());
    }

    /**
     * The file is replaced each time rather than added to, so what is on the disk
     * always matches the list in memory. Appending instead would make a deleted
     * task reappear on the next start, which is the mistake this checks for.
     */
    @Test
    public void save_afterATaskIsDeleted_replacesTheFileRatherThanAppending(
            @TempDir Path folder) throws ElsaException, IOException {
        Path file = folder.resolve("elsa.txt");
        Storage storage = new Storage(file.toString());

        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));
        storage.save(tasks);

        tasks.delete(0);
        storage.save(tasks);

        assertEquals(List.of("T | 0 | return book"), Files.readAllLines(file));
    }

    // ------------------------------------------------------------------
    // Saving and loading meet
    // ------------------------------------------------------------------

    /**
     * The point of the class: a list saved by one run is the same list when the
     * next run reads it back. Every kind of task is included, done and not done,
     * because the round trip has to hold for all of them and not just the simplest.
     */
    @Test
    public void saveThenLoad_everyKindOfTask_givesTheSameListBack(@TempDir Path folder)
            throws ElsaException {
        Storage storage = storageIn(folder, "elsa.txt");

        TaskList saved = new TaskList();
        saved.add(new Todo("read book"));
        saved.add(new Deadline("return book", LocalDate.of(2999, 1, 1)));
        saved.add(new Event("project meeting",
                LocalDate.of(2019, 10, 14), LocalDate.of(2019, 10, 16)));
        saved.mark(0);
        storage.save(saved);

        TaskList loaded = storage.load().tasks();
        assertEquals(saved.size(), loaded.size());
        for (int i = 0; i < saved.size(); i++) {
            assertEquals(saved.get(i).toString(), loaded.get(i).toString(),
                    "task " + (i + 1) + " did not survive being saved and loaded");
        }
    }
}
