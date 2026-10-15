package percy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * End-to-end tests of {@link Percy} through {@link Percy#getResponse}, the
 * same entry point the GUI uses. Each test gets its own temporary save file
 * so tests never touch the real {@code data/percy.txt} or each other.
 */
public class PercyTest {
    @TempDir
    Path tempDir;

    private Percy newPercy() {
        return new Percy(tempDir.resolve("percy.txt").toString());
    }

    @Test
    public void getWelcome_isTheFixedGreeting() {
        assertEquals("Hello! I'm Percy.\nWhat can I do for you?", newPercy().getWelcome());
    }

    @Test
    public void getResponse_todo_addsATaskAndReportsIt() {
        Percy percy = newPercy();
        String response = percy.getResponse("todo read book");
        assertTrue(response.contains("Got it. I've added this task:"));
        assertTrue(response.contains("[T][ ] read book"));
        assertTrue(response.contains("Now you have 1 tasks in the list."));
    }

    @Test
    public void getResponse_deadlineAndEvent_addBothTaskTypes() {
        Percy percy = newPercy();
        percy.getResponse("todo read book");
        percy.getResponse("deadline return book /by 2019-10-15");
        String response = percy.getResponse("event project /from 2019-10-10 /to 2019-10-12");

        assertTrue(response.contains("Now you have 3 tasks in the list."));

        String list = percy.getResponse("list");
        assertTrue(list.contains("1.[T][ ] read book"));
        assertTrue(list.contains("2.[D][ ] return book (by: Oct 15 2019)"));
        assertTrue(list.contains("3.[E][ ] project (from: Oct 10 2019 to: Oct 12 2019)"));
    }

    @Test
    public void getResponse_find_returnsOnlyMatchingTasks() {
        Percy percy = newPercy();
        percy.getResponse("todo read book");
        percy.getResponse("todo buy milk");

        String response = percy.getResponse("find book");

        assertTrue(response.contains("read book"));
        assertFalse(response.contains("buy milk"));
    }

    @Test
    public void getResponse_markThenUnmark_toggleTheStatusIcon() {
        Percy percy = newPercy();
        percy.getResponse("todo read book");

        String marked = percy.getResponse("mark 1");
        assertTrue(marked.contains("[T][X] read book"));

        String unmarked = percy.getResponse("unmark 1");
        assertTrue(unmarked.contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_delete_removesTheTaskAndUpdatesTheCount() {
        Percy percy = newPercy();
        percy.getResponse("todo read book");
        percy.getResponse("todo buy milk");

        String response = percy.getResponse("delete 1");

        assertTrue(response.contains("[T][ ] read book"));
        assertTrue(response.contains("Now you have 1 tasks in the list."));
        assertFalse(percy.getResponse("list").contains("read book"));
    }

    @Test
    public void getResponse_update_changesOnlyTheNamedField() {
        Percy percy = newPercy();
        percy.getResponse("deadline return book /by 2019-10-15");

        String response = percy.getResponse("update 1 /by 2019-12-25");

        assertTrue(response.contains("return book (by: Dec 25 2019)"));
    }

    @Test
    public void getResponse_bye_setsIsExit() {
        Percy percy = newPercy();
        assertFalse(percy.isExit());
        String response = percy.getResponse("bye");
        assertTrue(response.contains("Bye. Hope to see you again soon!"));
        assertTrue(percy.isExit());
    }

    @Test
    public void getResponse_unknownCommand_showsAnErrorAndDoesNotExit() {
        Percy percy = newPercy();
        String response = percy.getResponse("blah");
        assertTrue(response.contains("OOPS!!!"));
        assertFalse(percy.isExit());
    }

    @Test
    public void wasLastResponseError_tracksOnlyTheMostRecentCall() {
        Percy percy = newPercy();

        percy.getResponse("blah");
        assertTrue(percy.wasLastResponseError());

        percy.getResponse("todo read book");
        assertFalse(percy.wasLastResponseError());
    }

    @Test
    public void getResponse_invalidTaskNumber_showsAnErrorInstead() {
        Percy percy = newPercy();
        percy.getResponse("todo read book");
        String response = percy.getResponse("mark 5");
        assertTrue(response.contains("OOPS!!!"));
    }

    @Test
    public void tasksAddedInOneSession_arePersistedForTheNext() {
        Path file = tempDir.resolve("percy.txt");
        new Percy(file.toString()).getResponse("todo read book");

        Percy reloaded = new Percy(file.toString());

        assertTrue(reloaded.getResponse("list").contains("read book"));
    }
}
