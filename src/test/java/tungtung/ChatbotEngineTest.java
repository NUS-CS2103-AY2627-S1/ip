package tungtung;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Verifies GUI command persistence and task numbering with isolated storage. */
class ChatbotEngineTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void execute_failedAdd_restoresTasks() throws IOException {
        assertFailedCommandLeavesTasksUnchanged("todo new task", false);
    }

    @Test
    void execute_failedDelete_restoresTasks() throws IOException {
        assertFailedCommandLeavesTasksUnchanged("delete 2", false);
    }

    @Test
    void execute_failedMark_restoresStatus() throws IOException {
        assertFailedCommandLeavesTasksUnchanged("mark 2", false);
    }

    @Test
    void execute_failedUnmark_restoresStatus() throws IOException {
        assertFailedCommandLeavesTasksUnchanged("unmark 1", false);
    }

    @Test
    void execute_failedSort_restoresOrder() throws IOException {
        assertFailedCommandLeavesTasksUnchanged("sort by deadline", false);
    }

    @Test
    void execute_deniedSave_restoresTasks() throws IOException {
        assertFailedCommandLeavesTasksUnchanged("delete 1", true);
    }

    @Test
    void execute_findThenModify_usesFullListNumbers() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        ChatbotEngine engine = new ChatbotEngine(storage);
        engine.execute("todo groceries");
        engine.execute("todo book");
        engine.execute("todo book");
        assertEquals("Here are the matching tasks:\n2. [T][ ] book\n3. [T][ ] book",
                engine.execute("find book"));
        engine.execute("mark 2");
        engine.execute("delete 3");
        assertEquals("Here are your tasks:\n1. [T][ ] groceries\n2. [T][X] book",
                engine.execute("list"));
        assertEquals(engine.execute("list"), new ChatbotEngine(storage).execute("list"));
    }

    @Test
    void execute_findAfterSort_usesSortedPositions() {
        ChatbotEngine engine = new ChatbotEngine(
                new Storage(temporaryDirectory.resolve("tasks.txt").toString()));
        engine.execute("todo book");
        engine.execute("deadline report /by 2026-09-17");
        engine.execute("sort by deadline");
        assertEquals("Here are the matching tasks:\n2. [T][ ] book", engine.execute("find book"));
    }

    @Test
    void execute_corruptStartup_reportsErrorAndPreservesFile() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String original = "T | 0 | precious task\nbroken line\n";
        Files.writeString(file, original);
        ChatbotEngine engine = new ChatbotEngine(new Storage(file.toString()));
        assertNotNull(engine.getStartupError());
        for (String command : new String[] {"list", "todo replacement", "delete 1", "mark 1", "sort by deadline"}) {
            assertEquals(engine.getStartupError(), engine.execute(command));
            assertEquals(original, Files.readString(file));
        }
    }

    @Test
    void execute_twoSessions_preservesFirstSaveAndRollsBackSecond() {
        Path file = temporaryDirectory.resolve("tasks.txt");
        ChatbotEngine first = new ChatbotEngine(new Storage(file.toString()));
        ChatbotEngine second = new ChatbotEngine(new Storage(file.toString()));
        assertNull(first.getStartupError());
        first.execute("todo first session");
        String response = second.execute("todo second session");
        assertTrue(response.contains("No changes were made."));
        assertTrue(response.contains("Restart"));
        assertEquals("Here are your tasks:\nThere are no tasks to show.", second.execute("list"));
        assertEquals(first.execute("list"), new ChatbotEngine(new Storage(file.toString())).execute("list"));
    }

    @Test
    void execute_invalidInputs_leavesMemoryAndDiskUnchanged() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        ChatbotEngine engine = new ChatbotEngine(storage);
        engine.execute("todo original");
        String before = engine.execute("list");
        for (String command : new String[] {"", "find", "mark", "unmark", "delete", "mark 0", "delete -1",
            "delete -2147483648", "mark 2147483647", "mark 999999999999999999999999", "mark 1 2",
            "todo x\ny", "todo x\ry", "deadline report | /by 2026-09-18",
            "event meeting /to 2026-09-17 /from 2026-09-18", "event meeting /from 2026-09-18 /to 2026-09-17"}) {
            assertTrue(engine.execute(command).startsWith("OOPS!!!"), command);
            assertEquals(before, engine.execute("list"), command);
        }
        assertEquals(before, new ChatbotEngine(storage).execute("list"));
    }

    @Test
    void execute_fullLifecycle_persistsEverySuccessfulChange() {
        Path file = temporaryDirectory.resolve("tasks.txt");
        ChatbotEngine engine = new ChatbotEngine(new Storage(file.toString()));
        for (String command : new String[] {"todo book", "deadline report /by 2026-09-19",
            "event meeting /from 2026-09-18 /to 2026-09-19", "todo book", "mark  4", "unmark  4",
            "sort by deadline", "mark 2", "delete 3", "delete 3", "delete 2", "delete 1"}) {
            String response = engine.execute(command);
            assertTrue(!response.startsWith("OOPS!!!"), command + ": " + response);
            assertEquals(engine.execute("list"),
                    new ChatbotEngine(new Storage(file.toString())).execute("list"), command);
        }
        assertEquals("Here are your tasks:\nThere are no tasks to show.", engine.execute("list"));
    }

    /** Checks memory, saved data, and a later successful save after an injected failure. */
    private void assertFailedCommandLeavesTasksUnchanged(String command, boolean denyAccess) throws IOException {
        FailingStorage storage = new FailingStorage(temporaryDirectory.resolve("tasks.txt"));
        ChatbotEngine engine = new ChatbotEngine(storage);
        engine.execute("todo groceries");
        engine.execute("deadline book /by 2026-09-17");
        engine.execute("mark 1");
        String before = engine.execute("list");
        storage.failSave = true;
        storage.denyAccess = denyAccess;

        assertTrue(engine.execute(command).contains("No changes were made."));
        assertEquals(before, engine.execute("list"));
        assertEquals(before, new ChatbotEngine(storage).execute("list"));

        storage.failSave = false;
        engine.execute("todo later task");
        assertEquals(before + "\n3. [T][ ] later task", new ChatbotEngine(storage).execute("list"));
    }

    /** Injects deterministic write failures without depending on OS file permissions. */
    private static class FailingStorage extends Storage {
        private boolean failSave;
        private boolean denyAccess;

        FailingStorage(Path path) {
            super(path.toString());
        }

        @Override
        public void save(ArrayList<Task> tasks) throws IOException {
            if (failSave) {
                if (denyAccess) {
                    throw new SecurityException("Access denied for testing.");
                }
                throw new IOException("Write failed for testing.");
            }
            super.save(tasks);
        }
    }
}
