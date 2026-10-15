package cheecken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Checks rejected commands cannot mutate tasks or saved data and do not end the session.
 */
class CommandValidationTest {
    @TempDir
    private Path directory;

    /**
     * Verifies get response with invalid command: preserves memory and storage.
     * @param command command name used in error guidance
     * @throws Exception if test setup, execution, or file access fails
     */
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "unknown", "todoSuffix", "todo", "todo   ", "find", "find   ",
        "deadline", "deadline /by 2026-09-18", "deadline work /by", "event", "event /from today /to tomorrow",
        "event work /from /to tomorrow", "event work /from today /to", "event work /to today /from tomorrow",
        "mark", "mark 0", "mark -1", "mark 2", "mark abc", "mark 2147483648", "mark 1 2",
        "unmark", "unmark 0", "unmark -1", "unmark 2", "unmark abc",
        "delete", "delete 0", "delete -1", "delete 2", "delete abc"})
    void getResponse_invalidCommand_preservesMemoryAndStorage(String command) throws Exception {
        Path file = directory.resolve("tasks.txt");
        Cheecken chatbot = new Cheecken(file.toString());
        chatbot.getResponse("todo keep");
        String saved = Files.readString(file);
        String response = chatbot.getResponse(command);
        assertFalse(response.isBlank());
        assertTrue(chatbot.hasResponseError(), () -> "Expected an error for: " + command);
        assertFalse(chatbot.isFinished());
        assertEquals(saved, Files.readString(file));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] keep", chatbot.getResponse("list"));
        assertFalse(chatbot.hasResponseError());
    }

    /**
     * Verifies get response with mixed case and whitespace: executes commands and persists changes.
     * @throws Exception if test setup, execution, or file access fails
     */
    @Test
    void getResponse_mixedCaseAndWhitespace_executesCommandsAndPersistsChanges() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Cheecken chatbot = new Cheecken(file.toString());
        chatbot.getResponse("  ToDo first  task  ");
        chatbot.getResponse("TODO second");
        chatbot.getResponse("MARK\t2");
        assertEquals("T | 0 | first  task\nT | 1 | second\n", Files.readString(file));
        chatbot.getResponse("UNMARK 2");
        assertEquals("T | 0 | first  task\nT | 0 | second\n", Files.readString(file));
        assertEquals("Noted. I've removed this task:\n  [T][ ] first  task", chatbot.getResponse("DELETE 1"));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] second", chatbot.getResponse("LIST"));
        assertEquals("T | 0 | second\n", Files.readString(file));
        chatbot.getResponse("delete 1");
        assertEquals("", Files.readString(file));
    }

    /**
     * Verifies get response with after bye: does not execute or change storage.
     * @throws Exception if test setup, execution, or file access fails
     */
    @Test
    void getResponse_afterBye_doesNotExecuteOrChangeStorage() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Cheecken chatbot = new Cheecken(file.toString());
        chatbot.getResponse("todo keep");
        chatbot.getResponse("bye");
        assertTrue(chatbot.isFinished());
        assertEquals("This conversation has ended. Close the window to exit.", chatbot.getResponse("delete 1"));
        assertFalse(chatbot.hasResponseError());
        assertEquals("T | 0 | keep\n", Files.readString(file));
    }

    /**
     * Verifies initialize with after load failure: clears warning without retrying.
     */
    @Test
    void initialize_afterLoadFailure_clearsWarningWithoutRetrying() {
        Cheecken chatbot = new Cheecken(directory.toString());
        assertTrue(chatbot.initialize().startsWith("Unable to load tasks:"));
        assertTrue(chatbot.hasResponseError());
        assertEquals("", chatbot.initialize());
        assertFalse(chatbot.hasResponseError());
    }
}
