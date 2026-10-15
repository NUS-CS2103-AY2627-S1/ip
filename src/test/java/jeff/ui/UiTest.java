package jeff.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class UiTest {

    @Test
    public void capture_multipleMessages_returnsMessagesOnSeparateLines() {
        Ui ui = new Ui();

        ui.startCapturing();
        ui.showMessage("first", "second");
        String output = ui.stopCapturing();

        assertEquals(
                "first" + System.lineSeparator()
                        + "second" + System.lineSeparator(),
                output);
    }

    @Test
    public void startCapturing_calledAgain_discardsPreviousOutput() {
        Ui ui = new Ui();

        ui.startCapturing();
        ui.showMessage("discarded");
        ui.startCapturing();
        ui.showMessage("kept");

        assertEquals(
                "kept" + System.lineSeparator(),
                ui.stopCapturing());
    }
}
