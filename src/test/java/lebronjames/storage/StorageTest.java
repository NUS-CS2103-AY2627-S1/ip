package lebronjames.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import lebronjames.LebronJamesException;
import lebronjames.task.Deadline;
import lebronjames.task.Event;
import lebronjames.task.Task;
import lebronjames.task.TaskDateTime;
import lebronjames.task.Todo;

/**
 * Tests {@link Storage}, which reads and writes the save file.
 *
 * <p>The important promise here is that anything saved can be loaded back
 * unchanged, and that a damaged file costs the user at most the damaged lines
 * rather than the whole list or a crash at start-up.
 *
 * <p>Every test writes inside a JUnit {@code @TempDir}, so the real save file is
 * never touched and each test starts from a clean folder.
 */
public class StorageTest {
    @TempDir
    private Path tempFolder;

    /**
     * Returns storage backed by a fresh file in this test's temporary folder.
     *
     * @return Storage under test.
     */
    private Storage storage() {
        return new Storage(tempFolder.toString(), "tasks.txt");
    }

    /**
     * Writes the given lines straight into the save file, bypassing Storage, so
     * a test can set up a file that Storage itself would never produce.
     *
     * @param lines Lines to write.
     * @throws IOException If the file cannot be written.
     */
    private void writeSaveFile(String... lines) throws IOException {
        Files.write(tempFolder.resolve("tasks.txt"), List.of(lines), StandardCharsets.UTF_8);
    }

    @Test
    public void load_fileDoesNotExist_emptyListNotAnError() throws LebronJamesException {
        // A first run has nothing saved yet; that is normal, not a failure.
        assertEquals(0, storage().load().size());
    }

    @Test
    public void load_emptyFile_emptyList() throws Exception {
        writeSaveFile();

        assertEquals(0, storage().load().size());
    }

    @Test
    public void load_blankLines_skipped() throws Exception {
        writeSaveFile("", "T | 0 | read book", "   ");

        assertEquals(1, storage().load().size());
    }

    @Test
    public void load_oneOfEachTaskType_allRestored() throws Exception {
        writeSaveFile("T | 1 | read book",
                "D | 0 | return book | 2019-06-06",
                "E | 0 | project meeting | 2019-08-06 1400 | 2019-08-06 1600");

        ArrayList<Task> loaded = storage().load();

        assertEquals(3, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Jun 06 2019)", loaded.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Aug 06 2019, 2:00pm to: Aug 06 2019, 4:00pm)",
                loaded.get(2).toString());
    }

    @Test
    public void load_doneFlag_restoredBothWays() throws Exception {
        writeSaveFile("T | 1 | done one", "T | 0 | not done");

        ArrayList<Task> loaded = storage().load();

        assertEquals("X", loaded.get(0).getStatusIcon());
        assertEquals(" ", loaded.get(1).getStatusIcon());
    }

    @Test
    public void load_damagedLine_droppedAndTheRestKept() throws Exception {
        // A corrupted file should cost the user only the lines that are broken.
        writeSaveFile("T | 0 | first",
                "this line is nonsense",
                "T | 0 | second");

        ArrayList<Task> loaded = storage().load();

        assertEquals(2, loaded.size());
        assertEquals("first", loaded.get(0).getDescription());
        assertEquals("second", loaded.get(1).getDescription());
    }

    @Test
    public void load_malformedLines_allDropped() throws Exception {
        writeSaveFile("T | 0",
                "X | 0 | unknown type",
                "T | 2 | bad done flag",
                "T | 0 | ",
                "D | 0 | missing date",
                "D | 0 | empty date | ",
                "D | 0 | bad date | someday",
                "E | 0 | missing end | 2019-08-06",
                "T | 0 | extra field | oops");

        assertEquals(0, storage().load().size());
    }

    @Test
    public void load_pathIsADirectory_exceptionThrown() throws Exception {
        Files.createDirectory(tempFolder.resolve("tasks.txt"));

        assertThrows(LebronJamesException.class, () -> storage().load());
    }

    @Test
    public void save_thenLoad_tasksComeBackUnchanged() throws Exception {
        List<Task> original = new ArrayList<>();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        original.add(todo);
        original.add(new Deadline("return book", TaskDateTime.parse("2019-06-06")));
        original.add(new Event("project meeting",
                TaskDateTime.parse("2019-08-06 1400"), TaskDateTime.parse("2019-08-06 1600")));

        Storage storage = storage();
        storage.save(original);
        ArrayList<Task> loaded = storage.load();

        assertEquals(original.size(), loaded.size());
        for (int i = 0; i < original.size(); i++) {
            assertEquals(original.get(i).toString(), loaded.get(i).toString());
        }
    }

    @Test
    public void save_dateTypedInAnyAcceptedFormat_writtenInTheCanonicalOne() throws Exception {
        // The user may type 2/12/2019, but the file must always hold 2019-12-02
        // so load() can read it back with the same formats.
        Storage storage = storage();
        storage.save(List.of(new Deadline("pay rent", TaskDateTime.parse("2/12/2019"))));

        assertEquals(List.of("D | 0 | pay rent | 2019-12-02"),
                Files.readAllLines(tempFolder.resolve("tasks.txt")));
    }

    @Test
    public void save_emptyList_fileEmptiedNotLeftStale() throws Exception {
        Storage storage = storage();
        storage.save(List.of(new Todo("read book")));

        storage.save(new ArrayList<>());

        // Deleting the last task must not leave the old one behind on disk.
        assertEquals(0, Files.readAllLines(tempFolder.resolve("tasks.txt")).size());
        assertEquals(0, storage.load().size());
    }

    @Test
    public void save_replacesPreviousContents() throws Exception {
        Storage storage = storage();
        storage.save(List.of(new Todo("first"), new Todo("second")));

        storage.save(List.of(new Todo("only")));

        assertEquals(List.of("T | 0 | only"), Files.readAllLines(tempFolder.resolve("tasks.txt")));
    }

    @Test
    public void save_folderDoesNotExist_folderCreated() throws Exception {
        // The chatbot has to work on a machine where it has never run before.
        Storage storage = new Storage(tempFolder.toString(), "brand", "new", "tasks.txt");

        storage.save(List.of(new Todo("read book")));

        assertTrue(Files.exists(tempFolder.resolve("brand").resolve("new").resolve("tasks.txt")));
        assertEquals(1, storage.load().size());
    }

    @Test
    public void save_nonAsciiDescription_readBackUnchanged() throws Exception {
        // The save file is always UTF-8, so a description with an accent or an
        // emoji has to survive the round trip byte for byte.
        Storage storage = storage();
        storage.save(List.of(new Todo("café run"), new Todo("shoot hoops 🏀")));

        ArrayList<Task> loaded = storage.load();

        assertEquals("café run", loaded.get(0).getDescription());
        assertEquals("shoot hoops 🏀", loaded.get(1).getDescription());
    }

    @Test
    public void save_damagedLines_disappearOnTheNextSave() throws Exception {
        writeSaveFile("T | 0 | keep me", "nonsense");
        Storage storage = storage();

        storage.save(storage.load());

        assertEquals(List.of("T | 0 | keep me"), Files.readAllLines(tempFolder.resolve("tasks.txt")));
    }
}
