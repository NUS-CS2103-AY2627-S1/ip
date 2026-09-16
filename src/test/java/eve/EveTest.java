package eve;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests {@link Eve}: its constructor's loading behavior, {@link Eve#getResponse}
 * (the entry point the GUI drives), and {@link Eve#run} (the CLI loop), each
 * with an isolated {@code @TempDir} save file so tests don't share state.
 */
public class EveTest {
    @TempDir
    private Path tempDir;

    private String storagePath() {
        return tempDir.resolve("eve.txt").toString();
    }

    @Test
    public void constructor_noSavedFile_startsWithEmptyList() {
        Eve eve = new Eve(storagePath());

        assertTrue(eve.getResponse("list").contains("Here's everything on your list:"));
    }

    @Test
    public void constructor_savedFileExists_loadsItsTasks() throws IOException {
        Files.writeString(tempDir.resolve("eve.txt"), "T | 0 | read book\n", StandardCharsets.UTF_8);

        Eve eve = new Eve(storagePath());

        assertTrue(eve.getResponse("list").contains("[T][ ] read book"));
    }

    @Test
    public void constructor_saveFileIsADirectory_startsEmptyInsteadOfCrashing() throws IOException {
        Path directoryAsFile = tempDir.resolve("eve.txt");
        Files.createDirectory(directoryAsFile);

        Eve eve = new Eve(directoryAsFile.toString());

        assertTrue(eve.getResponse("list").contains("Here's everything on your list:"));
    }

    @Test
    public void getResponse_validCommand_returnsResponseWithoutDividerLines() {
        Eve eve = new Eve(storagePath());

        String response = eve.getResponse("todo read book");

        assertFalse(response.contains("____"));
        assertTrue(response.contains("read book"));
    }

    @Test
    public void getResponse_validCommand_isNotFlaggedAsError() {
        Eve eve = new Eve(storagePath());

        eve.getResponse("todo read book");

        assertFalse(eve.isLastResponseError());
    }

    @Test
    public void getResponse_invalidCommand_isFlaggedAsError() {
        Eve eve = new Eve(storagePath());

        String response = eve.getResponse("blah");

        assertTrue(eve.isLastResponseError());
        assertTrue(response.contains("don't recognize"));
    }

    @Test
    public void getResponse_errorThenValidCommand_errorFlagClears() {
        Eve eve = new Eve(storagePath());

        eve.getResponse("blah");
        eve.getResponse("list");

        assertFalse(eve.isLastResponseError());
    }

    @Test
    public void getResponse_bye_setsIsExit() {
        Eve eve = new Eve(storagePath());

        eve.getResponse("bye");

        assertTrue(eve.isExit());
    }

    @Test
    public void getResponse_notBye_isExitStaysFalse() {
        Eve eve = new Eve(storagePath());

        eve.getResponse("list");

        assertFalse(eve.isExit());
    }

    @Test
    public void getWelcomeMessage_delegatesToUi() {
        Eve eve = new Eve(storagePath());

        assertEquals(new Ui().getWelcomeMessage(), eve.getWelcomeMessage());
    }

    @Test
    public void run_scriptedInputEndingInBye_printsGreetingConfirmationAndGoodbyeThenReturns() throws Exception {
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream("todo read book\nbye\n".getBytes(StandardCharsets.UTF_8)));
        Eve eve;
        try {
            // Ui's Scanner captures System.in at construction time, so it must already be
            // redirected before Eve (which constructs the Ui) is created.
            eve = new Eve(storagePath());
        } finally {
            System.setIn(originalIn);
        }

        String output = TestUtil.captureStdOut(eve::run);

        assertTrue(output.contains("Eve"));
        assertTrue(output.contains("read book"));
        assertTrue(output.contains("Bye"));
    }
}
