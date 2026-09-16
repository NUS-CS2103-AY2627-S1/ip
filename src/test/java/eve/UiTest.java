package eve;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import eve.task.Task;
import eve.task.ToDo;

/**
 * Tests {@link Ui}'s show methods: what each one actually prints to
 * {@code System.out}, captured via {@link TestUtil#captureStdOut}.
 */
public class UiTest {
    private final Ui ui = new Ui();

    @Test
    public void showWelcome_printsBannerGreetingAndHelpHint() throws Exception {
        String output = TestUtil.captureStdOut(ui::showWelcome);

        assertTrue(output.contains("E V E"));
        assertTrue(output.contains("Eve"));
        assertTrue(output.contains("help"));
    }

    @Test
    public void getWelcomeMessage_mentionsHelp() {
        assertTrue(ui.getWelcomeMessage().contains("help"));
    }

    @Test
    public void showHelp_listsEveryCommandUsage() throws Exception {
        String output = TestUtil.captureStdOut(ui::showHelp);

        assertTrue(output.contains("todo <description>"));
        assertTrue(output.contains("bye"));
    }

    @Test
    public void showGoodbye_printsFarewell() throws Exception {
        String output = TestUtil.captureStdOut(ui::showGoodbye);

        assertTrue(output.contains("Bye"));
    }

    @Test
    public void showTaskList_printsEveryTaskNumberedFromOne() throws Exception {
        List<Task> tasks = List.of(new ToDo("read book"), new ToDo("return book"));

        String output = TestUtil.captureStdOut(() -> ui.showTaskList(tasks));

        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("2.[T][ ] return book"));
    }

    @Test
    public void showTaskList_empty_stillPrintsHeader() throws Exception {
        String output = TestUtil.captureStdOut(() -> ui.showTaskList(List.of()));

        assertTrue(output.contains("list"));
    }

    @Test
    public void showMatchingTasks_withMatches_printsThem() throws Exception {
        List<Task> matches = List.of(new ToDo("read book"));

        String output = TestUtil.captureStdOut(() -> ui.showMatchingTasks(matches));

        assertTrue(output.contains("1.[T][ ] read book"));
    }

    @Test
    public void showMatchingTasks_empty_printsNoMatchesMessage() throws Exception {
        String output = TestUtil.captureStdOut(() -> ui.showMatchingTasks(List.of()));

        assertTrue(output.toLowerCase().contains("no matches"));
    }

    @Test
    public void showTasksOnDate_withMatches_printsThemAndTheDate() throws Exception {
        LocalDate date = LocalDate.of(2019, 12, 2);
        List<Task> matches = List.of(new ToDo("read book"));

        String output = TestUtil.captureStdOut(() -> ui.showTasksOnDate(date, matches));

        assertTrue(output.contains("Dec 2 2019"));
        assertTrue(output.contains("1.[T][ ] read book"));
    }

    @Test
    public void showTasksOnDate_empty_printsNoTasksMessageWithDate() throws Exception {
        LocalDate date = LocalDate.of(2019, 1, 1);

        String output = TestUtil.captureStdOut(() -> ui.showTasksOnDate(date, List.of()));

        assertTrue(output.contains("Jan 1 2019"));
    }

    @Test
    public void showSchedule_withTasks_printsThem() throws Exception {
        List<Task> scheduled = List.of(new ToDo("read book"));

        String output = TestUtil.captureStdOut(() -> ui.showSchedule(scheduled));

        assertTrue(output.contains("1.[T][ ] read book"));
    }

    @Test
    public void showSchedule_empty_printsWideOpenMessage() throws Exception {
        String output = TestUtil.captureStdOut(() -> ui.showSchedule(List.of()));

        assertTrue(output.contains("wide open"));
    }

    @Test
    public void showTaskMarked_printsConfirmationAndTheTask() throws Exception {
        ToDo task = new ToDo("read book");
        task.markAsDone();

        String output = TestUtil.captureStdOut(() -> ui.showTaskMarked(task));

        assertTrue(output.contains("[T][X] read book"));
    }

    @Test
    public void showTaskUnmarked_printsConfirmationAndTheTask() throws Exception {
        ToDo task = new ToDo("read book");

        String output = TestUtil.captureStdOut(() -> ui.showTaskUnmarked(task));

        assertTrue(output.contains("[T][ ] read book"));
    }

    @Test
    public void showTaskAdded_printsTheTaskAndTheRunningCount() throws Exception {
        ToDo task = new ToDo("read book");

        String output = TestUtil.captureStdOut(() -> ui.showTaskAdded(task, 3));

        assertTrue(output.contains("[T][ ] read book"));
        assertTrue(output.contains("3"));
    }

    @Test
    public void showTaskDeleted_printsTheTaskAndTheRemainingCount() throws Exception {
        ToDo task = new ToDo("read book");

        String output = TestUtil.captureStdOut(() -> ui.showTaskDeleted(task, 1));

        assertTrue(output.contains("[T][ ] read book"));
        assertTrue(output.contains("1"));
    }

    @Test
    public void showError_printsTheMessageVerbatim() throws Exception {
        String output = TestUtil.captureStdOut(() -> ui.showError("Oops, something specific went wrong."));

        assertTrue(output.contains("Oops, something specific went wrong."));
    }

    @Test
    public void showLoadingError_printsMessageAndStartingFreshNote() throws Exception {
        String output = TestUtil.captureStdOut(() -> ui.showLoadingError("Could not read the file."));

        assertTrue(output.contains("Could not read the file."));
        assertTrue(output.toLowerCase().contains("fresh"));
    }
}
