package alice;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UiTest {
    private Ui ui;

    @BeforeEach
    void setUp() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ui = new Ui(new PrintStream(output));
    }

    @Test
    void showError_marksResponseAsError() {
        ui.showError("Invalid input");

        assertTrue(ui.isErrorResponse());
    }

    @Test
    void showUnknownCommand_marksResponseAsError() {
        ui.showUnknownCommand();

        assertTrue(ui.isErrorResponse());
    }

    @Test
    void showUndoUnavailable_marksResponseAsError() {
        ui.showUndoUnavailable();

        assertTrue(ui.isErrorResponse());
    }

    @Test
    void resetResponseState_clearsErrorState() {
        ui.showError("Invalid input");
        ui.resetResponseState();

        assertFalse(ui.isErrorResponse());
    }
}
