package windy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests command responses exposed by Windy to the GUI.
 */
public class WindyTest {

    @TempDir
    Path tempDirectory;

    @Test
    public void getWelcomeMessage_returnsConsoleWelcomeContent() {
        Windy windy = createWindy();

        String welcomeMessage = windy.getWelcomeMessage();

        assertTrue(welcomeMessage.contains("Hey there~ I'm Windy. What story shall we write today?"));
        assertTrue(welcomeMessage.contains("__        ___"));
    }

    @Test
    public void getResponse_addAndListCommands_returnsCommandResponses() {
        Windy windy = createWindy();

        String addResponse = windy.getResponse("todo read book");
        String listResponse = windy.getResponse("list");

        assertEquals(String.join(System.lineSeparator(),
                "A new verse for our story! I've added this task:",
                "  [T][ ] read book",
                "Now you have 1 tasks in the list."), addResponse);
        assertEquals(String.join(System.lineSeparator(),
                "Let's see what we've written so far:",
                "1.[T][ ] read book"), listResponse);
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorMessage() {
        Windy windy = createWindy();

        String response = windy.getResponse("nonsense");

        assertEquals("Unknown command 'nonsense'. Available commands: bye, list, mark, "
                + "unmark, delete, todo, deadline, event, date, find, undo, redo.", response);
        assertFalse(windy.isExitRequested());
    }

    @Test
    public void getResponse_emptyListAndUnmatchedQueries_showEmptyResults() {
        Windy windy = createWindy();

        assertEquals("Let's see what we've written so far:", windy.getResponse("list"));
        assertEquals("No tasks match 'missing'.", windy.getResponse("find missing"));
        assertEquals("No incomplete tasks relevant on 2026-09-03.",
                windy.getResponse("date 2026-9-3"));
    }

    @Test
    public void getResponse_findIncludesCompletedTasksInOriginalOrder() {
        Windy windy = createWindy();
        windy.getResponse("todo Read book");
        windy.getResponse("todo buy groceries");
        windy.getResponse("deadline book report /by 2026-9-3");
        windy.getResponse("mark 1");

        assertEquals(String.join(System.lineSeparator(),
                "There are 2 tasks that meet the requirements:",
                "[T][X] Read book",
                "[D][ ] book report (by: Sep 03 2026)"), windy.getResponse("find BOOK"));
    }

    @Test
    public void getResponse_byeCommand_returnsFarewellMessage() {
        Windy windy = createWindy();

        String response = windy.getResponse("bye");

        assertEquals(String.join(System.lineSeparator(),
                "Until our next song, may the wind carry you onward!",
                "______________________________________________"), response);
        assertTrue(windy.isExitRequested());
    }

    private Windy createWindy() {
        String filePath = tempDirectory.resolve("data/windy.txt").toString();
        return new Windy(filePath);
    }
}
