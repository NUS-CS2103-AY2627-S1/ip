package baemax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests for {@link Baemax#getResponse(String)}, the reply path shared by the CLI and the GUI. */
public class BaemaxTest {

    @TempDir
    Path tempDir;

    private Baemax freshBaemax() {
        return new Baemax(tempDir.resolve("tasks.txt").toString());
    }

    @Test
    public void getResponse_todo_confirmsAndCounts() {
        Baemax baemax = freshBaemax();
        String response = baemax.getResponse("todo read book");
        assertTrue(response.contains("[T][ ] read book"));
        assertTrue(response.contains("Now you have 1 tasks in the list."));
    }

    @Test
    public void getResponse_list_numbersTasksFromOne() {
        Baemax baemax = freshBaemax();
        baemax.getResponse("todo a");
        baemax.getResponse("todo b");
        String response = baemax.getResponse("list");
        assertEquals("Here are the tasks in your list:\n1. [T][ ] a\n2. [T][ ] b", response);
    }

    @Test
    public void getResponse_unknownCommand_returnsErrorMessage() {
        assertEquals(
                "Baemax does not know that command yet. "
                        + "Try help, todo, deadline, event, list, find, mark, unmark, delete, undo, or bye.",
                freshBaemax().getResponse("sing a song"));
    }

    @Test
    public void getResponse_bye_returnsFarewell() {
        assertEquals("Bye! I am satisfied with the care I have provided today. Have a lovely day!",
                freshBaemax().getResponse("bye"));
    }

    @Test
    public void getResponse_find_listsMatchesOnly() {
        Baemax baemax = freshBaemax();
        baemax.getResponse("todo read book");
        baemax.getResponse("todo buy milk");
        String response = baemax.getResponse("find book");
        assertTrue(response.contains("read book"));
        assertTrue(!response.contains("buy milk"));
    }

    @Test
    public void getResponse_undoWithNoHistory_throwsFriendlyMessage() {
        assertEquals("There's nothing to undo yet.", freshBaemax().getResponse("undo"));
    }

    @Test
    public void getResponse_undoAfterAdd_removesTheAddedTask() {
        Baemax baemax = freshBaemax();
        baemax.getResponse("todo read book");
        String response = baemax.getResponse("undo");
        assertFalse(response.contains("read book"));
        assertEquals("Here are the tasks in your list:", baemax.getResponse("list"));
    }

    @Test
    public void getResponse_undoAfterDelete_restoresTheRemovedTask() {
        Baemax baemax = freshBaemax();
        baemax.getResponse("todo read book");
        baemax.getResponse("delete 1");
        baemax.getResponse("undo");
        assertEquals("Here are the tasks in your list:\n1. [T][ ] read book", baemax.getResponse("list"));
    }

    @Test
    public void getResponse_undoAfterMark_revertsTheStatusNotJustTheReference() {
        Baemax baemax = freshBaemax();
        baemax.getResponse("todo read book");
        baemax.getResponse("mark 1");
        baemax.getResponse("undo");
        assertEquals("Here are the tasks in your list:\n1. [T][ ] read book", baemax.getResponse("list"));
    }

    @Test
    public void getResponse_undoTwiceInARow_secondUndoHasNothingLeft() {
        Baemax baemax = freshBaemax();
        baemax.getResponse("todo read book");
        baemax.getResponse("undo");
        assertEquals("There's nothing to undo yet.", baemax.getResponse("undo"));
    }

    @Test
    public void getResponse_listDoesNotClearUndoHistory() {
        Baemax baemax = freshBaemax();
        baemax.getResponse("todo read book");
        baemax.getResponse("list");
        String response = baemax.getResponse("undo");
        assertTrue(response.contains("Undone!"));
    }

    @Test
    public void getResponse_undoAfterFailedMark_undoesThePriorSuccessfulCommand() {
        Baemax baemax = freshBaemax();
        baemax.getResponse("todo read book");
        baemax.getResponse("mark 1");
        baemax.getResponse("mark 99"); // out of range, fails before any change
        baemax.getResponse("undo");
        assertEquals("Here are the tasks in your list:\n1. [T][ ] read book", baemax.getResponse("list"));
    }

    @Test
    public void getReply_successfulCommand_isNotAnError() {
        Baemax.Reply reply = freshBaemax().getReply("todo read book");
        assertFalse(reply.isError());
        assertTrue(reply.text().contains("read book"));
    }

    @Test
    public void getReply_bye_isNotAnError() {
        assertFalse(freshBaemax().getReply("bye").isError());
    }

    @Test
    public void getReply_invalidCommand_isAnError() {
        Baemax.Reply reply = freshBaemax().getReply("sing a song");
        assertTrue(reply.isError());
        assertTrue(reply.text().contains("does not know that command"));
    }

    @Test
    public void getReply_outOfRangeTaskNumber_isAnError() {
        assertTrue(freshBaemax().getReply("mark 1").isError());
    }

    @Test
    public void getResponse_help_listsEveryCommand() {
        String response = freshBaemax().getResponse("help");
        assertTrue(response.contains("todo"));
        assertTrue(response.contains("deadline"));
        assertTrue(response.contains("event"));
        assertTrue(response.contains("list"));
        assertTrue(response.contains("find"));
        assertTrue(response.contains("mark"));
        assertTrue(response.contains("unmark"));
        assertTrue(response.contains("delete"));
        assertTrue(response.contains("undo"));
        assertTrue(response.contains("bye"));
    }

    @Test
    public void getReply_help_isNotAnError() {
        assertFalse(freshBaemax().getReply("help").isError());
    }

    @Test
    public void getResponse_uppercaseCommandWord_isTreatedLikeLowercase() {
        Baemax baemax = freshBaemax();
        String response = baemax.getResponse("TODO read book");
        assertTrue(response.contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_mixedCaseCommandWord_isTreatedLikeLowercase() {
        Baemax baemax = freshBaemax();
        baemax.getResponse("todo read book");
        String response = baemax.getResponse("Mark 1");
        assertTrue(response.contains("[T][X] read book"));
    }

    @Test
    public void getResponse_commandWordCaseChangeDoesNotAffectDescriptionCase() {
        Baemax baemax = freshBaemax();
        String response = baemax.getResponse("TODO Read BOOK");
        assertTrue(response.contains("[T][ ] Read BOOK"));
    }

    @Test
    public void getResponse_mark_endsWithEncouragement() {
        Baemax baemax = freshBaemax();
        baemax.getResponse("todo read book");
        String response = baemax.getResponse("mark 1");
        assertTrue(response.contains("Great job looking after yourself!"));
    }

    @Test
    public void getResponse_unmark_endsWithReassurance() {
        Baemax baemax = freshBaemax();
        baemax.getResponse("todo read book");
        baemax.getResponse("mark 1");
        String response = baemax.getResponse("unmark 1");
        assertTrue(response.contains("No worries, take your time."));
    }

    @Test
    public void getResponse_help_hasACaringIntro() {
        assertTrue(freshBaemax().getResponse("help").contains("take care of your tasks"));
    }
}
