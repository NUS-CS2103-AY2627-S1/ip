package waffles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the chatbot coordinator and its user-facing error responses. */
class WafflesTest {
    /** Temporary folder used to isolate each test's task data. */
    @TempDir
    Path temporaryFolder;

    /** Verifies repeated whitespace is accepted without changing the task description. */
    @Test
    void getResponse_repeatedWhitespace_addsNormalizedTask() {
        Waffles waffles = new Waffles(storage());

        String response = waffles.getResponse("  todo    plan   the week  ");

        assertTrue(response.contains("[T][ ] plan the week"));
        assertTrue(waffles.getResponse("list").contains("1.[T][ ] plan the week"));
    }

    /** Verifies blank GUI input receives a correction instead of being ignored or crashing. */
    @Test
    void getResponse_blankInput_returnsHelpfulError() {
        Waffles waffles = new Waffles(storage());

        String response = waffles.getResponse("   ");

        assertTrue(response.contains("please enter a command"));
    }

    /** Verifies invalid scheduling data is rejected before it is persisted. */
    @Test
    void getResponse_reversedEvent_keepsTaskListUnchanged() {
        Waffles waffles = new Waffles(storage());

        String response = waffles.getResponse("event trip /from 2026-09-20 /to 2026-09-19");

        assertTrue(response.contains("must end on or after it starts"));
        String listResponse = waffles.getResponse("list");
        assertTrue(listResponse.contains("Here are the tasks in your list:"));
        assertFalse(listResponse.contains("trip"));
    }

    /** Creates isolated storage for one chatbot test. */
    private Storage storage() {
        return new Storage(temporaryFolder.resolve("waffles.txt").toString());
    }
}
