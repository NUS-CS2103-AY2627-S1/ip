package chione;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import chione.task.TaskList;
import chione.task.Todo;

/**
 * Tests {@link Ui}, which gathers a reply line by line and hands it over whole.
 *
 * <p>The wording is Chione's own and is free to change, so these tests avoid
 * pinning whole replies. What they pin is the mechanism: that lines are
 * gathered in order, that collecting a reply empties it, and that a count of
 * tasks is spelled out with the right plural.
 */
public class UiTest {
    @Test
    public void consumeResponse_nothingSaid_emptyString() {
        assertEquals("", new Ui().consumeResponse());
    }

    @Test
    public void consumeResponse_afterCollecting_replyStartsAfresh() {
        Ui ui = new Ui();
        ui.showGoodbye();
        ui.consumeResponse();
        // Everything said so far has been handed over, so nothing is left.
        assertEquals("", ui.consumeResponse());
    }

    @Test
    public void consumeResponse_severalThingsSaid_linesKeptInOrder() {
        Ui ui = new Ui();
        ui.showGreeting();
        ui.showGoodbye();
        String reply = ui.consumeResponse();

        String[] lines = reply.split("\\R");
        assertEquals(3, lines.length);
        assertTrue(lines[0].startsWith("Hello, I'm Chione"));
        assertTrue(lines[2].startsWith("Farewell"));
    }

    @Test
    public void showAdded_oneTask_countIsSingular() {
        Ui ui = new Ui();
        ui.showAdded(new Todo("read book"), 1);
        assertTrue(ui.consumeResponse().endsWith("Your list now holds 1 task."));
    }

    @Test
    public void showAdded_severalTasks_countIsPlural() {
        Ui ui = new Ui();
        ui.showAdded(new Todo("read book"), 3);
        assertTrue(ui.consumeResponse().endsWith("Your list now holds 3 tasks."));
    }

    @Test
    public void showRemoved_lastTaskGone_countIsZeroTasks() {
        Ui ui = new Ui();
        ui.showRemoved(new Todo("read book"), 0);
        assertTrue(ui.consumeResponse().endsWith("Your list now holds 0 tasks."));
    }

    @Test
    public void showTasks_emptyList_saysSoWithoutAHeading() {
        Ui ui = new Ui();
        ui.showTasks(new TaskList());
        String reply = ui.consumeResponse();
        assertEquals(1, reply.split("\\R").length);
    }

    @Test
    public void showTasks_twoTasks_numberedFromOne() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"), new Todo("return book"));
        Ui ui = new Ui();
        ui.showTasks(tasks);

        String[] lines = ui.consumeResponse().split("\\R");
        assertEquals(3, lines.length);
        assertEquals("1.[T][ ] read book", lines[1]);
        assertEquals("2.[T][ ] return book", lines[2]);
    }

    @Test
    public void showError_message_explanationKeptOnItsOwnLine() {
        Ui ui = new Ui();
        ui.showError("Tell me what to look for. Try: find book");
        String[] lines = ui.consumeResponse().split("\\R");
        assertEquals(2, lines.length);
        assertEquals("Tell me what to look for. Try: find book", lines[1]);
    }
}
