package glennon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

/**
 * Tests complete console sessions, including input exhaustion and startup failures.
 * Restores standard streams after every run and keeps saved data in temporary folders.
 */
@ResourceLock(Resources.SYSTEM_OUT)
@ResourceLock("java.lang.System.in")
class GlennonConsoleTest {
    private static final String DIVIDER = "_".repeat(60) + "\n";
    private static final String WELCOME = DIVIDER + """
            +==========================================================+
            |                                                          |
            |             ________                                     |
            |            / ____/ /__  ____  ____  ____  ____           |
            |           / / __/ / _ \\/ __ \\/ __ \\/ __ \\/ __ \\          |
            |          / /_/ / /  __/ / / / / / / /_/ / / / /          |
            |          \\____/_/\\___/_/ /_/_/ /_/\\____/_/ /_/           |
            |                                                          |
            +==========================================================+
            Hey there! Glennon online.
            What's the mission?
            """ + DIVIDER;

    @TempDir
    private Path directory;

    @Test
    void run_emptyInput_printsWelcomeAndClosesInputWithoutCreatingData() {
        assertEquals(WELCOME, runConsole(""));
        assertFalse(Files.exists(directory.resolve("missions.txt")));
    }

    @Test
    void run_inputEndsWithoutNewline_executesFinalCommandAndPersistsIt() {
        assertEquals(WELCOME + DIVIDER
                        + "Mission added: [T][ ] finish at EOF\nMission log now has 1 mission.\n" + DIVIDER,
                runConsole("todo finish at EOF"));
        assertEquals(WELCOME + DIVIDER + "Mission log:\n1. [T][ ] finish at EOF\n" + DIVIDER,
                runConsole("list\n"));
    }

    @Test
    void run_exitWithQueuedCommands_stopsBeforeLaterMutations() {
        assertEquals(WELCOME + DIVIDER + "Signing off. Catch you on the next mission!\n" + DIVIDER,
                runConsole("bye\ntodo must not run\n"));
        assertFalse(Files.exists(directory.resolve("missions.txt")));
    }

    @Test
    void run_rejectedCommandThenRecovery_keepsProcessingInput() {
        assertEquals(WELCOME
                        + DIVIDER + "Mission control alert!\nPlease enter a valid mission number.\n" + DIVIDER
                        + DIVIDER + "Mission added: [T][ ] recovered\nMission log now has 1 mission.\n" + DIVIDER
                        + DIVIDER + "Mission log:\n1. [T][ ] recovered\n" + DIVIDER
                        + DIVIDER + "Signing off. Catch you on the next mission!\n" + DIVIDER,
                runConsole("delete 1\ntodo recovered\nlist\nbye\n"));
    }

    @Test
    void run_corruptStorage_reportsFailureAndDoesNotExecuteInput() throws IOException {
        Path data = directory.resolve("missions.txt");
        Files.writeString(data, "corrupted\n");

        assertEquals(WELCOME + "Mission control alert!\nMission data is corrupted at line 1.\n",
                runConsole("todo cannot replace data\nbye\n"));
        assertEquals("corrupted\n", Files.readString(data));
    }

    @Test
    void run_storageIsDirectory_reportsLoadFailureAndClosesInput() throws IOException {
        Path data = Files.createDirectory(directory.resolve("missions.txt"));

        assertEquals(WELCOME + "Mission control alert!\nGlennon could not load the mission data.\n",
                runConsole("todo cannot replace directory\n"));
        assertTrue(Files.isDirectory(data));
    }

    /**
     * Captures a complete console run and verifies input is released on every exit path.
     */
    private String runConsole(String commands) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ClosingInputStream input = new ClosingInputStream(commands);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(input);
            System.setOut(capturedOutput);
            new Glennon(directory.resolve("missions.txt").toString()).run();
            assertTrue(input.isClosed, "A console session must release its input stream");
            return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n").replace('\r', '\n');
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
    }

    /**
     * Records closure without depending on ByteArrayInputStream's no-op close behavior.
     */
    private static final class ClosingInputStream extends ByteArrayInputStream {
        private boolean isClosed;

        private ClosingInputStream(String input) {
            super(input.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public void close() throws IOException {
            isClosed = true;
            super.close();
        }
    }
}
