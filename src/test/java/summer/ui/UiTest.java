package summer.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import summer.task.ToDo;

/**
 * Tests for {@link Ui}'s response buffer, the seam the GUI relies on:
 * {@code show*} methods must accumulate their text, and {@link Ui#flush()}
 * must return it and reset the buffer.
 */
public class UiTest {

    @Test
    public void flush_afterShowMessage_returnsMessageThenEmpties() {
        Ui ui = new Ui();
        ui.showMessage("hello");

        assertEquals("hello", ui.flush());
        assertEquals("", ui.flush());
    }

    @Test
    public void flush_afterShowTaskAdded_containsTaskAndCount() {
        Ui ui = new Ui();
        ui.showTaskAdded(new ToDo("read book", false), 1);

        String response = ui.flush();
        assertTrue(response.contains("on the board"));
        assertTrue(response.contains("[T][ ] read book"));
        assertTrue(response.contains("1 tasks"));
    }

    @Test
    public void flush_afterShowError_usesWipeoutFormat() {
        Ui ui = new Ui();
        ui.showError("bad input");

        assertTrue(ui.flush().contains("Whoa, wipeout. bad input"));
    }
}
