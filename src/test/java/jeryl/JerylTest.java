package jeryl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class JerylTest {
    @TempDir
    Path tempDir;

    private Jeryl newJeryl() {
        return new Jeryl(tempDir.resolve("jeryl.txt").toString());
    }

    @Test
    public void getResponse_todoWithHighPriority_addsTaskWithPriorityTag() {
        Jeryl jeryl = newJeryl();
        jeryl.getResponse("todo read book /priority high");

        String listing = jeryl.getResponse("list");
        assertTrue(listing.contains("[H][T][ ] read book"), listing);
    }

    @Test
    public void getResponse_todoWithoutPriority_addsTaskWithNoPriorityTag() {
        Jeryl jeryl = newJeryl();
        jeryl.getResponse("todo water the plants");

        String listing = jeryl.getResponse("list");
        assertTrue(listing.contains("[T][ ] water the plants") && !listing.contains("[H]") && !listing.contains("[M]")
                && !listing.contains("[L]"), listing);
    }

    @Test
    public void getResponse_todoWithInvalidPriority_returnsErrorAndDoesNotAddTask() {
        Jeryl jeryl = newJeryl();
        String response = jeryl.getResponse("todo read book /priority urgent");

        assertTrue(response.contains("Invalid priority \"urgent\""), response);
        assertTrue(jeryl.getResponse("list").isEmpty());
    }

    @Test
    public void getResponse_deadlineWithPriority_addsTaskWithPriorityTag() {
        Jeryl jeryl = newJeryl();
        jeryl.getResponse("deadline submit report /by 2026-09-20 /priority medium");

        String listing = jeryl.getResponse("list");
        assertTrue(listing.contains("[M][D][ ] submit report"), listing);
    }

    @Test
    public void getResponse_duplicateByFlagInDeadline_returnsErrorAndDoesNotAddTask() {
        Jeryl jeryl = newJeryl();
        String response = jeryl.getResponse("deadline submit report /by 2026-09-20 /by 2026-09-21");

        assertTrue(response.contains("only once"), response);
        assertTrue(jeryl.getResponse("list").isEmpty());
    }

    @Test
    public void getResponse_duplicatePriorityFlag_returnsErrorAndDoesNotAddTask() {
        Jeryl jeryl = newJeryl();
        String response = jeryl.getResponse("todo read book /priority high /priority low");

        assertTrue(response.contains("only once"), response);
        assertTrue(jeryl.getResponse("list").isEmpty());
    }

    @Test
    public void getResponse_priorityFlagWithNoLevel_returnsError() {
        Jeryl jeryl = newJeryl();
        String response = jeryl.getResponse("todo read book /priority ");

        assertTrue(response.contains("needs a level"), response);
        assertTrue(jeryl.getResponse("list").isEmpty());
    }

    @Test
    public void getResponse_eventEndBeforeStart_returnsErrorAndDoesNotAddTask() {
        Jeryl jeryl = newJeryl();
        String response = jeryl.getResponse("event party /from 2026-09-20 /to 2026-09-19");

        assertTrue(response.contains("must be after"), response);
        assertTrue(jeryl.getResponse("list").isEmpty());
    }

    @Test
    public void getResponse_eventEndEqualsStart_returnsErrorAndDoesNotAddTask() {
        Jeryl jeryl = newJeryl();
        String response = jeryl.getResponse("event party /from 2026-09-20 /to 2026-09-20");

        assertTrue(response.contains("must be after"), response);
        assertTrue(jeryl.getResponse("list").isEmpty());
    }

    @Test
    public void getResponse_duplicateFromFlagInEvent_returnsErrorAndDoesNotAddTask() {
        Jeryl jeryl = newJeryl();
        String response = jeryl.getResponse(
                "event party /from 2026-09-20 /from 2026-09-21 /to 2026-09-22");

        assertTrue(response.contains("only once"), response);
        assertTrue(jeryl.getResponse("list").isEmpty());
    }

    @Test
    public void getResponse_markWithNonNumericIndex_returnsErrorMessage() {
        Jeryl jeryl = newJeryl();
        jeryl.getResponse("todo read book");

        String response = jeryl.getResponse("mark abc");

        assertTrue(response.contains("must be a whole number"), response);
    }

    @Test
    public void getResponse_markWithOutOfRangeIndex_returnsErrorMessage() {
        Jeryl jeryl = newJeryl();
        jeryl.getResponse("todo read book");

        String response = jeryl.getResponse("mark 5");

        assertTrue(response.contains("no task with that number"), response);
    }

    @Test
    public void getResponse_unknownCommand_returnsErrorMessage() {
        Jeryl jeryl = newJeryl();
        String response = jeryl.getResponse("frobnicate");

        assertTrue(response.contains("don't know what that means"), response);
    }

    @Test
    public void getResponse_leadingAndTrailingWhitespaceInCommand_isHandledCorrectly() {
        Jeryl jeryl = newJeryl();
        jeryl.getResponse("  todo read book  ");

        String listing = jeryl.getResponse("list");
        assertTrue(listing.contains("[T][ ] read book"), listing);
    }

    @Test
    public void getResponse_findAfterDeletingAllTasks_returnsEmptyMessage() {
        Jeryl jeryl = newJeryl();
        jeryl.getResponse("todo read book");
        jeryl.getResponse("delete 1");

        String response = jeryl.getResponse("find book");

        assertTrue(response.equals("Here are the matching tasks in your list:\n"), response);
    }
}
