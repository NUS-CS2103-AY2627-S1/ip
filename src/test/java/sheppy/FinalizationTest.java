package sheppy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import sheppy.storage.Storage;
import sheppy.task.Event;
import sheppy.task.TaskList;
import sheppy.task.Todo;

/** Protects data and user-facing behavior through the finalization refactor. */
class FinalizationTest {
    @TempDir
    private Path directory;

    /** Model validation must apply even when task creation bypasses the command parser. */
    @Test
    void constructors_rejectUnserializableFields() {
        assertThrows(SheppyException.class, () -> new Todo("a|b"));
        assertThrows(SheppyException.class, () -> new Todo("a\nb"));
        assertThrows(SheppyException.class, () -> new Event("a", "x|y", "z"));
        assertThrows(SheppyException.class, () -> new Event("a", "x", "y\rz"));
    }

    /** A snapshot restores both statuses along with membership and ordering. */
    @Test
    void snapshot_restoresCompleteState() throws SheppyException {
        Todo first = new Todo("zebra");
        first.markAsDone();
        Todo second = new Todo("apple");
        TaskList tasks = new TaskList(first, second);
        TaskList.Snapshot snapshot = tasks.snapshot();
        first.markAsUndone();
        second.markAsDone();
        tasks.sortByDescription();
        tasks.remove(1);
        tasks.add(new Todo("extra"));
        snapshot.restore();
        assertEquals(List.of(first, second), tasks.asList());
        assertTrue(first.isDone());
        assertFalse(second.isDone());
    }

    /** A failed unmark must restore a completed task and permit a later successful retry. */
    @Test
    void failedUnmark_rollsBackAndCanRetry() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Sheppy app = new Sheppy(file.toString());
        assertTrue(app.getResponse("todo read").contains("1 task in the list."));
        app.getResponse("mark 1");
        String before = app.getResponse("list");
        Path backup = directory.resolve("backup.txt");
        Files.move(file, backup);
        Files.createDirectory(file);
        Files.writeString(file.resolve("block.txt"), "block");
        assertTrue(app.getResponse("unmark 1").contains("couldn't save"));
        assertEquals(before, app.getResponse("list"));
        Files.delete(file.resolve("block.txt"));
        Files.delete(file);
        Files.move(backup, file);
        assertTrue(app.getResponse("unmark 1").contains("[T][ ] read"));
        assertEquals(app.getResponse("list"), new Sheppy(file.toString()).getResponse("list"));
    }

    /** Corrupt record errors identify the record that needs repair. */
    @Test
    void loadFailure_reportsLineAndCause() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | valid\nbroken\n");
        SheppyException failure = assertThrows(SheppyException.class,
                () -> new Storage(file.toString()).load());
        assertTrue(failure.getMessage().contains("line 2"));
        assertTrue(failure.getMessage().contains(file.toString()));
        assertTrue(failure.getCause() instanceof SheppyException);
        assertEquals("T | 0 | valid\nbroken\n", Files.readString(file));
    }
}
