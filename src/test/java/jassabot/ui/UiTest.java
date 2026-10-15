package jassabot.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import jassabot.task.Todo;

/**
 * Tests the plain response formatter used by the GUI without launching JavaFX.
 */
public class UiTest {
    private final List<String> output = new ArrayList<>();
    private final Ui ui = new Ui(output::add);

    @Test
    public void showResponse_multilineResponse_preservesTextWithoutDecorations() {
        ui.showResponse("first\n\nlast");
        assertEquals(List.of("first\n\nlast"), output);
    }

    @Test
    public void showLoadingWarnings_emptyAndMultipleWarnings_preservesOrder() {
        ui.showLoadingWarnings(List.of());
        assertEquals(List.of(), output);
        ui.showLoadingWarnings(List.of("first problem", "second problem"));
        assertEquals(List.of("WARNING: first problem", "WARNING: second problem"), output);
    }

    @Test
    public void readCommand_responseCollector_hasNoInput() {
        assertFalse(ui.hasNextCommand());
        assertThrows(NoSuchElementException.class, ui::readCommand);
    }

    @Test
    public void showInputClosed_responseCollector_omitsConsoleDividers() {
        ui.showInputClosed();
        assertEquals(List.of("Input closed. Take care, and keep growing."), output);
    }

    @Test
    public void showTaskAdded_countsZeroOneAndMany_usesCorrectNoun() {
        Todo task = new Todo("read");
        for (int count : List.of(0, 1, 2)) {
            output.clear();
            ui.showTaskAdded(task, count);
            String noun = count == 1 ? "task" : "tasks";
            assertEquals(List.of("Planted a new task:", "  [T][ ] read",
                    "Your garden now holds " + count + " " + noun + "."), output);
        }
    }
}
