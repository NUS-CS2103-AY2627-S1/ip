package bot67.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

class UiTest {
    @Test
    void uiMessages_includeExpectedUserFacingContent() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try {
            System.setOut(new PrintStream(output));
            Ui ui = new Ui();
            ui.showWelcome("banner");
            ui.showError("problem");
            ui.showLine("first line", "second line");
            ui.showGoodbye("art");
            String text = output.toString();
            assertTrue(text.contains("banner"));
            assertTrue(text.contains("Hello! I'm Bot67."));
            assertTrue(text.contains("SIX SEVEN! problem"));
            assertTrue(text.contains("first line" + System.lineSeparator() + "second line"));
            assertTrue(text.contains("art"));
        } finally {
            System.setOut(original);
        }
    }
}
