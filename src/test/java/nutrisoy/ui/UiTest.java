package nutrisoy.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/** Verifies output capture and response metadata without starting JavaFX. */
public class UiTest {
    @Test
    public void capture_multipleMessages_preservesOrderAndResets() {
        Ui ui = new Ui();
        ui.startCapturingOutput();
        ui.showError("First");
        ui.showError("Second");
        assertEquals(" Nice try. First" + System.lineSeparator() + " Nice try. Second",
                ui.stopCapturingOutput());
        ui.startCapturingOutput();
        assertEquals("", ui.stopCapturingOutput());
    }

    @Test
    public void capture_stop_restoresConsoleOutput() {
        PrintStream original = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capture = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            Ui ui = new Ui();
            ui.startCapturingOutput();
            ui.showError("captured");
            assertEquals("", output.toString(StandardCharsets.UTF_8));
            assertEquals(" Nice try. captured", ui.stopCapturingOutput());
            ui.showError("console");
            assertEquals(" Nice try. console" + System.lineSeparator(), output.toString(StandardCharsets.UTF_8));
        } finally {
            System.setOut(original);
        }
    }

    @Test
    public void loadingError_defaultAndDetailed_includesRecoveryInformation() {
        Ui ui = new Ui();
        ui.startCapturingOutput();
        ui.showLoadingError();
        assertTrue(ui.stopCapturingOutput().contains("readable"));
        ui.startCapturingOutput();
        ui.showLoadingError("Permission denied");
        assertTrue(ui.stopCapturingOutput().contains("Permission denied"));
    }

    @Test
    public void response_errorFlag_isIndependentOfMessageText() {
        GuiResponse ordinary = new GuiResponse("Nice try. This is task text", false);
        GuiResponse error = new GuiResponse("Unable to save", true);
        assertFalse(ordinary.isError());
        assertEquals("Nice try. This is task text", ordinary.getMessage());
        assertTrue(error.isError());
        assertEquals("Unable to save", error.getMessage());
    }
}
