package milo.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import milo.exception.MiloException;
import milo.task.Deadline;
import milo.task.Event;
import milo.task.Task;
import milo.task.Todo;

/**
 * Storage is the boundary between the in-memory task list and the disk, so
 * its failure modes matter as much as its happy path: a first run with no
 * save file yet, a save file whose parent folder does not exist yet, and a
 * save file that has been hand-edited into something invalid.
 */
class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    void load_pathIsADirectory_warnsAndStartsEmpty() throws IOException {
        Path asDirectory = tempDir.resolve("milo.txt");
        Files.createDirectory(asDirectory);

        Storage storage = new Storage(asDirectory.toString());
        assertTrue(storage.load().isEmpty());
        assertEquals(1, storage.getLoadWarnings().size());
        assertTrue(storage.getLoadWarnings().get(0).contains("isn't a file I can read from"));
    }

    @Test
    void load_missingFile_startsEmptyWithoutComplaining() {
        Storage storage = new Storage(tempDir.resolve("never-written.txt").toString());
        assertTrue(storage.load().isEmpty());
        assertTrue(storage.getLoadWarnings().isEmpty());
    }

    @Test
    void load_calledTwice_doesNotRepeatTheFirstLoadsWarnings() throws IOException {
        Path file = tempDir.resolve("milo.txt");
        Files.writeString(file, "this is not a task line\n");

        Storage storage = new Storage(file.toString());
        storage.load();
        int afterFirst = storage.getLoadWarnings().size();
        storage.load();
        assertEquals(afterFirst, storage.getLoadWarnings().size());
    }

    @Test
    void load_missingFile_returnsEmptyListWithNoWarnings() {
        Storage storage = new Storage(tempDir.resolve("does-not-exist.txt").toString());
        ArrayList<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty());
        assertTrue(storage.getLoadWarnings().isEmpty());
    }

    @Test
    void load_missingParentFolder_returnsEmptyListWithoutError() {
        Storage storage = new Storage(tempDir.resolve("nested/does/not/exist.txt").toString());
        ArrayList<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty());
        assertTrue(storage.getLoadWarnings().isEmpty());
    }

    @Test
    void save_missingParentFolder_createsItAutomatically() throws MiloException, IOException {
        Path file = tempDir.resolve("nested/data/milo.txt");
        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));

        storage.save(tasks);

        assertTrue(Files.exists(file));
        assertEquals("T | 0 | read book", Files.readString(file).trim());
    }

    @Test
    void save_thenLoad_roundTripsAllThreeTaskTypes() throws MiloException {
        Path file = tempDir.resolve("milo.txt");
        Storage storage = new Storage(file.toString());

        ArrayList<Task> original = new ArrayList<>();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        original.add(todo);
        original.add(new Deadline("return book", "2019-10-15"));
        original.add(new Event("project meeting", "Mon 2pm", "4pm"));
        storage.save(original);

        ArrayList<Task> reloaded = new Storage(file.toString()).load();
        assertEquals(3, reloaded.size());
        assertEquals("[T][X] read book", reloaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", reloaded.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", reloaded.get(2).toString());
    }

    @Test
    void load_lineWithUnknownTaskType_isSkippedAndWarned() throws IOException {
        Path file = tempDir.resolve("milo.txt");
        Files.writeString(file, "T | 0 | ok\nX | 0 | unknown type\n");

        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] ok", tasks.get(0).toString());
        assertEquals(1, storage.getLoadWarnings().size());
    }

    @Test
    void load_lineWithWrongFieldCount_isSkippedAndWarned() throws IOException {
        Path file = tempDir.resolve("milo.txt");
        Files.writeString(file, "T | 0 | ok\nD | 0 | missing the by field\n");

        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals(1, storage.getLoadWarnings().size());
    }

    @Test
    void load_completelyUnparsableLine_isSkippedWithoutCrashing() throws IOException {
        Path file = tempDir.resolve("milo.txt");
        Files.writeString(file, "T | 0 | ok\nGARBAGE\n");

        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();

        assertEquals(1, tasks.size());
    }

    @Test
    void load_blankLines_areIgnoredWithoutWarning() throws IOException {
        Path file = tempDir.resolve("milo.txt");
        Files.writeString(file, "T | 0 | ok\n\n   \n");

        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();

        assertEquals(1, tasks.size());
        assertTrue(storage.getLoadWarnings().isEmpty());
    }

    @Test
    void getFilePath_returnsThePathGivenToConstructor() {
        Storage storage = new Storage(tempDir.resolve("milo.txt").toString());
        assertEquals(tempDir.resolve("milo.txt"), storage.getFilePath());
    }
}
