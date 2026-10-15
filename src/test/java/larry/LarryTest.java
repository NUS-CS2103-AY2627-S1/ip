package larry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the command-response bridge shared with Larry's graphical interface.
 */
class LarryTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_multilineReplies_alignLinesAndPreserveDescriptionSpacing() {
        Larry larry = new Larry(temporaryDirectory.resolve("tasks.txt"));
        String added = larry.getResponse("todo read  two chapters");
        String listed = larry.getResponse("list");
        String deleted = larry.getResponse("delete 1");

        for (String response : new String[]{added, listed, deleted}) {
            assertTrue(response.lines().allMatch(line -> line.equals(line.stripLeading())));
            assertTrue(response.contains("read  two chapters"));
        }
        assertTrue(deleted.contains("0 tasks"));
    }

    @Test
    void getResponse_validTaskCommands_stateAndCommandTypeUpdated() {
        Larry larry = new Larry(temporaryDirectory.resolve("tasks.txt"));

        String addResponse = larry.getResponse("todo read book");
        assertTrue(addResponse.contains("EVIL LARRY has added this task for you:"));
        assertTrue(addResponse.contains("[T][ ] read book"));
        assertEquals("AddCommand", larry.getCommandType());

        String markResponse = larry.getResponse("mark 1");
        assertTrue(markResponse.contains("[T][X] read book"));
        assertEquals("MarkCommand", larry.getCommandType());

        String listResponse = larry.getResponse("list");
        assertTrue(listResponse.contains("1.[T][X] read book"));
        assertEquals("ListCommand", larry.getCommandType());
    }

    @Test
    void getResponse_invalidCommand_errorResponseAndTypeReturned() {
        Larry larry = new Larry(temporaryDirectory.resolve("tasks.txt"));

        String response = larry.getResponse("not a command");

        assertTrue(response.contains("ERROR: EVIL LARRY rejects that command."));
        assertEquals("Error", larry.getCommandType());
    }

    @Test
    void getResponse_taskAdded_newLarryInstanceLoadsSavedTask() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        assertFalse(Files.exists(dataFile));

        Larry originalLarry = new Larry(dataFile);
        originalLarry.getResponse("todo persist this task");

        assertTrue(Files.isRegularFile(dataFile));
        Larry reloadedLarry = new Larry(dataFile);
        String response = reloadedLarry.getResponse("list");

        assertTrue(response.contains("1.[T][ ] persist this task"));
    }

    @Test
    void getResponse_editTask_changeSavedAndLoadedWithStatusPreserved() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Larry originalLarry = new Larry(dataFile);
        originalLarry.getResponse("deadline submit draft /by 10-9-2026 0900");
        originalLarry.getResponse("mark 1");

        String editResponse = originalLarry.getResponse("edit 1 /by 10-9-2026 1200");
        Larry reloadedLarry = new Larry(dataFile);
        String listResponse = reloadedLarry.getResponse("list");

        assertTrue(editResponse.contains("EVIL LARRY updated task 1:"));
        assertTrue(editResponse.contains(
                "Before: [D][X] submit draft (by: 10 Sep 2026, 9:00 AM)"));
        assertTrue(editResponse.contains(
                "After:  [D][X] submit draft (by: 10 Sep 2026, 12:00 PM)"));
        assertTrue(listResponse.contains(
                "1.[D][X] submit draft (by: 10 Sep 2026, 12:00 PM)"));
        assertEquals("EditCommand", originalLarry.getCommandType());
    }

    @Test
    void getResponse_editDescriptionWithFieldLikeText_entireReplacementUsed() {
        Larry larry = new Larry(temporaryDirectory.resolve("tasks.txt"));
        larry.getResponse("todo original");

        String response = larry.getResponse(
                "edit 1 /description discuss /by and /from markers");

        assertTrue(response.contains(
                "After:  [T][ ] discuss /by and /from markers"));
    }

    @Test
    void getResponse_noOpEdit_successfulResponseReturned() {
        Larry larry = new Larry(temporaryDirectory.resolve("tasks.txt"));
        larry.getResponse("todo unchanged");

        String response = larry.getResponse("edit 1 /description unchanged");

        assertTrue(response.contains("Before: [T][ ] unchanged"));
        assertTrue(response.contains("After:  [T][ ] unchanged"));
        assertEquals("EditCommand", larry.getCommandType());
    }

    @Test
    void getResponse_incompatibleAndInvalidEdits_taskUnchanged() {
        Larry larry = new Larry(temporaryDirectory.resolve("tasks.txt"));
        larry.getResponse("event meeting /from 10-9-2026 0900 /to 10-9-2026 1000");

        assertEquals("ERROR: You cannot edit /by on an event task.",
                larry.getResponse("edit 1 /by 10-9-2026 1200"));
        assertEquals("ERROR: The value for /from is not a valid date and time.",
                larry.getResponse("edit 1 /from tomorrow"));
        assertEquals("ERROR: An event must end after it starts.",
                larry.getResponse("edit 1 /to 10-9-2026 0900"));

        String listResponse = larry.getResponse("list");
        assertTrue(listResponse.contains(
                "1.[E][ ] meeting (from: 10 Sep 2026, 9:00 AM to: 10 Sep 2026, 10:00 AM)"));
    }
}
