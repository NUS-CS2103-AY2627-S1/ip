package yapbot.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import yapbot.task.Task;
import yapbot.task.Todo;

public class GuiUiTest {

    @Test
    public void consumeResponse_afterShowMessage_returnsMessageText() {
        GuiUi ui = new GuiUi();

        ui.showMessage("custom message");

        assertEquals("custom message", ui.consumeResponse());
    }

    @Test
    public void consumeResponse_clearsBufferForNextCall() {
        GuiUi ui = new GuiUi();
        ui.showMessage("first");
        ui.consumeResponse();

        ui.showMessage("second");

        assertEquals("second", ui.consumeResponse());
    }

    @Test
    public void wasLastResponseAnError_afterShowMessage_returnsTrue() {
        GuiUi ui = new GuiUi();

        ui.showMessage("something went wrong");

        assertTrue(ui.wasLastResponseAnError());
    }

    @Test
    public void wasLastResponseAnError_afterSuccessConfirmation_returnsFalse() {
        GuiUi ui = new GuiUi();
        Task task = new Todo("read book");

        ui.showTaskAdded(task, 1);

        assertFalse(ui.wasLastResponseAnError());
    }

    @Test
    public void wasLastResponseAnError_afterStartNewResponse_returnsFalse() {
        GuiUi ui = new GuiUi();
        ui.showMessage("an earlier error");

        ui.startNewResponse();

        assertFalse(ui.wasLastResponseAnError());
    }
}
