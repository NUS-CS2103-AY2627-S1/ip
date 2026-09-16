package discitrack;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks factual response content without fixing the coach's entire wording.
 */
public class ResponseAccuracyTest {
    @TempDir
    public Path directory;

    @Test
    public void addTasks_responses_reportDescriptionAndCorrectCount() {
        DisciTrack app = newApp();
        String first = app.getResponse("todo first task");
        assertTrue(first.contains("first task"));
        assertTrue(first.contains("1 task."));
        assertFalse(first.contains("1 tasks"));
        String second = app.getResponse("todo second task");
        assertTrue(second.contains("second task"));
        assertTrue(second.contains("2 tasks."));
    }

    @Test
    public void deleteTask_response_identifiesRemovedTaskAndRemainingCount() {
        DisciTrack app = newApp();
        app.getResponse("todo first");
        app.getResponse("todo second");
        String response = app.getResponse("delete 1");
        assertTrue(response.contains("[T] [ ] first"));
        assertTrue(response.contains("1 task"));
        assertFalse(response.contains("[T] [ ] second"));
    }

    @Test
    public void emptyList_response_explainsThereAreNoTasks() {
        DisciTrack app = newApp();
        String response = app.getResponse("list");
        assertFalse(app.hasResponseError());
        assertTrue(response.contains("Nothing on the board"));
        assertFalse(response.contains("1."));
    }

    @Test
    public void findNoMatches_response_suggestsAnotherSearch() {
        DisciTrack app = newApp();
        app.getResponse("todo exercise");
        String response = app.getResponse("find missing");
        assertFalse(app.hasResponseError());
        assertTrue(response.contains("Nothing on the radar"));
        assertTrue(response.contains("another keyword"));
        assertFalse(response.contains("[T]"));
    }

    @Test
    public void dateNoMatches_response_explainsThereAreNoEventsOrDeadlines() {
        DisciTrack app = newApp();
        app.getResponse("deadline report /by 2026-09-30");
        String response = app.getResponse("checkdate 2026-10-01");
        assertFalse(app.hasResponseError());
        assertTrue(response.contains("no deadlines or events"));
        assertFalse(response.contains("[D]"));
    }

    @Test
    public void validCommandAfterError_resetsErrorFlagAndReplacesResponse() {
        DisciTrack app = newApp();
        app.getResponse("unknown");
        assertTrue(app.hasResponseError());
        String response = app.getResponse("todo recover");
        assertFalse(app.hasResponseError());
        assertTrue(response.contains("recover"));
        assertFalse(response.contains("don't recognise"));
    }

    private DisciTrack newApp() {
        return new DisciTrack(directory.resolve("tasks.txt").toString());
    }
}
