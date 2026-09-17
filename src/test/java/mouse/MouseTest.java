package mouse;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for GUI-facing command replies.
 */
public class MouseTest {
    @TempDir
    Path tempDir;

    private Mouse mouse() {
        return new Mouse(tempDir.resolve("mouse.txt").toString());
    }

    @Test
    public void getResponse_todo_returnsAddedConfirmation() {
        Mouse mouse = mouse();
        String reply = mouse.getResponse("todo read book");
        assertTrue(reply.contains("Stashed this crumb:"));
        assertTrue(reply.contains("read book"));
        assertTrue(reply.contains("1 crumb"));
        assertFalse(mouse.isExit("todo read book"));
    }

    @Test
    public void getResponse_unknownCommand_returnsError() {
        Mouse mouse = mouse();
        String reply = mouse.getResponse("cheese");
        assertTrue(reply.startsWith("SQUEAK!!!"));
        assertTrue(reply.contains("Mouse no understand"));
    }

    @Test
    public void isExit_bye_returnsTrue() {
        Mouse mouse = mouse();
        assertTrue(mouse.isExit("bye"));
        assertTrue(mouse.getResponse("bye").contains("off to nibble"));
    }

    @Test
    public void getResponse_priority_updatesExistingTask() {
        Mouse mouse = mouse();
        mouse.getResponse("todo read book");
        String reply = mouse.getResponse("priority 1 high");
        assertTrue(reply.contains("OK, Mouse tagged this crumb:"));
        assertTrue(reply.contains("priority: high"));
        String list = mouse.getResponse("list");
        assertTrue(list.contains("read book (priority: high)"));
    }

    @Test
    public void getResponse_afterRestart_keepsSavedTasks() {
        Path saveFile = tempDir.resolve("mouse.txt");
        Mouse first = new Mouse(saveFile.toString());
        first.getResponse("todo read book");
        first.getResponse("priority 1 high");

        Mouse second = new Mouse(saveFile.toString());
        String list = second.getResponse("list");
        assertTrue(list.contains("read book (priority: high)"));
    }

    @Test
    public void getResponse_help_returnsCommandGuide() {
        Mouse mouse = mouse();
        String reply = mouse.getResponse("help");
        assertTrue(reply.contains("todo DESCRIPTION"));
        assertTrue(reply.contains("deadline DESCRIPTION /by WHEN"));
        assertTrue(reply.contains("bye"));
        assertFalse(mouse.isExit("help"));
    }

    @Test
    public void getResponse_duplicateTodo_returnsError() {
        Mouse mouse = mouse();
        mouse.getResponse("todo read book");
        String reply = mouse.getResponse("todo read book");
        assertTrue(mouse.isErrorResponse(reply));
        assertTrue(reply.contains("already in the stash"));
        assertFalse(mouse.getResponse("list").contains("2."));
    }

    @Test
    public void getResponse_invalidDate_returnsError() {
        Mouse mouse = mouse();
        String reply = mouse.getResponse("deadline report /by 2019-02-30");
        assertTrue(mouse.isErrorResponse(reply));
        assertTrue(reply.contains("not a real one"));
    }

    @Test
    public void getResponse_listWithExtraWords_returnsError() {
        Mouse mouse = mouse();
        String reply = mouse.getResponse("list now");
        assertTrue(mouse.isErrorResponse(reply));
        assertTrue(reply.contains("does not take extra crumbs"));
        assertFalse(mouse.isExit("bye now"));
    }

    @Test
    public void getGreeting_corruptSaveFile_includesWarning() throws Exception {
        Path saveFile = tempDir.resolve("mouse.txt");
        java.nio.file.Files.writeString(saveFile, "not a task line\n");
        Mouse mouse = new Mouse(saveFile.toString());
        String greeting = mouse.getGreeting();
        assertTrue(greeting.contains("Squeak! I'm Mouse."));
        assertTrue(greeting.contains("mouldy"));
    }
}
