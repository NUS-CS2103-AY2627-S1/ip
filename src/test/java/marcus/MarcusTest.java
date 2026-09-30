package marcus;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command dispatch and user-facing response routing. */
class MarcusTest {
    @TempDir
    private Path temporaryDirectory;

    /** Verifies that a valid command creates a task and reports success. */
    @Test
    void processCommand_validTodo_reportsSuccess() {
        Marcus marcus = createMarcus();
        RecordingResponseHandler responseHandler = new RecordingResponseHandler();

        assertTrue(marcus.processCommand("  todo   read book  ", responseHandler));
        assertTrue(responseHandler.message.contains("I've added this task"));
    }

    /** Verifies that duplicate task creation reports an error and does not continue silently. */
    @Test
    void processCommand_duplicateTodo_reportsError() {
        Marcus marcus = createMarcus();
        RecordingResponseHandler responseHandler = new RecordingResponseHandler();
        marcus.processCommand("todo read book", responseHandler);

        assertTrue(marcus.processCommand("todo read book", responseHandler));
        assertTrue(responseHandler.error.contains("identical task"));
    }

    /** Verifies that a blank command reports an error while keeping the session active. */
    @Test
    void processCommand_blankCommand_reportsErrorAndContinues() {
        Marcus marcus = createMarcus();
        RecordingResponseHandler responseHandler = new RecordingResponseHandler();

        assertTrue(marcus.processCommand("   ", responseHandler));
        assertTrue(responseHandler.error.contains("Please enter a command"));
    }

    /** Verifies that the bye command ends the session and sends a goodbye response. */
    @Test
    void processCommand_bye_returnsFalseAndSaysGoodbye() {
        Marcus marcus = createMarcus();
        RecordingResponseHandler responseHandler = new RecordingResponseHandler();

        assertFalse(marcus.processCommand("bye", responseHandler));
        assertTrue(responseHandler.isGoodbyeShown);
    }

    private Marcus createMarcus() {
        Marcus marcus = new Marcus(temporaryDirectory.resolve("results.txt").toString());
        marcus.startSession();
        return marcus;
    }

    private static class RecordingResponseHandler implements ResponseHandler {
        private String message = "";
        private String error = "";
        private boolean isGoodbyeShown;

        @Override
        public void showMessage(String message) {
            this.message = message;
        }

        @Override
        public void showError(String message) {
            this.error = message;
        }

        @Override
        public void showTaskList(String taskList) {
            message = taskList;
        }

        @Override
        public void showGoodbye() {
            isGoodbyeShown = true;
        }
    }
}
