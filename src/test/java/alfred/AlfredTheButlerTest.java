package alfred;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the two methods the window uses, {@link AlfredTheButler#getGreeting()}
 * and {@link AlfredTheButler#getResponse(String)}: that a line is really
 * carried out, and that the reply arrives without the console's dividers.
 */
public class AlfredTheButlerTest {
    private String saveFile;
    private AlfredTheButler alfred;

    @BeforeEach
    public void setUp(@TempDir Path tempDir) {
        saveFile = tempDir.resolve("tasks.txt").toString();
        alfred = new AlfredTheButler(saveFile);
    }

    @Test
    public void getGreeting_noSaveFile_greetsWithoutMentioningTasks() {
        assertEquals("Good day, sir. Alfred Pennyworth, at your disposal.\nWhat may I do for you?",
                alfred.getGreeting());
    }

    @Test
    public void getGreeting_savedTasksFromLastRun_restoredCountReported() {
        alfred.getResponse("todo polish the silver");
        alfred.getResponse("todo walk the dog");

        AlfredTheButler nextRun = new AlfredTheButler(saveFile);
        assertEquals("Good day, sir. Alfred Pennyworth, at your disposal.\n"
                + "What may I do for you?\n"
                + "I've brought back 2 tasks from last time, sir.",
                nextRun.getGreeting());
    }

    @Test
    public void getResponse_todoCommand_taskAddedAndConfirmed() {
        assertEquals("Very good, sir. I've added this task:\n"
                + "  [T][ ] polish the silver\n"
                + "That makes 1 task on your list.",
                alfred.getResponse("todo polish the silver"));
    }

    @Test
    public void getResponse_listAfterAdding_taskListed() {
        alfred.getResponse("todo polish the silver");

        assertEquals("Here are the tasks on your list, sir:\n"
                + "1.[T][ ] polish the silver",
                alfred.getResponse("list"));
    }

    @Test
    public void getResponse_listOfSeveralTasks_numberedFromOne() {
        alfred.getResponse("todo polish the silver");
        alfred.getResponse("todo walk the dog");

        assertEquals("Here are the tasks on your list, sir:\n"
                + "1.[T][ ] polish the silver\n"
                + "2.[T][ ] walk the dog",
                alfred.getResponse("list"));
    }

    @Test
    public void getResponse_listWhenNothingIsStored_headingOnly() {
        assertEquals("Here are the tasks on your list, sir:", alfred.getResponse("list"));
    }

    @Test
    public void getResponse_findMatchingSomeTasks_numberedByPlaceInWholeList() {
        alfred.getResponse("todo polish the silver");
        alfred.getResponse("todo walk the dog");
        alfred.getResponse("todo polish the boots");

        // 1 and 3, not 1 and 2: the numbers are the ones `mark` and `delete` take.
        assertEquals("Here are the matching tasks on your list, sir:\n"
                + "1.[T][ ] polish the silver\n"
                + "3.[T][ ] polish the boots",
                alfred.getResponse("find polish"));
    }

    @Test
    public void getResponse_findMatchingNothing_noMatchesReported() {
        alfred.getResponse("todo polish the silver");

        assertEquals("I found no matching tasks, sir.", alfred.getResponse("find umbrella"));
    }

    @Test
    public void getResponse_onDayWithTasks_numberedByPlaceInWholeList() {
        alfred.getResponse("todo polish the silver");
        alfred.getResponse("deadline return book /by 2019-10-15");
        alfred.getResponse("todo walk the dog");
        alfred.getResponse("event gala /from 2019-10-14 /to 2019-10-16");

        assertEquals("Here is what you have on Oct 15 2019:\n"
                + "2.[D][ ] return book (by: Oct 15 2019)\n"
                + "4.[E][ ] gala (from: Oct 14 2019 to: Oct 16 2019)",
                alfred.getResponse("on 2019-10-15"));
    }

    @Test
    public void getResponse_onDayWithNoTasks_nothingOnThatDayReported() {
        alfred.getResponse("todo polish the silver");

        assertEquals("You have nothing on Oct 15 2019, sir.", alfred.getResponse("on 2019-10-15"));
    }

    @Test
    public void getResponse_markCommand_taskMarkedDone() {
        alfred.getResponse("todo polish the silver");

        assertEquals("Splendid, sir. I've marked this task as done:\n"
                + "  [T][X] polish the silver",
                alfred.getResponse("mark 1"));
    }

    @Test
    public void getResponse_unknownCommand_refusedAndListUntouched() {
        String reply = alfred.getResponse("polish the silver");

        assertFalse(reply.isEmpty(), "an unknown command should be answered");
        assertEquals("Here are the tasks on your list, sir:", alfred.getResponse("list").split("\n")[0]);
    }

    @Test
    public void getResponse_markPastEndOfList_refusedNotThrown() {
        assertEquals("There is no such task, sir.", alfred.getResponse("mark 3"));
    }

    @Test
    public void getResponse_spacesAroundCommand_commandStillRecognized() {
        assertEquals("Here are the tasks on your list, sir:", alfred.getResponse("   list   "));
    }

    @Test
    public void getResponse_nothingButSpaces_inputRequested() {
        assertEquals("You'll have to give me something to work with, sir.", alfred.getResponse("   "));
    }

    @Test
    public void getCommandType_afterEachKindOfCommand_commandClassNamed() {
        alfred.getResponse("todo polish the silver");
        assertEquals("AddCommand", alfred.getCommandType());

        alfred.getResponse("mark 1");
        assertEquals("MarkCommand", alfred.getCommandType());

        alfred.getResponse("delete 1");
        assertEquals("DeleteCommand", alfred.getCommandType());
    }

    @Test
    public void getCommandType_beforeAnyCommand_null() {
        assertNull(alfred.getCommandType());
    }

    @Test
    public void getCommandType_refusedCommand_nullRatherThanThePreviousKind() {
        alfred.getResponse("todo polish the silver");

        alfred.getResponse("mark 9");
        assertNull(alfred.getCommandType());
    }

    @Test
    public void isLastResponseError_beforeAnyCommand_false() {
        assertFalse(alfred.isLastResponseError());
    }

    @Test
    public void isLastResponseError_commandCarriedOut_false() {
        alfred.getResponse("todo polish the silver");
        assertFalse(alfred.isLastResponseError());
    }

    @Test
    public void isLastResponseError_unknownCommand_true() {
        alfred.getResponse("blah");
        assertTrue(alfred.isLastResponseError());
    }

    @Test
    public void isLastResponseError_noSuchTask_true() {
        alfred.getResponse("todo polish the silver");

        alfred.getResponse("mark 9");
        assertTrue(alfred.isLastResponseError());
    }

    @Test
    public void isLastCommandExit_beforeAnyCommand_false() {
        assertFalse(alfred.isLastCommandExit());
    }

    @Test
    public void isLastCommandExit_byeCommand_true() {
        alfred.getResponse("bye");
        assertTrue(alfred.isLastCommandExit());
    }

    @Test
    public void isLastCommandExit_ordinaryCommand_false() {
        alfred.getResponse("todo polish the silver");
        assertFalse(alfred.isLastCommandExit());
    }

    @Test
    public void isLastCommandExit_refusedLineAfterBye_falseAgain() {
        // A refusal cannot end the session, even straight after a bye.
        alfred.getResponse("bye");
        alfred.getResponse("blah");
        assertFalse(alfred.isLastCommandExit());
    }

    @Test
    public void isLastResponseError_commandAfterAnError_falseAgain() {
        alfred.getResponse("blah");

        alfred.getResponse("todo polish the silver");
        assertFalse(alfred.isLastResponseError());
    }

    @Test
    public void getResponse_byeCommand_farewellReturned() {
        assertEquals("Very good, sir. I shall be here when you need me.", alfred.getResponse("bye"));
    }

    @Test
    public void getResponse_anyCommand_replyCarriesNoConsoleDecoration() {
        String reply = alfred.getResponse("todo polish the silver");

        assertFalse(reply.contains("____"), "a dialog box draws no dividers: " + reply);
        assertFalse(reply.startsWith(" "), "a dialog box adds no indent: " + reply);
    }
}
