package waddles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class WaddlesTest {

    @TempDir
    private Path tempDir;

    private Waddles newWaddles() {
        return new Waddles(tempDir.resolve("waddles.txt").toString());
    }

    @Test
    public void undo_afterAddingTask_removesTheAddedTask() {
        Waddles waddles = newWaddles();
        waddles.getResponse("todo read book");

        waddles.getResponse("undo");

        assertEquals("Your trough is empty — nothing to snort about yet!", waddles.getResponse("list"));
    }

    @Test
    public void undo_afterMarkingTask_revertsToNotDone() {
        Waddles waddles = newWaddles();
        waddles.getResponse("todo read book");
        waddles.getResponse("mark 1");

        waddles.getResponse("undo");

        assertTrue(waddles.getResponse("list").contains("[T][ ] read book"));
    }

    @Test
    public void mark_recurringDeadline_confirmationShowsJustCompletedOccurrenceAsDone() {
        Waddles waddles = newWaddles();
        waddles.getResponse("deadline pay rent /by 2024-01-01 /every month");

        String response = waddles.getResponse("mark 1");

        // The confirmation should prove the occurrence just marked (Jan, now done) was
        // completed, not the next booked occurrence (Feb, still unmarked) it advances to.
        assertTrue(response.contains("[D][X] pay rent (by: Jan 1 2024) (every month)"));
        assertTrue(!response.contains("Feb 1 2024"));

        String list = waddles.getResponse("list");
        assertTrue(list.contains("[D][ ] pay rent (by: Feb 1 2024) (every month)"));
    }

    @Test
    public void undo_afterDeletingTask_restoresTheTask() {
        Waddles waddles = newWaddles();
        waddles.getResponse("todo read book");

        waddles.getResponse("delete 1");
        waddles.getResponse("undo");

        assertTrue(waddles.getResponse("list").contains("read book"));
    }

    @Test
    public void undo_withNoPriorMutation_returnsErrorMessage() {
        Waddles waddles = newWaddles();

        String response = waddles.getResponse("undo");

        assertTrue(response.contains("nothing to undo"));
    }

    @Test
    public void undo_calledTwiceInARow_secondCallHasNothingLeftToUndo() {
        Waddles waddles = newWaddles();
        waddles.getResponse("todo read book");

        waddles.getResponse("undo");
        String secondUndoResponse = waddles.getResponse("undo");

        assertTrue(secondUndoResponse.contains("nothing to undo"));
    }

    @Test
    public void undo_onlyRevertsTheSingleMostRecentCommand() {
        Waddles waddles = newWaddles();
        waddles.getResponse("todo read book");
        waddles.getResponse("todo return book");

        waddles.getResponse("undo");
        String list = waddles.getResponse("list");

        assertTrue(list.contains("read book"));
        assertTrue(!list.contains("return book"));
    }
}
