package chris;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests complete command handling through the chatbot coordinator.
 */
public class ChrisTest {
    @TempDir
    private Path temporaryDirectory;

    /**
     * Verifies that adding and listing a task works through the complete command flow.
     */
    @Test
    public void getResponse_addAndListTodo_returnsExpectedResponses() {
        Chris chris = createChris();

        String addResponse = chris.getResponse("todo read book");
        String listResponse = chris.getResponse("list");

        assertTrue(addResponse.contains("[T][ ] read book"));
        assertTrue(listResponse.contains("1.[T][ ] read book"));
    }

    /**
     * Verifies that mark, unmark, and delete commands update the same task.
     */
    @Test
    public void getResponse_taskMutations_updateTaskState() {
        Chris chris = createChris();
        chris.getResponse("todo read book");

        assertTrue(chris.getResponse("mark 1").contains("[T][X] read book"));
        assertTrue(chris.getResponse("unmark 1").contains("[T][ ] read book"));
        assertTrue(chris.getResponse("delete 1").contains("Now you have 0 tasks"));
    }

    /**
     * Verifies that matching tasks can be found through a find command.
     */
    @Test
    public void getResponse_findMatchingTask_returnsMatch() {
        Chris chris = createChris();
        chris.getResponse("todo read book");
        chris.getResponse("todo exercise");

        String response = chris.getResponse("find book");

        assertTrue(response.contains("read book"));
        assertFalse(response.contains("exercise"));
    }

    /**
     * Verifies that invalid and duplicate commands produce user-facing errors.
     */
    @Test
    public void getResponse_invalidAndDuplicateInput_returnsErrors() {
        Chris chris = createChris();
        chris.getResponse("todo read book");

        assertTrue(chris.getResponse("dance").startsWith("OOPS!!!"));
        assertEquals("OOPS!!! That task is already in your list.",
                chris.getResponse("todo Read Book"));
    }

    /**
     * Verifies that saved tasks are restored by a new chatbot instance.
     */
    @Test
    public void constructor_existingData_restoresSavedTasks() {
        Path filePath = this.temporaryDirectory.resolve("data").resolve("chris.txt");
        Chris firstSession = new Chris(filePath);
        firstSession.getResponse("deadline return book /by 2026-09-30");

        Chris secondSession = new Chris(filePath);

        assertTrue(secondSession.getResponse("list").contains("return book (by: Sep 30 2026)"));
    }

    /**
     * Verifies that the bye command requests application shutdown.
     */
    @Test
    public void getResponse_bye_requestsExit() {
        Chris chris = createChris();

        String response = chris.getResponse("bye");

        assertEquals("Bye. Hope to see you again soon!", response);
        assertTrue(chris.isExitRequested());
    }

    private Chris createChris() {
        return new Chris(this.temporaryDirectory.resolve("data").resolve("chris.txt"));
    }
}
