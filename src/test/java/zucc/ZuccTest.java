package zucc;

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
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;

import zucc.ui.TextUi;
import zucc.ui.Ui;

/**
 * Tests the application boundary shared by terminal and JavaFX interfaces.
 */
public class ZuccTest {
    @TempDir
    private Path temporaryDirectory;

    /**
     * Verifies that a regular command sends its response through the supplied UI.
     *
     * @throws ZuccException if the isolated task store cannot be initialized.
     */
    @Test
    public void executeCommand_regularCommand_responseShownAndSessionContinues()
            throws ZuccException {
        RecordingUi ui = new RecordingUi();
        Zucc zucc = new Zucc(temporaryDirectory.resolve("tasks.txt"));

        boolean isExit = zucc.executeCommand("todo read the JavaFX guide", ui);

        assertFalse(isExit);
        assertTrue(ui.getLastMessage().contains("[T][ ] read the JavaFX guide"));
    }

    /**
     * Verifies that an invalid command is shown as feedback without ending the session.
     *
     * @throws ZuccException if the isolated task store cannot be initialized.
     */
    @Test
    public void executeCommand_invalidCommand_errorShownAndSessionContinues()
            throws ZuccException {
        RecordingUi ui = new RecordingUi();
        Zucc zucc = new Zucc(temporaryDirectory.resolve("tasks.txt"));

        boolean isExit = zucc.executeCommand("dance", ui);

        assertFalse(isExit);
        assertTrue(ui.getLastMessage().contains("doesn't recognize that command"));
    }

    /**
     * Verifies that the exit command shows its farewell and reports the ended session.
     *
     * @throws ZuccException if the isolated task store cannot be initialized.
     */
    @Test
    public void executeCommand_exitCommand_farewellShownAndSessionEnds()
            throws ZuccException {
        RecordingUi ui = new RecordingUi();
        Zucc zucc = new Zucc(temporaryDirectory.resolve("tasks.txt"));

        boolean isExit = zucc.executeCommand("bye", ui);

        assertTrue(isExit);
        assertEquals("Bye. Hope to see you again soon!", ui.getLastMessage());
    }

    /**
     * Verifies that a terminal session greets once, executes commands, and stops after bye.
     *
     * @throws IOException if the isolated task file cannot be inspected.
     * @throws ZuccException if the isolated task store cannot be initialized.
     */
    @Test
    @ResourceLock("SYSTEM_IN")
    @ResourceLock("SYSTEM_OUT")
    public void run_commandsBeforeAndAfterBye_onlyCommandsBeforeByeExecuted()
            throws IOException, ZuccException {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(
                "todo first task\nbye\ntodo ignored task\n".getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

        try (TextUi ui = new TextUi()) {
            new Zucc(taskFile).run(ui);
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        assertEquals(
                List.of("T | 0 | 0 | first task"),
                Files.readAllLines(taskFile, StandardCharsets.UTF_8));
        String displayedOutput = output.toString(StandardCharsets.UTF_8);
        assertTrue(displayedOutput.contains("Hello! I'm Zucc."));
        assertTrue(displayedOutput.contains("Bye. Hope to see you again soon!"));
        assertFalse(displayedOutput.contains("ignored task"));
    }

    /** Records messages without depending on a concrete frontend toolkit. */
    private static final class RecordingUi extends Ui {
        /** Messages shown during the test. */
        private final List<String> messages = new ArrayList<>();

        /** Records one response for later assertion. */
        @Override
        public void showMessage(String message) {
            messages.add(message);
        }

        /** Returns the most recently recorded response. */
        private String getLastMessage() {
            return messages.getLast();
        }
    }
}
