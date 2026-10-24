package kiki.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class DialogBoxTest {

    @Test
    public void isErrorResponse_errorPrefix_returnsTrue() {
        assertTrue(DialogBox.isErrorResponse("OOPS!!! Unknown command."));
    }

    @Test
    public void isErrorResponse_normalResponse_returnsFalse() {
        assertFalse(DialogBox.isErrorResponse("Task added."));
    }

    @Test
    public void getMessageMaxWidth_narrowContainer_keepsReadableMinimum() {
        assertEquals(120, DialogBox.getMessageMaxWidth(100));
    }

    @Test
    public void getMessageMaxWidth_wideContainer_respectsMaximum() {
        assertEquals(520, DialogBox.getMessageMaxWidth(1000));
    }

    @Test
    public void getMessageMaxWidth_standardContainer_usesAvailableSpace() {
        assertEquals(287, DialogBox.getMessageMaxWidth(400));
    }
}
