package dennis.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import dennis.DennisException;
import dennis.task.Todo;

/**
 * Tests for {@link DialogUi}: that command output is accumulated into the
 * buffer, joined with line separators, and cleared by {@link DialogUi#drain()}.
 * The overridden {@code show*} methods just delegate to the buffer, so only a
 * representative few are exercised here.
 */
public class DialogUiTest {

    @Test
    public void drain_singleMessage_returnsThatMessage() {
        DialogUi ui = new DialogUi();
        ui.showMessage("hello");
        assertEquals("hello", ui.drain());
    }

    @Test
    public void drain_multipleMessages_joinedWithLineSeparator() {
        DialogUi ui = new DialogUi();
        ui.showMessage("first");
        ui.showMessage("second");
        assertEquals("first" + System.lineSeparator() + "second", ui.drain());
    }

    @Test
    public void drain_calledAgain_returnsEmptyString() {
        DialogUi ui = new DialogUi();
        ui.showMessage("only once");
        ui.drain();
        assertEquals("", ui.drain());
    }

    @Test
    public void showLine_appendsNothing() {
        DialogUi ui = new DialogUi();
        ui.showLine();
        assertEquals("", ui.drain());
    }

    @Test
    public void showError_isPrefixedWithErrorMarker() {
        DialogUi ui = new DialogUi();
        ui.showError("something went wrong");
        assertEquals("HISS!! something went wrong", ui.drain());
    }

    @Test
    public void showAddedTask_formatsConfirmationOverThreeLines()
            throws DennisException {
        DialogUi ui = new DialogUi();
        Todo todo = new Todo("read book");

        ui.showAddedTask(todo, 1);

        String[] lines = ui.drain().split(System.lineSeparator(), -1);
        assertEquals(3, lines.length);
        assertEquals("Purrfect, I've added this task:", lines[0]);
        assertEquals("Meow you have 1 tasks!", lines[2]);
        assertTrue(lines[1].contains("[T]"));
        assertTrue(lines[1].endsWith("read book"));
    }

    @Test
    public void showAddedTask_longSpacelessDescription_staysOnTheBoxLine()
            throws DennisException {
        DialogUi ui = new DialogUi();
        String longName = "a".repeat(80);

        ui.showAddedTask(new Todo(longName), 1);

        String taskLine = ui.drain().split(System.lineSeparator(), -1)[1];
        // No plain space between the status box and the description, so the
        // renderer cannot drop the description onto a line of its own.
        assertFalse(taskLine.contains("] " + longName.charAt(0)));
        assertTrue(taskLine.endsWith(longName));
    }
}
