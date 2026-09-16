package lebronjames.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lebronjames.LebronJamesException;
import lebronjames.task.Deadline;
import lebronjames.task.Task;
import lebronjames.task.TaskDateTime;
import lebronjames.task.Todo;

/**
 * Tests the parts of {@link Ui} that make a decision rather than simply
 * repeating what they were given: whether to mention loaded tasks at all, and
 * whether a day is shown as busy or free.
 *
 * <p>Ui now collects its replies instead of printing them, so each test reads
 * the reply back with {@link Ui#getResponse()} rather than capturing
 * {@code System.out}. Numbering is checked too, since the numbers shown by
 * {@code list} are what the user types into {@code mark} and {@code delete}.
 */
public class UiTest {
    private Ui ui;

    @BeforeEach
    public void createUi() {
        ui = new Ui();
    }

    @Test
    public void getResponse_calledTwice_secondReplyIsEmpty() {
        // Each reply must stand alone, or the GUI would repeat the previous
        // answer inside the next speech bubble.
        ui.showError("Something went wrong.");
        ui.getResponse();

        assertEquals("", ui.getResponse());
    }

    @Test
    public void showWelcome_bannerKeepsItsIndentation() {
        // The crown is drawn by padding its lines, so trimming the leading
        // spaces off the first one would leave it hanging off to one side.
        ui.showWelcome();

        String reply = ui.getResponse();
        assertTrue(reply.startsWith("      /\\"), "the crown's first line must keep its padding");
        assertTrue(reply.contains("They call me The King"), "should introduce the chatbot by name");
    }

    @Test
    public void showLoaded_noTasks_saysNothing() {
        // Nothing was restored, so there is nothing worth telling the user.
        ui.showLoaded(0);

        assertEquals("", ui.getResponse());
    }

    @Test
    public void showLoaded_someTasks_reportsHowMany() {
        ui.showLoaded(3);

        assertTrue(ui.getResponse().contains("3"), "should say how many tasks were loaded");
    }

    @Test
    public void showTaskList_severalTasks_numberedFromOne() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));

        ui.showTaskList(tasks);

        // These numbers are what the user types into mark and delete, so
        // starting at 1 is not cosmetic.
        String reply = ui.getResponse();
        assertTrue(reply.contains("1.[T][ ] first"));
        assertTrue(reply.contains("2.[T][ ] second"));
    }

    @Test
    public void showTaskList_emptyList_headingOnly() {
        ui.showTaskList(new ArrayList<>());

        assertEquals("Here is your playbook:", ui.getResponse());
    }

    @Test
    public void showTasksOn_noTasks_saysTheDayIsFree() {
        ui.showTasksOn(new ArrayList<>(), LocalDate.of(2019, 12, 2));

        String reply = ui.getResponse();
        assertTrue(reply.contains("Rest day"), "should say the day is free");
        assertTrue(reply.contains("Dec 02 2019"), "should name the date asked about");
    }

    @Test
    public void showTasksOn_someTasks_listedAndNumberedFromOne() throws LebronJamesException {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Deadline("return book", TaskDateTime.parse("2019-12-02")));

        ui.showTasksOn(tasks, LocalDate.of(2019, 12, 2));

        String reply = ui.getResponse();
        assertFalse(reply.contains("Rest day"));
        assertTrue(reply.contains("1.[D][ ] return book"));
    }

    @Test
    public void showMatchingTasks_noMatches_saysSo() {
        ui.showMatchingTasks(new ArrayList<>());

        assertTrue(ui.getResponse().contains("No plays match"));
    }

    @Test
    public void showMatchingTasks_someMatches_listedAndNumberedFromOne() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));

        ui.showMatchingTasks(tasks);

        String reply = ui.getResponse();
        assertTrue(reply.contains("Here are the plays that match:"));
        assertTrue(reply.contains("1.[T][ ] read book"));
        assertTrue(reply.contains("2.[T][ ] return book"));
    }

    @Test
    public void showTaskAdded_reportsTheTaskAndTheNewCount() {
        ui.showTaskAdded(new Todo("read book"), 5);

        String reply = ui.getResponse();
        assertTrue(reply.contains("[T][ ] read book"));
        assertTrue(reply.contains("5"), "should report the new list size");
    }

    @Test
    public void showTaskRemoved_reportsTheTaskAndTheRemainingCount() {
        ui.showTaskRemoved(new Todo("read book"), 2);

        String reply = ui.getResponse();
        assertTrue(reply.contains("[T][ ] read book"));
        assertTrue(reply.contains("2"));
    }

    @Test
    public void showError_returnsTheMessageUnchanged() {
        ui.showError("Oops! something went wrong.");

        assertEquals("Oops! something went wrong.", ui.getResponse());
    }

    @Test
    public void showLoadingError_explainsTheListStartsEmpty() {
        ui.showLoadingError("Oops! I could not read the file.");

        String reply = ui.getResponse();
        assertTrue(reply.contains("Oops! I could not read the file."));
        assertTrue(reply.contains("playbook empty"), "should say what happens next");
    }
}
