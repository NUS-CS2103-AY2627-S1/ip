package catgpt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests CatGPT's response API used by the graphical interface.
 */
public class CatGPTTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponseValidCommandsReturnsUpdatedTaskList() {
        CatGPT chatbot = new CatGPT(temporaryDirectory.resolve("tasks.txt").toString());

        String addResponse = chatbot.getResponse("todo read book");
        String listResponse = chatbot.getResponse("list");

        assertTrue(addResponse.contains("[T][ ] read book"));
        assertTrue(listResponse.contains("1.[T][ ] read book"));
    }

    @Test
    public void getResponseInvalidCommandReturnsUserFriendlyError() {
        CatGPT chatbot = new CatGPT(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals(
                "My whiskers got crossed. I don't know what that means.",
                chatbot.getResponse("meow"));
    }

    @Test
    public void getCommandResultByeReturnsFarewellAndExitSignal() {
        CatGPT chatbot = new CatGPT(temporaryDirectory.resolve("tasks.txt").toString());

        CommandResult result = chatbot.getCommandResult("bye");

        assertEquals("Time for a catnap. Your tasks are safe. See you again soon!", result.response());
        assertTrue(result.shouldExit());
    }

    @Test
    public void getCommandResultInvalidCommandDoesNotExit() {
        CatGPT chatbot = new CatGPT(temporaryDirectory.resolve("tasks.txt").toString());

        CommandResult result = chatbot.getCommandResult("bye now");

        assertEquals("My whiskers got crossed. I don't know what that means.", result.response());
        assertFalse(result.shouldExit());
    }

    @Test
    public void getResponseSortCommandSortsAndPersistsTasks() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        CatGPT chatbot = new CatGPT(dataFile.toString());
        chatbot.getResponse("todo write report");
        chatbot.getResponse("todo attend meeting");

        String sortResponse = chatbot.getResponse("sort");
        CatGPT reloadedChatbot = new CatGPT(dataFile.toString());

        assertTrue(sortResponse.contains("1.[T][ ] attend meeting"));
        assertTrue(sortResponse.contains("2.[T][ ] write report"));
        assertTrue(reloadedChatbot.getResponse("list").contains(
                "1.[T][ ] attend meeting" + System.lineSeparator()
                        + "2.[T][ ] write report"));
    }

    @Test
    public void getResponseAddWhenSaveFailsDoesNotRetainTask() {
        CatGPT chatbot = new CatGPT(new FailingStorage(List.of()));

        String response = chatbot.getResponse("todo read book");

        assertEquals("My whiskers got crossed. Simulated save failure.", response);
        assertEquals("Here are the tasks in your list:", chatbot.getResponse("list"));
    }

    @Test
    public void getResponseMarkWhenSaveFailsRestoresTaskStatus() {
        CatGPT chatbot = new CatGPT(new FailingStorage(List.of(new Todo("read book"))));

        String response = chatbot.getResponse("mark 1");

        assertEquals("My whiskers got crossed. Simulated save failure.", response);
        assertTrue(chatbot.getResponse("list").contains("1.[T][ ] read book"));
    }

    @Test
    public void getResponseDeleteWhenSaveFailsRestoresTask() {
        CatGPT chatbot = new CatGPT(new FailingStorage(List.of(new Todo("read book"))));

        String response = chatbot.getResponse("delete 1");

        assertEquals("My whiskers got crossed. Simulated save failure.", response);
        assertTrue(chatbot.getResponse("list").contains("1.[T][ ] read book"));
    }

    @Test
    public void getResponseSortWhenSaveFailsRestoresTaskOrder() {
        CatGPT chatbot = new CatGPT(new FailingStorage(List.of(
                new Todo("write report"), new Todo("attend meeting"))));

        String response = chatbot.getResponse("sort");

        assertEquals("My whiskers got crossed. Simulated save failure.", response);
        assertTrue(chatbot.getResponse("list").contains(
                "1.[T][ ] write report" + System.lineSeparator()
                        + "2.[T][ ] attend meeting"));
    }

    @Test
    public void getStartupErrorMessageCorruptedDataReportsProblemAndUsesEmptyList() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "corrupted task data");

        CatGPT chatbot = new CatGPT(dataFile.toString());

        assertEquals(
                "My whiskers got crossed. The saved task data is corrupted at line 1.",
                chatbot.getStartupErrorMessage());
        assertEquals("Here are the tasks in your list:", chatbot.getResponse("list"));
    }

    /**
     * Simulates storage that loads normally but rejects every save attempt.
     */
    private static class FailingStorage extends Storage {
        private final List<Task> initialTasks;

        FailingStorage(List<Task> initialTasks) {
            super("unused.txt");
            this.initialTasks = initialTasks;
        }

        @Override
        public List<Task> load() {
            return initialTasks;
        }

        @Override
        public void save(TaskList tasks) throws CatGPTException {
            throw new CatGPTException("Simulated save failure.");
        }
    }
}
