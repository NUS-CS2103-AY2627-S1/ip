package coco;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class CocoTest {

    @TempDir
    Path tempDir;

    private Coco newCoco() {
        return new Coco(tempDir.resolve("coco.txt").toString());
    }

    @Test
    public void greet_newInstance_returnsWelcomeMessage() {
        assertEquals("Hey hey, Coco here! 🌴\nWhat're we getting done today?", newCoco().greet());
    }

    @Test
    public void getResponse_addTodoThenList_reportsAdditionThenListsIt() {
        Coco coco = newCoco();

        String addReply = coco.getResponse("todo read book");
        assertTrue(addReply.contains("read book"));
        assertTrue(addReply.contains("That's 1 things cookin' now."));

        String listReply = coco.getResponse("list");
        assertEquals("Here's what's on your plate:\n1.[T][ ] read book", listReply);
    }

    @Test
    public void getResponse_markThenUnmark_flipsDoneStatusInReply() {
        Coco coco = newCoco();
        coco.getResponse("todo read book");

        String markReply = coco.getResponse("mark 1");
        assertTrue(markReply.contains("[X]"));

        String unmarkReply = coco.getResponse("unmark 1");
        assertTrue(unmarkReply.contains("[ ]"));
    }

    @Test
    public void getResponse_deleteOnlyTask_leavesEmptyList() {
        Coco coco = newCoco();
        coco.getResponse("todo read book");

        String deleteReply = coco.getResponse("delete 1");
        assertTrue(deleteReply.contains("That's 0 things left on the pile."));
        assertEquals("Here's what's on your plate:", coco.getResponse("list"));
    }

    @Test
    public void getResponse_markRecurringDeadline_advancesToNextOccurrenceInsteadOfMarkingDone() {
        Coco coco = newCoco();
        coco.getResponse("deadline standup /by 2026-01-06 /every weekly");

        String firstMarkReply = coco.getResponse("mark 1");
        assertTrue(firstMarkReply.contains("on repeat"));
        assertTrue(firstMarkReply.contains("Jan 13 2026"));
        assertTrue(firstMarkReply.contains("[ ]"));

        String secondMarkReply = coco.getResponse("mark 1");
        assertTrue(secondMarkReply.contains("Jan 20 2026"));
        assertTrue(secondMarkReply.contains("[ ]"));

        assertEquals("Here's what's on your plate:\n1.[D][ ] standup (by: Jan 20 2026) (every: weekly)",
                coco.getResponse("list"));
    }

    @Test
    public void getResponse_markNonRecurringDeadline_marksDoneNormally() {
        Coco coco = newCoco();
        coco.getResponse("deadline return book /by 2026-01-06");

        String markReply = coco.getResponse("mark 1");

        assertTrue(markReply.contains("Nice one, that's outta here"));
        assertTrue(markReply.contains("[X]"));
    }

    @Test
    public void getResponse_findMatchingKeyword_returnsOnlyMatchingTasks() {
        Coco coco = newCoco();
        coco.getResponse("todo read book");
        coco.getResponse("todo buy groceries");

        String findReply = coco.getResponse("find book");
        assertEquals("Here's what I dug up:\n1.[T][ ] read book", findReply);
    }

    @Test
    public void getResponse_commands_listsEveryCommandAndItsSyntax() {
        String reply = newCoco().getResponse("commands");

        assertTrue(reply.contains("todo <description>"));
        assertTrue(reply.contains("deadline <description> /by <yyyy-mm-dd>"));
        assertTrue(reply.contains("event <description> /from <start> /to <end>"));
        assertTrue(reply.contains("list"));
        assertTrue(reply.contains("find <keyword>"));
        assertTrue(reply.contains("mark <task number>"));
        assertTrue(reply.contains("unmark <task number>"));
        assertTrue(reply.contains("delete <task number>"));
        assertTrue(reply.contains("commands"));
        assertTrue(reply.contains("bye"));
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorMessageInsteadOfThrowing() {
        assertEquals("Whoa, lost me there, chief. Try somethin' else?", newCoco().getResponse("blah"));
    }

    @Test
    public void getResponse_bye_returnsGoodbyeMessage() {
        assertEquals("Catch you on the flip side! 🌊", newCoco().getResponse("bye"));
    }
}
