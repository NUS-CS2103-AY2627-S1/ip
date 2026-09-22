package friday.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/** Tests console output produced by the user interface. */
class UiTest {

    @Test
    void showLine_message_printsMessageAndLineSeparator() {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
            Ui ui = new Ui();

            ui.showLine("hello");

            assertEquals("hello" + System.lineSeparator(),
                    capturedOutput.toString(StandardCharsets.UTF_8));
        } finally {
            System.setOut(originalOutput);
        }
    }

    @Test
    void showWelcome_outputOnlyUi_printsBannerAndGreetingToConfiguredOutput() {
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(capturedOutput, true, StandardCharsets.UTF_8);
        Ui ui = new Ui(output);

        ui.showWelcome();

        String welcomeMessage = capturedOutput.toString(StandardCharsets.UTF_8);
        assertTrue(welcomeMessage.contains(" ______    _     _"));
        assertTrue(welcomeMessage.contains("Hello! I'm Friday."));
        assertTrue(welcomeMessage.contains("What you want ah?"));
    }
}
