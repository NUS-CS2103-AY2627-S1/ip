package zhangwei;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ZhangWeiTest {

    @TempDir
    Path tempDir;

    /** Returns a chatbot saving into the temporary folder, not the real data folder. */
    private ZhangWei newChatbot() {
        return new ZhangWei(tempDir.resolve("tasks.txt").toString());
    }

    @Test
    void getResponse_unknownCommand_errorFlagSet() {
        ZhangWei chatbot = newChatbot();
        chatbot.getResponse("blah");
        assertTrue(chatbot.isErrorResponse());
    }

    @Test
    void getResponse_todoWithoutDescription_errorFlagSet() {
        ZhangWei chatbot = newChatbot();
        chatbot.getResponse("todo");
        assertTrue(chatbot.isErrorResponse());
    }

    @Test
    void getResponse_validCommandAfterError_errorFlagCleared() {
        ZhangWei chatbot = newChatbot();
        chatbot.getResponse("blah");
        chatbot.getResponse("todo read book");
        assertFalse(chatbot.isErrorResponse());
    }
}
