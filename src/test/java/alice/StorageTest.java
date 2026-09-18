package alice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for the one component whose bugs cost the user their data rather
 * than merely confusing them.
 */
public class StorageTest {

    @TempDir
    private Path tempDir;

    private Storage storageAt(String fileName) {
        return new Storage(tempDir.resolve(fileName).toString());
    }

    private ArrayList<Task> listOf(Task... tasks) {
        ArrayList<Task> list = new ArrayList<>();
        for (Task task : tasks) {
            list.add(task);
        }
        return list;
    }

    @Test
    public void saveThenLoad_allTaskTypes_roundTripsFaithfully() throws AliceException {
        Storage storage = storageAt("round.txt");
        storage.save(listOf(
                new Todo("read book"),
                new Deadline("essay", LocalDateTime.of(2019, 12, 2, 18, 0)),
                new Event("talk", LocalDateTime.of(2019, 12, 2, 14, 0), LocalDateTime.of(2019, 12, 2, 16, 0))));

        ArrayList<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals("Todo", loaded.get(0).getTypeName());
        assertEquals("Deadline", loaded.get(1).getTypeName());
        assertEquals("Event", loaded.get(2).getTypeName());
        assertEquals("read book", loaded.get(0).getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), ((Deadline) loaded.get(1)).getBy());
        assertEquals(LocalDateTime.of(2019, 12, 2, 14, 0), ((Event) loaded.get(2)).getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 2, 16, 0), ((Event) loaded.get(2)).getTo());
    }

    @Test
    public void saveThenLoad_completedTask_staysCompleted() throws AliceException {
        Storage storage = storageAt("done.txt");
        Todo todo = new Todo("read book");
        todo.markAsDone();
        storage.save(listOf(todo));

        assertTrue(storage.load().get(0).isDone());
    }

    @Test
    public void saveThenLoad_notDoneTask_staysNotDone() throws AliceException {
        Storage storage = storageAt("notdone.txt");
        storage.save(listOf(new Todo("read book")));

        assertFalse(storage.load().get(0).isDone());
    }

    @Test
    public void save_overwritesPreviousContents() throws AliceException {
        Storage storage = storageAt("overwrite.txt");
        storage.save(listOf(new Todo("first"), new Todo("second")));
        storage.save(listOf(new Todo("only")));

        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("only", loaded.get(0).getDescription());
    }

    @Test
    public void save_emptyList_loadsAsEmpty() throws AliceException {
        Storage storage = storageAt("empty.txt");
        storage.save(new ArrayList<>());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void save_createsMissingParentDirectory() throws AliceException {
        Storage storage = new Storage(tempDir.resolve("nested").resolve("deep").resolve("a.txt").toString());
        storage.save(listOf(new Todo("read book")));

        assertEquals(1, storage.load().size());
    }

    @Test
    public void load_fileDoesNotExist_returnsEmptyWithoutWarning() {
        Storage storage = storageAt("absent.txt");

        assertTrue(storage.load().isEmpty());
        assertTrue(storage.getLoadWarnings().isEmpty());
    }

    @Test
    public void load_cleanFile_producesNoWarnings() throws AliceException {
        Storage storage = storageAt("clean.txt");
        storage.save(listOf(new Todo("read book")));
        storage.load();

        assertTrue(storage.getLoadWarnings().isEmpty());
    }

    @Test
    public void load_malformedLine_skipsItAndKeepsTheRest() throws IOException {
        Path file = tempDir.resolve("broken.txt");
        Files.writeString(file, "T | 0 | read book\nthis line is nonsense\nT | 1 | buy milk\n");
        Storage storage = new Storage(file.toString());

        ArrayList<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("read book", loaded.get(0).getDescription());
        assertEquals("buy milk", loaded.get(1).getDescription());
    }

    @Test
    public void load_malformedLine_recordsAWarningNamingTheLine() throws IOException {
        Path file = tempDir.resolve("warn.txt");
        Files.writeString(file, "T | 0 | read book\nthis line is nonsense\n");
        Storage storage = new Storage(file.toString());
        storage.load();

        assertEquals(1, storage.getLoadWarnings().size());
        assertTrue(storage.getLoadWarnings().get(0).contains("this line is nonsense"));
    }

    @Test
    public void load_unknownTypeMarker_skipsTheLine() throws IOException {
        Path file = tempDir.resolve("unknown.txt");
        Files.writeString(file, "T | 0 | read book\nX | 0 | mystery\n");
        Storage storage = new Storage(file.toString());

        assertEquals(1, storage.load().size());
    }

    @Test
    public void load_unparseableDate_skipsTheLineAndWarns() throws IOException {
        Path file = tempDir.resolve("baddate.txt");
        Files.writeString(file, "D | 0 | essay | not-a-date\n");
        Storage storage = new Storage(file.toString());

        assertTrue(storage.load().isEmpty());
        assertFalse(storage.getLoadWarnings().isEmpty());
    }

    @Test
    public void getLoadWarnings_isUnmodifiable() throws IOException {
        Path file = tempDir.resolve("immutable.txt");
        Files.writeString(file, "rubbish\n");
        Storage storage = new Storage(file.toString());
        storage.load();

        assertThrows(UnsupportedOperationException.class, () -> storage.getLoadWarnings().add("nope"));
    }

    @Test
    public void save_pathThatCannotBeWritten_reportsFailureInsteadOfLosingItSilently() throws IOException {
        // A regular file where a directory is needed makes the write fail.
        Path blocker = tempDir.resolve("blocker");
        Files.writeString(blocker, "not a directory");
        Storage storage = new Storage(blocker.resolve("alice.txt").toString());

        assertThrows(AliceException.class, () -> storage.save(listOf(new Todo("read book"))));
    }
}
