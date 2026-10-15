package cheecken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks the GUI's response boundary, independent sessions, and persistence failures.
 */
class CheeckenResponseTest {
    @TempDir
    private Path directory;

    /**
     * Verifies get response with commands: preserves state and returns plain text.
     */
    @Test
    void getResponse_commands_preservesStateAndReturnsPlainText() {
        Cheecken chatbot = new Cheecken(directory.resolve("tasks.txt").toString());
        assertEquals("Got it. I've added this task:\n  [T][ ] read book\nNow you have 1 tasks in the list.",
                chatbot.getResponse("todo read book"));
        chatbot.getResponse("mark 1");
        assertEquals("Here are the tasks in your list:\n1.[T][X] read book", chatbot.getResponse("list"));
        assertEquals("Here are the matching tasks in your list:\n1.[T][X] read book",
                chatbot.getResponse("find book"));
        chatbot.getResponse("unmark 1");
        chatbot.getResponse("delete 1");
        assertEquals("Here are the tasks in your list:", chatbot.getResponse("list"));
    }

    /**
     * Verifies initialize with repeated calls: does not duplicate saved tasks.
     */
    @Test
    void initialize_repeatedCalls_doesNotDuplicateSavedTasks() {
        String path = directory.resolve("tasks.txt").toString();
        new Cheecken(path).getResponse("todo saved task");
        Cheecken restored = new Cheecken(path);
        restored.initialize();
        restored.initialize();
        assertEquals("Here are the tasks in your list:\n1.[T][ ] saved task", restored.getResponse("list"));
        Cheecken separate = new Cheecken(directory.resolve("other.txt").toString());
        assertEquals("Here are the tasks in your list:", separate.getResponse("list"));
    }

    /**
     * Verifies get response with invalid command: returns error and remains usable.
     */
    @Test
    void getResponse_invalidCommand_returnsErrorAndRemainsUsable() {
        Cheecken chatbot = new Cheecken(directory.resolve("tasks.txt").toString());
        assertEquals("Task index out of range", chatbot.getResponse("mark 1"));
        assertFalse(chatbot.isFinished());
        assertTrue(chatbot.hasResponseError());
        assertTrue(chatbot.getResponse("todo recover").contains("[T][ ] recover"));
        assertFalse(chatbot.hasResponseError());
    }

    /**
     * Verifies get response with bye: ends session without discarding farewell.
     */
    @Test
    void getResponse_bye_endsSessionWithoutDiscardingFarewell() {
        Cheecken chatbot = new Cheecken(directory.resolve("tasks.txt").toString());
        assertEquals("Bye. Hope to see you again soon!", chatbot.getResponse("bye"));
        assertTrue(chatbot.isFinished());
        assertEquals("This conversation has ended. Close the window to exit.", chatbot.getResponse("todo ignored"));
    }

    /**
     * Verifies get response with save failure: includes warning in reply.
     * @throws Exception if test setup, execution, or file access fails
     */
    @Test
    void getResponse_saveFailure_includesWarningInReply() throws Exception {
        Path blocked = directory.resolve("blocked");
        Files.writeString(blocked, "This is a file, not a directory.");
        Cheecken chatbot = new Cheecken(blocked.resolve("tasks.txt").toString());
        assertTrue(chatbot.getResponse("todo unsaved task").contains("Unable to save tasks:"));
        assertTrue(chatbot.hasResponseError());
        chatbot.getResponse("list");
        assertFalse(chatbot.hasResponseError());
    }

    /**
     * Verifies initialize with load failure: marks warning and resets for next response.
     */
    @Test
    void initialize_loadFailure_marksWarningAndResetsForNextResponse() {
        Cheecken chatbot = new Cheecken(directory.toString());
        assertTrue(chatbot.initialize().contains("Unable to load tasks:"));
        assertTrue(chatbot.hasResponseError());
        chatbot.getResponse("list");
        assertFalse(chatbot.hasResponseError());
    }

}
