package odysseus;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

/**
 * Tests reply metadata exposed to the conversation presentation adapter.
 */
class ConversationResponseTest {
    @Test
    void getConversationResponse_invalidCommand_marksResponseAsError() throws Exception {
        Path taskPath = Files.createTempDirectory("odysseus-response-test").resolve("tasks.txt");
        Odysseus odysseus = new Odysseus(taskPath);

        ConversationResponse response = odysseus.getConversationResponse("mark no");

        assertTrue(response.isCommandError());
        assertTrue(response.message().contains("Use a task number after mark"));
    }

    @Test
    void getConversationResponse_validCommand_marksResponseAsStandard() throws Exception {
        Path taskPath = Files.createTempDirectory("odysseus-response-test").resolve("tasks.txt");
        Odysseus odysseus = new Odysseus(taskPath);

        ConversationResponse response = odysseus.getConversationResponse("todo mend sail");

        assertFalse(response.isCommandError());
        assertTrue(response.message().contains("Well charted"));
    }
}
