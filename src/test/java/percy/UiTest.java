package percy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Ui}'s buffering: each {@code show*} call should append the
 * exact text CLI users have always seen, and {@link Ui#flush()} should return
 * it once and then start clean for the next command.
 */
public class UiTest {
    private static final String LINE =
            "____________________________________________________________";

    @Test
    public void flush_withNothingShown_returnsEmptyString() {
        Ui ui = new Ui();
        assertEquals("", ui.flush());
    }

    @Test
    public void flush_clearsTheBufferForTheNextCommand() {
        Ui ui = new Ui();
        ui.showLine();
        assertTrue(!ui.flush().isEmpty());
        assertEquals("", ui.flush());
    }

    @Test
    public void showLine_isTheDividerFramedByNothingElse() {
        Ui ui = new Ui();
        ui.showLine();
        assertEquals(LINE + System.lineSeparator(), ui.flush());
    }

    @Test
    public void showError_framesTheMessageWithDividers() {
        Ui ui = new Ui();
        ui.showError("OOPS!!! something went wrong.");
        assertEquals(
                LINE + System.lineSeparator()
                        + "OOPS!!! something went wrong." + System.lineSeparator()
                        + LINE + System.lineSeparator(),
                ui.flush());
    }

    @Test
    public void showGoodbye_matchesTheFarewellText() {
        Ui ui = new Ui();
        ui.showGoodbye();
        assertEquals(
                LINE + System.lineSeparator()
                        + "Bye. Hope to see you again soon!" + System.lineSeparator()
                        + LINE + System.lineSeparator(),
                ui.flush());
    }

    @Test
    public void showTaskList_numbersEachTaskFromOne() {
        Ui ui = new Ui();
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", LocalDate.of(2019, 10, 15)));

        ui.showTaskList(tasks);

        assertEquals(
                LINE + System.lineSeparator()
                        + "Here are the tasks in your list:" + System.lineSeparator()
                        + "1.[T][ ] read book" + System.lineSeparator()
                        + "2.[D][ ] return book (by: Oct 15 2019)" + System.lineSeparator()
                        + LINE + System.lineSeparator(),
                ui.flush());
    }

    @Test
    public void showMatchingTasks_usesTheFindHeader() {
        Ui ui = new Ui();
        TaskList matches = new TaskList();
        matches.add(new Todo("read book"));

        ui.showMatchingTasks(matches);

        assertEquals(
                LINE + System.lineSeparator()
                        + "Here are the matching tasks in your list:" + System.lineSeparator()
                        + "1.[T][ ] read book" + System.lineSeparator()
                        + LINE + System.lineSeparator(),
                ui.flush());
    }

    @Test
    public void showAdded_reportsTheTaskAndNewTotal() {
        Ui ui = new Ui();
        Todo task = new Todo("read book");

        ui.showAdded(task, 3);

        assertEquals(
                LINE + System.lineSeparator()
                        + "Got it. I've added this task:" + System.lineSeparator()
                        + "  [T][ ] read book" + System.lineSeparator()
                        + "Now you have 3 tasks in the list." + System.lineSeparator()
                        + LINE + System.lineSeparator(),
                ui.flush());
    }

    @Test
    public void showRemoved_reportsTheTaskAndNewTotal() {
        Ui ui = new Ui();
        Todo task = new Todo("read book");

        ui.showRemoved(task, 1);

        assertEquals(
                LINE + System.lineSeparator()
                        + "Noted. I've removed this task:" + System.lineSeparator()
                        + "  [T][ ] read book" + System.lineSeparator()
                        + "Now you have 1 tasks in the list." + System.lineSeparator()
                        + LINE + System.lineSeparator(),
                ui.flush());
    }

    @Test
    public void showMarked_and_showUnmarked_reflectTheTaskAsGiven() {
        Ui ui = new Ui();
        Todo task = new Todo("read book");
        task.setDone(true);

        ui.showMarked(task);
        assertEquals(
                LINE + System.lineSeparator()
                        + "Nice! I've marked this task as done:" + System.lineSeparator()
                        + "  [T][X] read book" + System.lineSeparator()
                        + LINE + System.lineSeparator(),
                ui.flush());

        task.setDone(false);
        ui.showUnmarked(task);
        assertEquals(
                LINE + System.lineSeparator()
                        + "OK, I've marked this task as not done yet:" + System.lineSeparator()
                        + "  [T][ ] read book" + System.lineSeparator()
                        + LINE + System.lineSeparator(),
                ui.flush());
    }

    @Test
    public void showUpdated_reflectsTheTaskAsGiven() {
        Ui ui = new Ui();
        Todo task = new Todo("read the whole book");

        ui.showUpdated(task);

        assertEquals(
                LINE + System.lineSeparator()
                        + "Nice! I've updated this task:" + System.lineSeparator()
                        + "  [T][ ] read the whole book" + System.lineSeparator()
                        + LINE + System.lineSeparator(),
                ui.flush());
    }

    @Test
    public void showLoadingError_isAnErrorAboutLoading() {
        Ui ui = new Ui();
        ui.showLoadingError();
        String shown = ui.flush();
        assertTrue(shown.contains("Could not load saved tasks"));
    }

    @Test
    public void showWelcome_endsWithTheGreetingAndADivider() {
        Ui ui = new Ui();
        ui.showWelcome();
        String shown = ui.flush();
        assertTrue(shown.contains("Hello! I'm Percy."));
        assertTrue(shown.contains("What can I do for you?"));
        assertTrue(shown.endsWith(LINE + System.lineSeparator()));
    }
}
