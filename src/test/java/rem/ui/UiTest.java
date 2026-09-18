package rem.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

import rem.task.Todo;

/**
 * Tests console input/output and message collection in {@link Ui}.
 */
@ResourceLock("SYSTEM_STREAMS")
public class UiTest {
    @Test
    public void messageUi_messagesErrorsAndNumbering_forwardedInOrder() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);

        assertFalse(ui.hasError());
        ui.showMessage("first");
        ui.showMessages("second", "third");
        ui.showMessages();
        ui.showNumberedTasks(List.of(new Todo("read"), new Todo("write")));
        ui.showNumberedTasks(List.of());
        ui.showError("problem");
        ui.close();

        assertTrue(ui.hasError());
        assertEquals(List.of("first", "second", "third", "1.[T][ ] read",
                "2.[T][ ] write", "problem"), output);
    }

    @Test
    public void consoleUi_welcomeReadAndSeparator_printsExpectedConversation() {
        PrintStream originalOutput = System.out;
        java.io.InputStream originalInput = System.in;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream("list\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));

            try (Ui ui = new Ui()) {
                ui.showWelcome(true);
                assertEquals("list", ui.readCommand());
                ui.showMessage("A\nB");
                ui.showSeparator();
                assertNull(ui.readCommand());
            }
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        String output = capturedOutput.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
        assertTrue(output.startsWith("____________________________________________________________\n"
                + " ____                      \n"));
        assertTrue(output.contains("Rem: Hi! I'm Rem.\n"));
        assertTrue(output.contains("Rem: Oh. I couldn't load your saved tasks. I'm showing an empty list.\n"));
        assertTrue(output.contains("Me: ____________________________________________________________\n"
                + "Rem: A\nRem: B\n"));
        assertTrue(output.endsWith("____________________________________________________________\nMe: "));
    }

    @Test
    public void consoleUi_welcomeWithoutLoadError_omitsWarning() {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));

            new Ui(message -> { }).showWelcome(false);
        } finally {
            System.setOut(originalOutput);
        }

        assertFalse(capturedOutput.toString(StandardCharsets.UTF_8).contains("couldn't load"));
    }
}
