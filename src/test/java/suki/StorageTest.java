package suki;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests that saving and reloading returns the same tasks.
 *
 * <p>These write to a JUnit-supplied temporary directory rather than the real
 * data/suki.txt, so running the tests never disturbs the user's own task list.
 */
public class StorageTest {

    @TempDir
    private Path tempDir;

    @Test
    public void load_missingFile_returnsEmptyList() throws SukiException {
        Storage storage = new Storage(tempDir.resolve("nothing-here.txt").toString());
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void saveThenLoad_allTaskTypes_roundTrips() throws SukiException {
        Storage storage = new Storage(tempDir.resolve("suki.txt").toString());

        ArrayList<Task> original = new ArrayList<>();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        original.add(todo);
        original.add(new Deadline("return book", DateTime.parse("2019-10-15")));
        original.add(new Event("meeting",
                DateTime.parse("2019-10-15 1400"), DateTime.parse("2019-10-15 1600")));

        storage.save(new TaskList(original));
        ArrayList<Task> reloaded = storage.load();

        assertEquals(original.size(), reloaded.size());
        for (int i = 0; i < original.size(); i++) {
            assertEquals(original.get(i).toString(), reloaded.get(i).toString());
        }
    }

    @Test
    public void saveThenLoad_doneStatusSurvives() throws SukiException {
        Storage storage = new Storage(tempDir.resolve("suki.txt").toString());

        ArrayList<Task> tasks = new ArrayList<>();
        Todo done = new Todo("done task");
        done.markAsDone();
        tasks.add(done);
        tasks.add(new Todo("pending task"));

        storage.save(new TaskList(tasks));
        ArrayList<Task> reloaded = storage.load();

        assertTrue(reloaded.get(0).isDone());
        assertFalse(reloaded.get(1).isDone());
    }

    @Test
    public void save_createsMissingParentDirectory() throws SukiException {
        Path nested = tempDir.resolve("some/new/folder/suki.txt");
        Storage storage = new Storage(nested.toString());

        storage.save(new TaskList());

        assertTrue(Files.exists(nested), "save should create the folders it needs");
    }

    @Test
    public void load_corruptLines_areSkippedRatherThanFailing() throws SukiException, IOException {
        Path file = tempDir.resolve("suki.txt");
        Files.write(file, java.util.List.of(
                "T | 0 | good task",
                "this line is nonsense",
                "D | 0 | missing its date",
                "X | 0 | unknown task type",
                "D | 0 | bad date | not-a-date",
                "T | maybe | invalid status",
                "T | 1 | another good task"));

        ArrayList<Task> loaded = new Storage(file.toString()).load();

        assertEquals(2, loaded.size(), "only the two well-formed lines should load");
        assertEquals("[T][ ] good task", loaded.get(0).toString());
        assertEquals("[T][X] another good task", loaded.get(1).toString());
    }
}
