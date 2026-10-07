package mary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import mary.ui.Ui;

/**
 * Tests the shared GUI/console session through an isolated reply destination.
 */
class MaryTest {
    @TempDir
    Path directory;

    /**
     * Checks a full command flow and reload without replacing any global streams.
     */
    @Test
    void execute_allCommands_routesRepliesAndPersistsState() throws Exception {
        Path file = directory.resolve("tasks.txt");
        List<String> replies = new ArrayList<>();
        Mary mary = new Mary(file.toString(), new Ui(replies::add, false));
        assertNull(mary.getLoadingError());
        assertFalse(Files.exists(file));
        assertFalse(mary.execute("todo read café book"));
        assertEquals(List.of(" Purr-fect! I've added this task:", "   [T][ ] read café book",
                " Tasks on your list: 1."), replies);
        mary.execute("deadline return book /by 2/12/2026 1800");
        mary.execute("event meeting /from 2/12/2026 1400 /to 3/12/2026 1600");
        mary.execute("mark 1");
        replies.clear();
        mary.execute("find book");
        assertEquals(List.of(" Look what I sniffed out:", " 1.[T][X] read café book",
                " 2.[D][ ] return book (by: 2 Dec 2026 18:00)"), replies);
        replies.clear();
        mary.execute("on 3/12/2026");
        assertEquals(List.of(" Tasks occurring on 2026-12-03:",
                " [E][ ] meeting (from: 2 Dec 2026 14:00 to: 3 Dec 2026 16:00)"), replies);
        mary.execute("unmark 1");
        mary.execute("delete 2");
        replies.clear();
        new Mary(file.toString(), new Ui(replies::add, false)).execute("list");
        assertEquals(List.of(" Here's your task lineup:", " 1.[T][ ] read café book",
                " 2.[E][ ] meeting (from: 2 Dec 2026 14:00 to: 3 Dec 2026 16:00)"), replies);
    }

    /**
     * Ensures invalid input is reported through the same destination as successes.
     */
    @Test
    void execute_invalidInput_reportsErrorAndAcceptsNextCommand() {
        assertTrue(Mary.class.desiredAssertionStatus(), "Run tests with Java assertions enabled");
        List<String> replies = new ArrayList<>();
        Mary mary = new Mary(directory.resolve("tasks.txt").toString(), new Ui(replies::add, false));
        for (String input : List.of("", "blah", "todo ", "mark", "deadline read /by tomorrow")) {
            replies.clear();
            assertFalse(mary.execute(input));
            assertEquals(1, replies.size());
            assertTrue(replies.getFirst().startsWith(" Error:"));
        }
        replies.clear();
        mary.execute("list");
        assertEquals(List.of(" Nothing to chase yet! Add a task with 'todo description'."), replies);
    }

    /**
     * Confirms that goodbye ends the session and subsequent commands cannot write.
     */
    @Test
    void execute_afterBye_doesNotChangeTasksOrRepeatFarewell() {
        Path file = directory.resolve("tasks.txt");
        List<String> replies = new ArrayList<>();
        Mary mary = new Mary(file.toString(), new Ui(replies::add, false));
        assertTrue(mary.execute("bye"));
        assertTrue(mary.execute("todo ignored"));
        assertTrue(mary.execute("bye"));
        assertEquals(List.of("Time for a catnap. See you soon!"), replies);
        assertFalse(Files.exists(file));
    }

    /**
     * Leaves a corrupt file untouched while exposing the startup error to the view.
     */
    @Test
    void constructor_corruptFile_reportsErrorWithoutOverwritingFile() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "broken\n");
        List<String> replies = new ArrayList<>();
        Mary mary = new Mary(file.toString(), new Ui(replies::add, false));
        assertTrue(mary.getLoadingError().contains("invalid record on line 1."));
        mary.execute("todo must not overwrite");
        assertTrue(replies.getLast().contains("saving is disabled"));
        replies.clear();
        mary.execute("list");
        assertEquals(List.of(" Nothing to chase yet! Add a task with 'todo description'."), replies);
        assertEquals("broken\n", Files.readString(file));
    }

    /**
     * Checks that a failed write produces an error instead of a success confirmation.
     */
    @Test
    void execute_unwritableFile_routesSaveFailureToView() {
        List<String> replies = new ArrayList<>();
        Mary mary = new Mary(directory.toString(), new Ui(replies::add, false));
        mary.execute("todo read");
        assertEquals(1, replies.size());
        assertTrue(replies.getFirst().contains("saving is disabled because loading failed"));
    }
}
