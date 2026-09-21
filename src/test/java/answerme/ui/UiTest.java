package answerme.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class UiTest {
    @Test
    public void showMessage_validMessage_setsNormalResponseType() {
        Ui ui = new Ui(false);

        ui.showMessage("Here are your tasks.");

        assertEquals(ResponseType.NORMAL, ui.getLatestResponseType());
    }

    @Test
    public void showSuccessMessage_validMessage_setsSuccessResponseType() {
        Ui ui = new Ui(false);

        ui.showSuccessMessage("Task added.");

        assertEquals(ResponseType.SUCCESS, ui.getLatestResponseType());
    }

    @Test
    public void showError_validReason_addsErrorPrefixAndSetsErrorResponseType() {
        Ui ui = new Ui(false);

        ui.showError("The task does not exist.");

        assertEquals("Oh no! The task does not exist.", ui.getLatestResponse());
        assertEquals(ResponseType.ERROR, ui.getLatestResponseType());
    }

    @Test
    public void showLoadingError_validReason_doesNotAddErrorPrefixAndSetsErrorResponseType() {
        Ui ui = new Ui(false);

        ui.showLoadingError("Unable to open the data file.");

        assertEquals("Issue loading saved tasks.\n"
                        + "Unable to open the data file.\n"
                        + "Please repair the data file and then restart the application.",
                ui.getLoadingErrorMessage());
        assertEquals(ResponseType.ERROR, ui.getLatestResponseType());
    }

    @Test
    public void showLoadingWarning_multipleWarnings_doesNotAddErrorPrefixAndSetsErrorResponseType() {
        Ui ui = new Ui(false);
        List<String> warnings = List.of(
                "Invalid task status on line 2.",
                "Invalid Event datetime on line 4."
        );

        ui.showLoadingWarning(warnings);

        assertEquals("Some saved tasks could not be loaded and were skipped:\n"
                        + "- Invalid task status on line 2.\n"
                        + "- Invalid Event datetime on line 4.\n\n"
                        + "The remaining tasks were loaded successfully. "
                        + "Skipped entries will be removed the next time tasks are saved.",
                ui.getLoadingWarningMessage());
        assertEquals(ResponseType.ERROR, ui.getLatestResponseType());
    }
}
