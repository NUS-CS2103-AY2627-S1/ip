package sheppy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Exercises complete command sequences and persistence after rejected input. */
class CommandSequenceTest {
    @TempDir
    private Path directory;

    /** Explains empty search results while preserving tasks and successful searches. */
    @Test
    void missingKeyword_returnsHelpfulMessage() {
        Sheppy app = new Sheppy(directory.resolve("search.txt").toString());
        String expected = "No matching tasks found. Try another keyword!";
        assertEquals(expected, app.getResponse("find pineapple"));
        app.getResponse("todo read book");
        String before = app.getResponse("list");
        assertEquals(expected, app.getResponse("find pineapple"));
        assertEquals(before, app.getResponse("list"));
        assertTrue(app.getResponse("find book").contains("1.[T][ ] read book"));
    }

    /** Verifies numbering, search, completion and deletion after sorting. */
    @Test
    void mixedCommands_preserveExpectedStateAcrossRestart() {
        String file = directory.resolve("tasks.txt").toString();
        Sheppy app = new Sheppy(file);
        assertEquals("", app.getStartupMessage());
        app.getResponse("todo zebra");
        app.getResponse("deadline apple /by 2026-09-18");
        app.getResponse("event middle /from Mon /to Tue");
        app.getResponse("sort");
        assertTrue(app.getResponse("mark 1").contains("[D][X] apple"));
        assertTrue(app.getResponse("unmark 1").contains("[D][ ] apple"));
        assertTrue(app.getResponse("delete 2").contains("[E][ ] middle"));
        String expected = "Here are the tasks in your list:" + System.lineSeparator()
                + "1.[D][ ] apple (by: Sep 18 2026)" + System.lineSeparator() + "2.[T][ ] zebra";
        for (String command : new String[]{"mark 0", "delete 3", "find", "sort extra", "unknown"}) {
            assertTrue(app.getResponse(command).startsWith("Baa-error:"));
            assertEquals(expected, app.getResponse("list"));
        }
        assertEquals(expected, new Sheppy(file).getResponse("list"));
        assertEquals("Here are the matching tasks in your list:" + System.lineSeparator()
                + "1.[T][ ] zebra", app.getResponse("find zebra"));
        assertTrue(app.getResponse("bye").startsWith("Baa-bye!"));
    }
}
