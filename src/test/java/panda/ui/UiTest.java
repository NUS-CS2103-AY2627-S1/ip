package panda.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class UiTest {
    private static final String DIVIDER = "____________________________________________________________";

    @Test
    void showWelcome_printsGreetingAndDividers() {
        String output = captureOutput(() -> new Ui().showWelcome());

        assertEquals(DIVIDER + "\nPANDA\nHello! I'm Panda.\nWhat can I do for you?\n" + DIVIDER + "\n", output);
    }

    @Test
    void showBye_printsPandaArtAndFarewell() {
        String output = captureOutput(() -> new Ui().showBye());

        assertEquals("    ( ) ( ) ( )\n      \\ | /\n       \\|/\n     .-----.\n    /       \\\n"
                + "   |   o o   |\n    \\_______/\nBye. Hope to see you again soon!\n" + DIVIDER + "\n", output);
    }

    private String captureOutput(Runnable action) {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream outputStream = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(outputStream);
            action.run();
        } finally {
            System.setOut(originalOutput);
        }
        return output.toString(StandardCharsets.UTF_8);
    }
}
