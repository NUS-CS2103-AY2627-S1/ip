package monkey.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UiTest {
    @Test
    void consumerUi_showWelcomeAndMessage_sendsExpectedOutput() {
        StringBuilder output = new StringBuilder();
        Ui ui = new Ui(message -> output.append(message).append('\n'));

        ui.showWelcome();
        ui.showMessage("ready");

        assertTrue(output.toString().startsWith("____________________________________________________________\n"));
        assertTrue(output.toString().contains("Hello! I'm Monkey, your cheeky little assistant.\n"));
        assertTrue(output.toString().endsWith("ready\n"));
        assertNull(ui.readCommand());
    }

    @Test
    void consoleUi_readCommandAndShowSeparator_usesSystemStreams() {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream("list\n".getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            Ui ui = new Ui();

            assertEquals("list", ui.readCommand());
            assertNull(ui.readCommand());
            ui.showSeparator();

            assertEquals("____________________________________________________________\n",
                    output.toString(StandardCharsets.UTF_8));
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
    }
}
