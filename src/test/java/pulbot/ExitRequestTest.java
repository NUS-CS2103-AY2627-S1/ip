package pulbot;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import pulbot.storage.Storage;

/** Tests the exit signal consumed by the GUI after parsing a chat command. */
public class ExitRequestTest {
    @TempDir
    private Path tempDir;

    @ParameterizedTest
    @ValueSource(strings = {"bye", "  bye  ", "\tbye\t"})
    public void getResponse_exitCommand_requestsExit(String input) {
        Pulbot pulbot = new Pulbot(new Storage(tempDir.resolve("tasks.txt").toString()));

        assertTrue(pulbot.getResponse(input).contains("Bye."));
        assertTrue(pulbot.isExitRequested());
        assertTrue(pulbot.getResponse("bye now").contains("ERROR"));
        assertFalse(pulbot.isExitRequested());
    }
}
