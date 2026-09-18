package friday;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import friday.storage.Storage;

/**
 * Tests the user-interface-independent chatbot adapter.
 */
class ChatbotTest {
    @TempDir
    Path temporaryDirectory;

    /**
     * Verifies that chatbot commands use the existing task command behavior.
     */
    @Test
    void getResponse_addTask_returnsTaskConfirmation() {
        Chatbot chatbot = createChatbot();

        Chatbot.Response response = chatbot.getResponseResult("todo read book");

        assertTrue(response.text().contains("I've added this task"));
        assertEquals(1, response.taskCards().size());
        assertEquals("To-do", response.taskCards().get(0).category().getLabel());
        assertEquals("read book", response.taskCards().get(0).title());
    }

    /**
     * Verifies that GUI archive commands move tasks and persist both task files.
     */
    @Test
    void getResponse_archiveCommand_persistsArchivedTask() throws Exception {
        Chatbot chatbot = createChatbot();

        chatbot.getResponse("todo read book");
        String response = chatbot.getResponse("archive 1");

        assertTrue(response.contains("archived this task"));
        assertFalse(chatbot.getResponse("list").contains("read book"));
        assertEquals("read book", chatbot.getResponseResult("list-archive").taskCards().get(0).title());
        assertEquals(1, new Storage(temporaryDirectory.resolve("archive.txt").toString()).load()
                .getTasks().size());
    }

    /**
     * Verifies that a new chatbot loads active and archived tasks from disk.
     */
    @Test
    void newChatbot_loadsPersistedTaskState() {
        Chatbot firstChatbot = createChatbot();
        firstChatbot.getResponse("todo read book");
        firstChatbot.getResponse("archive 1");

        Chatbot secondChatbot = createChatbot();

        assertEquals("read book", secondChatbot.getResponseResult("list-archive").taskCards().get(0).title());
    }

    /**
     * Verifies that invalid GUI commands provide a response that can receive error styling.
     */
    @Test
    void getResponseResult_invalidCommand_marksResponseAsError() {
        Chatbot chatbot = createChatbot();

        Chatbot.Response response = chatbot.getResponseResult("not-a-command");

        assertTrue(response.isError());
        assertTrue(response.text().contains("don't know what that means"));
    }

    /**
     * Verifies that adding duplicate task details is rejected without adding a second task.
     */
    @Test
    void getResponse_duplicateTask_reportsFriendlyError() {
        Chatbot chatbot = createChatbot();

        chatbot.getResponse("todo read book");
        String response = chatbot.getResponse("todo read book");

        assertTrue(response.contains("A task with those details already exists."));
    }

    /**
     * Verifies that the GUI response identifies the exit command.
     */
    @Test
    void getResponseResult_bye_marksResponseAsExit() {
        Chatbot.Response response = createChatbot().getResponseResult("bye");

        assertTrue(response.isExit());
        assertFalse(response.isError());
        assertTrue(response.text().contains("Standing by."));
    }

    /**
     * Verifies that GUI responses do not contain console-only dividers.
     */
    @Test
    void getResponse_guiResponse_omitsConsoleDividers() {
        String response = createChatbot().getResponse("todo read book");

        assertFalse(response.contains("____________________________________________________________"));
    }

    private Chatbot createChatbot() {
        return new Chatbot(
                new Storage(temporaryDirectory.resolve("friday.txt").toString()),
                new Storage(temporaryDirectory.resolve("archive.txt").toString()));
    }
}
