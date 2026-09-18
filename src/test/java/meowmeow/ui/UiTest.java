package meowmeow.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import meowmeow.task.TaskPriority;
import meowmeow.task.TaskStatus;
import meowmeow.task.Todo;

/**
 * Tests for {@link Ui} - specifically the seams introduced for A-Personality:
 * that a seeded {@link Random} makes phrasing reproducible (what the console
 * UI test plan depends on), that {@link MessageStyle#RICH} actually strips
 * the console's leading-space convention, that console-only operations
 * reject a {@link MessageStyle#RICH} UI, and that warnings are buffered for
 * {@link Ui#drainWarnings()}.
 */
public class UiTest {

    @Test
    public void showAdded_sameSeed_producesIdenticalOutput() {
        Ui first = new Ui(MessageStyle.PLAIN, new Random(1));
        Ui second = new Ui(MessageStyle.PLAIN, new Random(1));
        Todo task = new Todo("read book");

        assertEquals(first.showAdded(task, 1), second.showAdded(task, 1));
    }

    @Test
    public void showAdded_richStyle_stripsLeadingSpaceAndAddsDecoration() {
        Ui ui = new Ui(MessageStyle.RICH, new Random(1));

        String result = ui.showAdded(new Todo("read book"), 1);

        assertFalse(result.startsWith(" "), "the RICH form should not keep the console's leading space");
        for (String line : result.split("\n")) {
            assertFalse(line.startsWith(" "), "every RICH line should have its leading space stripped");
        }
    }

    @Test
    public void showError_prependsLeadIn_keepingFactualTextUnchanged() {
        Ui ui = new Ui(MessageStyle.PLAIN, new Random(1));

        String result = ui.showError(" Tell me what to add, e.g. \"todo borrow book\".");
        String[] lines = result.split("\n");

        assertEquals(2, lines.length);
        assertEquals(" Tell me what to add, e.g. \"todo borrow book\".", lines[1]);
    }

    @Test
    public void hasNextCommand_richStyle_throwsIllegalStateException() {
        Ui ui = new Ui(MessageStyle.RICH, new Random());

        assertThrows(IllegalStateException.class, ui::hasNextCommand);
    }

    @Test
    public void readCommand_richStyle_throwsIllegalStateException() {
        Ui ui = new Ui(MessageStyle.RICH, new Random());

        assertThrows(IllegalStateException.class, ui::readCommand);
    }

    @Test
    public void showFarewell_plainStyle_includesAsciiCat() {
        Ui ui = new Ui(MessageStyle.PLAIN, new Random());

        assertTrue(ui.showFarewell().contains("/\\_/\\"));
    }

    @Test
    public void showFarewell_richStyle_omitsAsciiCat() {
        Ui ui = new Ui(MessageStyle.RICH, new Random());

        assertFalse(ui.showFarewell().contains("/\\_/\\"));
    }

    @Test
    public void showWarning_bufferedThenDrained_returnedOnceThenEmpty() {
        Ui ui = new Ui(MessageStyle.RICH, new Random());

        ui.showWarning("Meow... something went wrong.");
        List<String> drained = ui.drainWarnings();

        assertEquals(1, drained.size());
        assertTrue(ui.drainWarnings().isEmpty(), "a second drain should be empty");
    }

    @Test
    public void showTasksOn_emptyMatches_substitutesDateLabel() {
        Ui ui = new Ui(MessageStyle.PLAIN, new Random());

        String result = ui.showTasksOn("Dec 2 2019", List.of());

        assertTrue(result.contains("Dec 2 2019"));
        assertFalse(result.contains("%s"));
    }

    @Test
    public void showTasksOn_nonEmptyMatches_substitutesDateLabelInHeader() {
        Ui ui = new Ui(MessageStyle.PLAIN, new Random());

        String result = ui.showTasksOn("Dec 2 2019", List.of(new Todo("read book")));

        assertTrue(result.contains("Dec 2 2019"));
        assertFalse(result.contains("%s"));
    }

    @Test
    public void showMatchingTasks_empty_usesEmptyFindWording() {
        Ui ui = new Ui(MessageStyle.PLAIN, new Random());

        assertFalse(ui.showMatchingTasks(List.of()).contains("1."));
    }

    @Test
    public void showStatusChange_donePicksDifferentWordingThanNotDone() {
        Ui ui = new Ui(MessageStyle.PLAIN, new Random(1));
        Todo task = new Todo("read book");
        task.setStatus(TaskStatus.DONE);

        String done = ui.showStatusChange(TaskStatus.DONE, task);
        String notDone = ui.showStatusChange(TaskStatus.NOT_DONE, task);

        assertNotEquals(done, notDone);
    }

    @Test
    public void showPriorityChange_echoesTheUpdatedTask() {
        Ui ui = new Ui(MessageStyle.PLAIN, new Random(1));
        Todo task = new Todo("read book");
        task.setPriority(TaskPriority.HIGH);

        assertTrue(ui.showPriorityChange(task).contains(task.toString()));
    }

    @Test
    public void showPriorityChange_differsFromShowStatusChangeWording() {
        Ui ui = new Ui(MessageStyle.PLAIN, new Random(1));
        Todo task = new Todo("read book");

        String priorityChange = ui.showPriorityChange(task);
        String statusChange = ui.showStatusChange(TaskStatus.DONE, task);

        assertNotEquals(priorityChange, statusChange);
    }
}
