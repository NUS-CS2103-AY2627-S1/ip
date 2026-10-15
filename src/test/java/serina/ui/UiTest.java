package serina.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/** Tests console input trimming, end-of-input behavior, and response framing. */
public class UiTest {
    @Test
    public void input_multipleLinesAndEndOfFile_readsTrimmedCommands() {
        InputStream originalInput = System.in;
        try {
            System.setIn(new ByteArrayInputStream("  todo task  \n\n".getBytes(StandardCharsets.UTF_8)));
            try (Ui ui = new Ui()) {
                assertTrue(ui.hasNextCommand());
                assertEquals("todo task", ui.readCommand());
                assertTrue(ui.hasNextCommand());
                assertEquals("", ui.readCommand());
                assertFalse(ui.hasNextCommand());
            }
        } finally {
            System.setIn(originalInput);
        }
    }

    @Test
    public void showMessage_multilineText_prefixesEveryLineAndAddsBoundaries() {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            try (Ui ui = new Ui()) {
                ui.showMessage("first\nsecond\n");
            }
        } finally {
            System.setOut(originalOutput);
        }

        String rendered = output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
        assertTrue(rendered.startsWith("    ____________________________________________________________\n"));
        assertTrue(rendered.contains("     first\n     second\n     \n"));
        assertTrue(rendered.endsWith("    ____________________________________________________________\n"));
    }
}
