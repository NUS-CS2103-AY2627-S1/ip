package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import nova.task.Deadline;
import nova.task.Event;
import nova.task.Task;
import nova.task.Todo;

public class StorageTest {

    private static final LocalDateTime DEC_2_6PM = LocalDateTime.of(2019, 12, 2, 18, 0);
    private static final LocalDateTime AUG_6_2PM = LocalDateTime.of(2019, 8, 6, 14, 0);
    private static final LocalDateTime AUG_8_4PM = LocalDateTime.of(2019, 8, 8, 16, 0);

    @Test
    public void load_fileDoesNotExist_returnsEmptyListWithoutFailing(@TempDir Path dir)
            throws NovaException {
        Storage storage = new Storage(dir.resolve("nova.txt").toString());

        assertEquals(0, storage.load().size());
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void save_parentFolderMissing_folderCreated(@TempDir Path dir) throws NovaException {
        Path file = dir.resolve("data").resolve("nova.txt");
        Storage storage = new Storage(file.toString());

        storage.save(List.of(new Todo("read book")));

        assertTrue(Files.exists(file));
    }

    @Test
    public void saveThenLoad_allTaskTypes_roundTripsExactly(@TempDir Path dir) throws NovaException {
        Storage storage = new Storage(dir.resolve("nova.txt").toString());
        Todo done = new Todo("read book");
        done.markAsDone();

        storage.save(List.of(done,
                new Deadline("return book", DEC_2_6PM),
                new Event("trip", AUG_6_2PM, AUG_8_4PM)));
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals(0, storage.getSkippedLineCount());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00pm)", loaded.get(1).toString());
        assertEquals("[E][ ] trip (from: Aug 06 2019, 2:00pm to: Aug 08 2019, 4:00pm)",
                loaded.get(2).toString());
    }

    @Test
    public void save_listShrinks_fileIsRewrittenNotAppended(@TempDir Path dir) throws NovaException {
        Storage storage = new Storage(dir.resolve("nova.txt").toString());

        storage.save(List.of(new Todo("first"), new Todo("second")));
        storage.save(List.of(new Todo("first")));

        assertEquals(1, storage.load().size());
    }

    @Test
    public void load_blankLines_ignored(@TempDir Path dir) throws NovaException, IOException {
        Path file = writeFile(dir, "T | 0 | read book", "", "   ", "T | 1 | join club");
        Storage storage = new Storage(file.toString());

        assertEquals(2, storage.load().size());
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void load_unreadableLines_skippedAndCounted(@TempDir Path dir)
            throws NovaException, IOException {
        Path file = writeFile(dir,
                "T | 0 | read book",
                "X | 0 | unknown type",
                "T | 9 | done flag is not 0 or 1",
                "T | 0 |    ",
                "D | 0 | missing the date",
                "D | 0 | unparseable date | someday",
                "E | 0 | missing the end time | 2019-08-06T14:00:00",
                "not a task line at all",
                "D | 1 | return book | 2019-12-02T18:00:00");
        Storage storage = new Storage(file.toString());

        List<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals(7, storage.getSkippedLineCount());
        assertEquals("[T][ ] read book", loaded.get(0).toString());
        assertEquals("[D][X] return book (by: Dec 02 2019, 6:00pm)", loaded.get(1).toString());
    }

    @Test
    public void load_calledTwice_skippedCountReflectsOnlyTheLatestLoad(@TempDir Path dir)
            throws NovaException, IOException {
        Path file = writeFile(dir, "T | 0 | read book", "X | 0 | unknown type");
        Storage storage = new Storage(file.toString());

        storage.load();
        assertEquals(1, storage.getSkippedLineCount());

        Files.write(file, List.of("T | 0 | read book"));
        storage.load();
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void saveThenLoad_descriptionContainingTheFieldSeparator_survives(@TempDir Path dir)
            throws NovaException {
        Storage storage = new Storage(dir.resolve("nova.txt").toString());

        storage.save(List.of(new Todo("read a | b"),
                new Todo("read a|b"),
                new Deadline("pay a | b", DEC_2_6PM),
                new Event("trip a | b", AUG_6_2PM, AUG_8_4PM)));
        List<Task> loaded = storage.load();

        assertEquals(4, loaded.size());
        assertEquals(0, storage.getSkippedLineCount());
        assertEquals("[T][ ] read a | b", loaded.get(0).toString());
        assertEquals("[T][ ] read a|b", loaded.get(1).toString());
        assertEquals("[D][ ] pay a | b (by: Dec 02 2019, 6:00pm)", loaded.get(2).toString());
        assertEquals("[E][ ] trip a | b (from: Aug 06 2019, 2:00pm to: Aug 08 2019, 4:00pm)",
                loaded.get(3).toString());
    }

    /** Writes the given lines to a save file inside the temporary folder. */
    private static Path writeFile(Path dir, String... lines) throws IOException {
        Path file = dir.resolve("nova.txt");
        Files.write(file, List.of(lines));
        return file;
    }
}
