package sage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;

import org.junit.jupiter.api.Test;

class SageIntegrationTest {
    @Test
    void processesAddListFindDeleteAndExitCommands() throws Exception {
        Sage sage = new Sage(Files.createTempFile("sage", ".txt").toString());

        assertTrue(sage.processCommand("todo read book").contains("Now you have 1 task"));
        assertEquals("Here are the matching tasks in your list:\n1.[T][ ] read book",
                sage.processCommand("find book"));
        assertEquals("Noted. I've removed this task:\n  [T][ ] read book\nNow you have 0 tasks in the list.",
                sage.processCommand("delete 1"));
        assertEquals("Bye. Hope to see you again soon!", sage.processCommand("bye"));
        assertTrue(sage.isExitCommand("bye"));
    }

    @Test
    void invalidCommandsReturnHelpfulMessages() throws Exception {
        Sage sage = new Sage(Files.createTempFile("sage", ".txt").toString());

        assertTrue(sage.processCommand("unknown").contains("I didn't quite catch that"));
        assertTrue(sage.processCommand("mark one").contains("task number should be a number"));
        assertTrue(sage.processCommand("delete 1").contains("couldn't find task 1"));
    }
}
