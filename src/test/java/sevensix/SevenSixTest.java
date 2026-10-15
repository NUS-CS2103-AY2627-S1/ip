package sevensix;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Verifies command processing shared by the console and JavaFX interfaces.
 */
class SevenSixTest {
    /** The temporary folder used to isolate task-storage tests. */
    @TempDir
    private Path temporaryDirectory;

    /**
     * Adding and listing a task should return the same responses used by the console interface.
     */
    @Test
    void getResponseAddAndListTaskReturnsExpectedResponses() {
        SevenSix chatbot = createChatbot();

        assertEquals(String.join(System.lineSeparator(),
                "Circling back on your ask. I've actioned this deliverable:",
                "  [T][ ] read book",
                "Your pipeline now holds 1 deliverable."), chatbot.getResponse("todo read book"));
        assertEquals("Here is your pipeline:" + System.lineSeparator() + "1.[T][ ] read book",
                chatbot.getResponse("list"));
    }

    /**
     * Listing an empty task list should provide visible feedback.
     */
    @Test
    void getResponseListWithNoTasksReturnsHelpfulMessage() {
        SevenSix chatbot = createChatbot();

        assertEquals("Your pipeline is empty. Nothing to action right now.",
                chatbot.getResponse("list"));
    }

    /**
     * A new chatbot should load tasks saved by an earlier chatbot instance.
     */
    @Test
    void getResponseSavedTaskIsReloadedReturnsPersistedTask() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        SevenSix firstChatbot = new SevenSix(dataFile);
        firstChatbot.getResponse("todo saved task");

        SevenSix secondChatbot = new SevenSix(dataFile);

        assertEquals("Here is your pipeline:" + System.lineSeparator() + "1.[T][ ] saved task",
                secondChatbot.getResponse("list"));
    }

    /**
     * Invalid input should use a clearly labeled error response.
     *
     * @param command the rejected command.
     * @param message the existing user-visible explanation.
     */
    @ParameterizedTest
    @CsvSource(value = {
        "todo|a todo needs a description. Let us put some substance behind it.",
        "deadline /by 2024-01-01|a deadline needs both a description and a due time to be actionable.",
        "event /from 2024-01-01 /to 2024-01-02|"
                + "an event needs a description, a start, and an end before I can calendar it.",
        "find|a find needs a keyword before I can surface anything.",
        "mark two|please reference a valid deliverable number.",
        "mark 1|that deliverable number is not in your pipeline.",
        "undo|there is nothing in the rollback history yet."
    }, delimiter = '|')
    void getResponseInvalidCommandReturnsHelpfulError(String command, String message) {
        SevenSix chatbot = createChatbot();

        assertEquals("Flagging a blocker: " + message, chatbot.getResponse(command));
    }

    /**
     * Unknown commands should use the same error prefix as invalid task commands.
     */
    @Test
    void getResponseUnknownCommandReturnsLabeledError() {
        SevenSix chatbot = createChatbot();

        assertEquals("Flagging a blocker: that one is outside my wheelhouse. Type help for available commands.",
                chatbot.getResponse("blah"));
    }

    /**
     * Undoing an added task should restore the previous empty task list.
     */
    @Test
    void getResponseUndoAddTaskRestoresPreviousTaskList() {
        SevenSix chatbot = createChatbot();

        chatbot.getResponse("todo read book");

        assertEquals("Undid addition: [T][ ] read book",
                chatbot.getResponse("undo"));
        assertEquals("Your pipeline is empty. Nothing to action right now.",
                chatbot.getResponse("list"));
        assertEquals("Flagging a blocker: there is nothing in the rollback history yet.",
                chatbot.getResponse("undo"));
    }

    /**
     * Undoing a status change should restore the task and persist its previous state.
     */
    @Test
    void getResponseUndoMarkTaskRestoresAndPersistsPreviousState() {
        Path dataFile = temporaryDirectory.resolve("undo-tasks.txt");
        SevenSix chatbot = new SevenSix(dataFile);

        chatbot.getResponse("deadline submit report /by 2019-06-06");
        chatbot.getResponse("mark 1");

        assertEquals("Undid mark as done: [D][ ] submit report (by: Jun 06 2019)",
                chatbot.getResponse("undo"));
        assertEquals("Here is your pipeline:" + System.lineSeparator() + "1.[D][ ] submit report (by: Jun 06 2019)",
                chatbot.getResponse("list"));

        SevenSix reloadedChatbot = new SevenSix(dataFile);
        assertEquals("Here is your pipeline:" + System.lineSeparator() + "1.[D][ ] submit report (by: Jun 06 2019)",
                reloadedChatbot.getResponse("list"));
    }

    /**
     * Creates a chatbot with an isolated data file.
     *
     * @return a chatbot backed by the test directory.
     */
    private SevenSix createChatbot() {
        return new SevenSix(temporaryDirectory.resolve("tasks.txt"));
    }
}
