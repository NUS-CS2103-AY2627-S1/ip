package avon.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import avon.exception.AvonException;
import avon.task.TaskList;

class UiTest {
    @Test
    void commandInput_multipleLines_readsCompleteLinesUntilExhausted() {
        ByteArrayInputStream input = new ByteArrayInputStream(
                "todo read Hamlet\nbye\n".getBytes(StandardCharsets.UTF_8));
        Ui ui = new Ui(input, new PrintStream(new ByteArrayOutputStream()));

        assertTrue(ui.hasNextCommand());
        assertEquals("todo read Hamlet", ui.readCommand());
        assertTrue(ui.hasNextCommand());
        assertEquals("bye", ui.readCommand());
        assertFalse(ui.hasNextCommand());
        ui.close();
    }

    @Test
    void showTaskList_emptyList_reportsEmptyState() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));

        ui.showTaskList(new TaskList());

        assertEquals("Avon:\tThy task list is empty.\n", output.toString());
        ui.close();
    }

    @Test
    void showError_multilineMessage_indentsFollowUpLines() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));

        ui.showError(new AvonException("First line.\nSecond line."));

        assertEquals("""
                Avon:\tPardon, I beseech thee! First line.
                        Second line.
                """, output.toString());
        ui.close();
    }
}
