package bott;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests {@link Bott#getResponse(String)}, the entry point used by the GUI. The command handling
 * it delegates to (Parser's own parsing/validation, TaskList's own indexing, Storage's own
 * file format) is covered by those classes' tests; these cases instead check the wiring in
 * {@code executeCommand} and its helpers - that each command word reaches the right task-list
 * operation, that the resulting acknowledgement is built from the right pieces, and that changes
 * are actually persisted (and reloaded) via {@link bott.storage.Storage}.
 */
public class BottTest {

    @TempDir
    private Path tempDir;

    private Bott newBott() {
        return new Bott(tempDir.resolve("bott.txt").toString());
    }

    @Test
    public void getResponse_todoCommand_addsTaskAndConfirms() {
        String response = newBott().getResponse("todo read book");
        assertEquals(
                "Mission logged, recruit! Fall in:\n"
                        + "  [T][ ] read book\n"
                        + "You now have 1 mission(s) on the roster.",
                response);
    }

    @Test
    public void getResponse_listAfterAdds_showsTasksInOrder() {
        Bott bott = newBott();
        bott.getResponse("todo read book");
        bott.getResponse("todo return book");

        String response = bott.getResponse("list");

        assertEquals(
                "Roll call! Here's your mission roster:\n"
                        + "1.[T][ ] read book\n"
                        + "2.[T][ ] return book",
                response);
    }

    @Test
    public void getResponse_deadlineCommand_addsDeadlineTask() {
        String response = newBott().getResponse("deadline return book /by 2019-10-15");
        assertEquals(
                "Mission logged, recruit! Fall in:\n"
                        + "  [D][ ] return book (by: Oct 15 2019)\n"
                        + "You now have 1 mission(s) on the roster.",
                response);
    }

    @Test
    public void getResponse_eventCommand_addsEventTask() {
        String response = newBott().getResponse("event project meeting /from 2019-08-06 /to 2019-08-07");
        assertEquals(
                "Mission logged, recruit! Fall in:\n"
                        + "  [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)\n"
                        + "You now have 1 mission(s) on the roster.",
                response);
    }

    @Test
    public void getResponse_durationCommand_addsFixedDurationTask() {
        String response = newBott().getResponse("duration read sales report /for 1h30m");
        assertEquals(
                "Mission logged, recruit! Fall in:\n"
                        + "  [F][ ] read sales report (for: 1h 30m)\n"
                        + "You now have 1 mission(s) on the roster.",
                response);
    }

    @Test
    public void getResponse_durationWithInvalidTime_returnsErrorMessage() {
        assertEquals(
                "NEGATIVE, RECRUIT! \"2\" is not a valid duration. Use a number with a unit, e.g. 2h, 30m,"
                        + " or 1h30m.",
                newBott().getResponse("duration read sales report /for 2"));
    }

    @Test
    public void getResponse_unknownCommand_returnsErrorMessage() {
        String response = newBott().getResponse("blah");
        assertTrue(response.startsWith("NEGATIVE, RECRUIT! That's not an order I recognize: \"blah\"."));
    }

    @Test
    public void getResponse_missingTaskNumber_returnsErrorMessage() {
        assertEquals(
                "NEGATIVE, RECRUIT! Please specify a task number. Try: mark <task number>",
                newBott().getResponse("mark"));
    }

    @Test
    public void getResponse_markCommand_marksTaskAndConfirms() {
        Bott bott = newBott();
        bott.getResponse("todo read book");

        String response = bott.getResponse("mark 1");

        assertEquals("Outstanding! Mission accomplished:\n  [T][X] read book", response);
    }

    @Test
    public void getResponse_unmarkCommand_unmarksTaskAndConfirms() {
        Bott bott = newBott();
        bott.getResponse("todo read book");
        bott.getResponse("mark 1");

        String response = bott.getResponse("unmark 1");

        assertEquals("At ease. Mission's back on the roster:\n  [T][ ] read book", response);
    }

    @Test
    public void getResponse_deleteCommand_removesTaskAndShiftsRest() {
        Bott bott = newBott();
        bott.getResponse("todo read book");
        bott.getResponse("todo return book");

        String deleteResponse = bott.getResponse("delete 1");
        String listResponse = bott.getResponse("list");

        assertEquals(
                "Mission scrubbed, recruit! Fall out:\n"
                        + "  [T][ ] read book\n"
                        + "You now have 1 mission(s) on the roster.",
                deleteResponse);
        assertEquals("Roll call! Here's your mission roster:\n1.[T][ ] return book", listResponse);
    }

    @Test
    public void getResponse_findCommand_returnsOnlyMatchingTasks() {
        Bott bott = newBott();
        bott.getResponse("todo read book");
        bott.getResponse("todo join sports club");

        String response = bott.getResponse("find book");

        assertEquals("Found these missions matching your intel:\n1.[T][ ] read book", response);
    }

    @Test
    public void getResponse_findCommand_noMatches_showsHeaderOnly() {
        Bott bott = newBott();
        bott.getResponse("todo read book");

        String response = bott.getResponse("find zzz");

        assertEquals("Found these missions matching your intel:", response);
    }

    @Test
    public void getResponse_afterRestart_tasksPersistAcrossInstances() {
        Path filePath = tempDir.resolve("bott.txt");
        Bott firstRun = new Bott(filePath.toString());
        firstRun.getResponse("todo read book");
        firstRun.getResponse("mark 1");

        Bott secondRun = new Bott(filePath.toString());
        String response = secondRun.getResponse("list");

        assertEquals("Roll call! Here's your mission roster:\n1.[T][X] read book", response);
    }

    @Test
    public void getResponse_byeCommand_returnsGoodbyeMessage() {
        assertEquals("Dismissed! Fall out, recruit.", newBott().getResponse("bye"));
    }

    @Test
    public void getGreeting_returnsWelcomeMessage() {
        assertEquals(
                "Ten-hut! Sergeant Bott reporting for duty.\nWhat's your first order, recruit?",
                newBott().getGreeting());
    }

    @Test
    public void isExitCommand_onlyExactByeMatches() {
        assertTrue(Bott.isExitCommand("bye"));
        assertFalse(Bott.isExitCommand("byebye"));
        assertFalse(Bott.isExitCommand("list"));
    }
}
