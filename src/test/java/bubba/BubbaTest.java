package bubba;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks complete command-response workflows across Bubba's main components.
 */
public class BubbaTest {
    @TempDir
    Path directory;

    @Test
    public void getResponse_taskLifecycle_updatesDisplayedList() {
        Bubba bubba = createBubba();

        assertEquals("Added new task:\n[T][ ] read book\nNumber of tasks in list: 1",
                bubba.getResponse("todo read book"));
        assertEquals("Good job, this task is done!\n[T][X] read book",
                bubba.getResponse("mark 1"));
        assertEquals("Current list of tasks:\n1. [T][X] read book",
                bubba.getResponse("list"));
        assertEquals("Removed the following task:\n[T][X] read book\nNumber of tasks in list: 0",
                bubba.getResponse("delete 1"));
        assertEquals("Current list of tasks:", bubba.getResponse("list"));
    }

    @Test
    public void getResponse_invalidThenValidCommand_resetsErrorState() {
        Bubba bubba = createBubba();

        assertEquals("My bad... Sorry, unsure what you mean by that.",
                bubba.getResponse("unknown"));
        assertTrue(bubba.isLastResponseError());

        assertTrue(bubba.getResponse("help").startsWith("Here are the commands I understand:"));
        assertFalse(bubba.isLastResponseError());
    }

    @Test
    public void getResponse_addThenReload_preservesTask() {
        Bubba firstSession = createBubba();
        firstSession.getResponse("deadline submit report /by 18/9/2026 2359");

        Bubba secondSession = createBubba();
        assertEquals("Current list of tasks:\n"
                        + "1. [D][ ] submit report (by: Sept 18 2026 23:59)",
                secondSession.getResponse("list"));
    }

    private Bubba createBubba() {
        return new Bubba(directory.resolve("data/bubba.txt").toString());
    }
}
