package bob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bob.exception.BobException;
import bob.storage.Storage;
import bob.task.Task;
import bob.task.TaskStatus;

/** Tests the chatbot's command-processing loop and persistence integration. */
class ChatBotTest {
    @TempDir
    private Path tempDirectory;

    private final InputStream originalInput = System.in;
    private final PrintStream originalOutput = System.out;

    @AfterEach
    void restoreStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    @Test
    void run_validCommandSequence_executesAndPersistsCommands() throws Exception {
        ByteArrayOutputStream output = configureStreams(
                "todo read book\nmark 1\nlist\nbye\n");
        Path dataFile = tempDirectory.resolve("tasks.txt");
        ChatBot chatBot = new ChatBot(dataFile.toString());

        chatBot.run();

        List<Task> storedTasks = new Storage(dataFile.toString()).load();
        assertEquals(1, storedTasks.size());
        assertEquals("read book", storedTasks.get(0).getDescription());
        assertEquals(TaskStatus.DONE, storedTasks.get(0).getStatus());
        String displayedText = output.toString(StandardCharsets.UTF_8);
        assertTrue(displayedText.contains("added: [T][ ] read book"));
        assertTrue(displayedText.contains("1.[T][X] read book"));
        assertTrue(displayedText.contains("See you next time. Keep reaching for the stars!"));
    }

    @Test
    void run_invalidCommand_displaysErrorAndContinuesUntilBye() throws BobException {
        ByteArrayOutputStream output = configureStreams("unknown command\nbye\n");
        ChatBot chatBot = new ChatBot(tempDirectory.resolve("tasks.txt").toString());

        chatBot.run();

        String displayedText = output.toString(StandardCharsets.UTF_8);
        assertTrue(displayedText.contains("I hit a problem: I don't recognise that command."));
        assertTrue(displayedText.contains("See you next time. Keep reaching for the stars!"));
    }

    @Test
    void processCommand_validTodo_returnsResponseAndPersistsTask() throws Exception {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        ChatBot chatBot = new ChatBot(dataFile.toString());

        ChatResponse response = chatBot.processCommand("todo read book");

        List<Task> storedTasks = new Storage(dataFile.toString()).load();
        assertTrue(response.text().contains("added: [T][ ] read book"));
        assertFalse(response.isExit());
        assertEquals(ResponseType.ADD, response.responseType());
        assertEquals(1, storedTasks.size());
        assertEquals("read book", storedTasks.get(0).getDescription());
    }

    @Test
    void processCommand_invalidCommand_returnsNonExitErrorResponse() throws BobException {
        ChatBot chatBot = new ChatBot(tempDirectory.resolve("tasks.txt").toString());

        ChatResponse response = chatBot.processCommand("unknown command");

        assertTrue(response.text().startsWith(
                "I hit a problem: I don't recognise that command."));
        assertFalse(response.isExit());
        assertEquals(ResponseType.ERROR, response.responseType());
    }

    @Test
    void processCommand_invalidDeadlineDate_returnsSupportedDateFormatError() throws BobException {
        ChatBot chatBot = new ChatBot(tempDirectory.resolve("tasks.txt").toString());

        ChatResponse response = chatBot.processCommand("deadline submit report /by Friday");

        assertEquals(
                "I hit a problem: Use d/M/yyyy or d/M/yyyy HHmm, "
                        + "such as 2/12/2019 or 2/12/2019 1800.",
                response.text());
        assertFalse(response.isExit());
        assertEquals(ResponseType.ERROR, response.responseType());
    }

    @Test
    void processCommand_bye_returnsExitResponse() throws BobException {
        ChatBot chatBot = new ChatBot(tempDirectory.resolve("tasks.txt").toString());

        ChatResponse response = chatBot.processCommand("bye");

        assertEquals("See you next time. Keep reaching for the stars!", response.text());
        assertTrue(response.isExit());
        assertEquals(ResponseType.BYE, response.responseType());
    }

    @Test
    void processCommand_supportedCommands_returnsMatchingResponseTypes() throws BobException {
        ChatBot chatBot = new ChatBot(tempDirectory.resolve("tasks.txt").toString());

        assertEquals(ResponseType.ADD, chatBot.processCommand("todo read book").responseType());
        assertEquals(ResponseType.LIST, chatBot.processCommand("list").responseType());
        assertEquals(ResponseType.FIND, chatBot.processCommand("find book").responseType());
        assertEquals(ResponseType.FIND, chatBot.processCommand("search book").responseType());
        assertEquals(ResponseType.MARK, chatBot.processCommand("mark 1").responseType());
        assertEquals(ResponseType.UNMARK, chatBot.processCommand("unmark 1").responseType());
        assertEquals(ResponseType.DELETE, chatBot.processCommand("delete 1").responseType());
        assertEquals(ResponseType.BYE, chatBot.processCommand("bye").responseType());
    }

    @Test
    void constructor_directoryAsDataPath_bobExceptionThrown() {
        BobException exception = assertThrows(BobException.class,
                () -> new ChatBot(tempDirectory.toString()));

        assertEquals("I could not load your saved tasks", exception.getMessage());
    }

    private ByteArrayOutputStream configureStreams(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        return output;
    }
}
