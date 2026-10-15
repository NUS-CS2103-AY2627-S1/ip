package sevensix;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Verifies how the user interface recognizes error responses.
 */
class UiTest {
    /** The temporary folder that keeps the chatbot's data file out of the project. */
    @TempDir
    private Path temporaryDirectory;

    /**
     * A message built by formatError should be recognized as an error.
     */
    @Test
    void isErrorResponseFormattedErrorReturnsTrue() {
        assertTrue(Ui.isErrorResponse(
                Ui.formatError("that deliverable number is not in your pipeline.")));
    }

    /**
     * An ordinary reply should not be highlighted as an error.
     */
    @Test
    void isErrorResponseNormalReplyReturnsFalse() {
        assertFalse(Ui.isErrorResponse(Ui.getEmptyListMessage()));
    }

    /**
     * Error replies from real commands should be recognized, including unknown commands.
     */
    @Test
    void isErrorResponseChatbotErrorsReturnTrue() {
        SevenSix chatbot = new SevenSix(temporaryDirectory.resolve("tasks.txt"));

        assertTrue(Ui.isErrorResponse(chatbot.getResponse("blah")));
        assertTrue(Ui.isErrorResponse(chatbot.getResponse("mark 99")));
    }
}
