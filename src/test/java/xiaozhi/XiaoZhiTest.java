package xiaozhi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * End-to-end tests of {@link XiaoZhi#getResponse}, the entry point the GUI
 * drives: parses one line of input, runs it against the real
 * {@code Parser}/{@code Command}/{@code TaskList}/{@code Storage} pipeline,
 * and hands back exactly what a user would see. Console-only behavior
 * ({@link XiaoZhi#run}, the banner/greeting) and the JavaFX layer itself
 * are outside this class's scope, per the "omit GUI functionality" note in
 * the A-MoreTesting exercise.
 */
public class XiaoZhiTest {

    @TempDir
    Path tempDir;

    private XiaoZhi newXiaoZhi() {
        return new XiaoZhi(tempDir.resolve("xiaozhi.txt").toString());
    }

    @Test
    public void getCommandType_beforeAnyResponse_isEmpty() {
        XiaoZhi xiaoZhi = newXiaoZhi();

        assertEquals("", xiaoZhi.getCommandType());
    }

    @Test
    public void isExit_beforeAnyResponse_isFalse() {
        XiaoZhi xiaoZhi = newXiaoZhi();

        assertFalse(xiaoZhi.isExit());
    }

    @Test
    public void getResponse_validTodo_reportsAddedAndTagsAddCommand() {
        XiaoZhi xiaoZhi = newXiaoZhi();

        String response = xiaoZhi.getResponse("todo read book");

        assertEquals(
                "It is done." + System.lineSeparator()
                        + "  [T][ ] read book" + System.lineSeparator()
                        + "Now 1 task(s) rest among your intentions.",
                response);
        assertEquals("AddCommand", xiaoZhi.getCommandType());
        assertFalse(xiaoZhi.isExit());
    }

    @Test
    public void getResponse_unrecognisedCommand_tagsError() {
        XiaoZhi xiaoZhi = newXiaoZhi();

        String response = xiaoZhi.getResponse("foobar");

        assertEquals("Reflect. I have not learned the word \"foobar\".", response);
        assertEquals("Error", xiaoZhi.getCommandType());
    }

    @Test
    public void getResponse_commandThatFailsDuringExecution_alsoTagsError() {
        XiaoZhi xiaoZhi = newXiaoZhi();

        String response = xiaoZhi.getResponse("mark 1");

        assertEquals("Reflect. There is no task 1 to speak of. Only 0 task(s) exist; look again before you act.",
                response);
        assertEquals("Error", xiaoZhi.getCommandType());
    }

    @Test
    public void getResponse_bye_signalsExitAndTagsExitCommand() {
        XiaoZhi xiaoZhi = newXiaoZhi();

        String response = xiaoZhi.getResponse("bye");

        assertEquals("Go now, and let your tasks find their season.", response);
        assertEquals("ExitCommand", xiaoZhi.getCommandType());
        assertTrue(xiaoZhi.isExit());
    }

    @Test
    public void getResponse_sequenceOfCommands_sharesStateAcrossCalls() {
        XiaoZhi xiaoZhi = newXiaoZhi();

        xiaoZhi.getResponse("todo read book");
        xiaoZhi.getResponse("mark 1");
        String listResponse = xiaoZhi.getResponse("list");

        assertEquals(
                "The tasks that occupy your mind:" + System.lineSeparator()
                        + "1.[T][X] read book",
                listResponse);
    }

    @Test
    public void getResponse_undoAfterAdd_reportsRemovalAndTagsUndoCommand() {
        XiaoZhi xiaoZhi = newXiaoZhi();
        xiaoZhi.getResponse("todo read book");

        String response = xiaoZhi.getResponse("undo");

        assertEquals(
                "What no longer serves you has been released." + System.lineSeparator()
                        + "  [T][ ] read book" + System.lineSeparator()
                        + "Now 0 task(s) remain.",
                response);
        assertEquals("UndoCommand", xiaoZhi.getCommandType());
    }

    @Test
    public void getResponse_afterErrorInput_undoStillReversesEarlierMutation() {
        // Guards against a naive implementation that pushes every parsed
        // command onto the undo history regardless of whether it failed.
        XiaoZhi xiaoZhi = newXiaoZhi();
        xiaoZhi.getResponse("todo read book");
        xiaoZhi.getResponse("mark 5");

        xiaoZhi.getResponse("undo");
        String listResponse = xiaoZhi.getResponse("list");

        assertEquals("The tasks that occupy your mind:", listResponse);
    }

    @Test
    public void getResponse_leadingWhitespaceBeforeCommandWord_isIgnored() {
        // A stray leading space used to make the command word look like ""
        // and be reported as unrecognised.
        XiaoZhi xiaoZhi = newXiaoZhi();

        String response = xiaoZhi.getResponse("  list");

        assertEquals("The tasks that occupy your mind:", response);
        assertEquals("ListCommand", xiaoZhi.getCommandType());
    }

    @Test
    public void getResponse_addingTheSameTaskTwice_reportsErrorAndKeepsOnlyOneCopy() {
        XiaoZhi xiaoZhi = newXiaoZhi();
        xiaoZhi.getResponse("todo read book");

        String response = xiaoZhi.getResponse("todo read book");

        assertEquals("Reflect. This task already lives among your intentions: [T][ ] read book", response);
        assertEquals("Error", xiaoZhi.getCommandType());
        assertEquals(
                "The tasks that occupy your mind:" + System.lineSeparator()
                        + "1.[T][ ] read book",
                xiaoZhi.getResponse("list"));
    }

    @Test
    public void newXiaoZhiOnSameFile_seesTasksSavedByAnEarlierInstance() {
        String filePath = tempDir.resolve("xiaozhi.txt").toString();
        new XiaoZhi(filePath).getResponse("todo read book");

        XiaoZhi reopened = new XiaoZhi(filePath);
        String listResponse = reopened.getResponse("list");

        assertEquals(
                "The tasks that occupy your mind:" + System.lineSeparator()
                        + "1.[T][ ] read book",
                listResponse);
    }
}
