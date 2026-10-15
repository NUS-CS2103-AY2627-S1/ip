package jiji.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jiji.task.Deadline;
import jiji.task.Event;
import jiji.task.TaskList;
import jiji.task.Todo;

/**
 * Unit tests for {@link Ui}.
 */
public class UiTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        ui = new Ui();
        PersonalityBank.setRandomized(false);
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void formatTaskList_emptyList_returnsHeaderOnly() {
        TaskList tasks = new TaskList();
        assertEquals("Here are the tasks in your list:", ui.formatTaskList(tasks));
    }

    @Test
    public void formatTaskList_populatedList_returnsNumberedItems() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", "2026-08-30"));

        String result = ui.formatTaskList(tasks);
        assertTrue(result.startsWith("Here are the tasks in your list:"));
        assertTrue(result.contains("1.[T][ ] read book"));
        assertTrue(result.contains("2.[D][ ] return book (by: Aug 30 2026)"));
    }

    @Test
    public void formatMatchingTasks_emptyAndPopulated_returnsExpected() {
        assertEquals("Here are the matching tasks in your list:", ui.formatMatchingTasks(List.of()));

        List<jiji.task.Task> matches = List.of(new Todo("study biology"));
        String formatted = ui.formatMatchingTasks(matches);
        assertTrue(formatted.contains("1.[T][ ] study biology"));
    }

    @Test
    public void formatPendingTasks_handlesEmptyAndNonEmpty() {
        TaskList tasks = new TaskList();
        assertEquals("You have no pending tasks! Great job! ₍^. .^₎", ui.formatPendingTasks(tasks));

        Todo pending = new Todo("read book");
        Todo done = new Todo("write code");
        done.markAsDone();
        tasks.add(pending);
        tasks.add(done);

        String result = ui.formatPendingTasks(tasks);
        assertTrue(result.contains("1.[T][ ] read book"));
        assertTrue(!result.contains("write code"));
    }

    @Test
    public void formatDoneTasks_handlesEmptyAndNonEmpty() {
        TaskList tasks = new TaskList();
        assertEquals("You have no completed tasks yet.", ui.formatDoneTasks(tasks));

        Todo pending = new Todo("read book");
        Todo done = new Todo("write code");
        done.markAsDone();
        tasks.add(pending);
        tasks.add(done);

        String result = ui.formatDoneTasks(tasks);
        assertTrue(result.contains("2.[T][X] write code"));
        assertTrue(!result.contains("read book"));
    }

    @Test
    public void formatSchedule_emptyAndMatchingTasks_returnsExpected() {
        TaskList tasks = new TaskList();
        LocalDate targetDate = LocalDate.of(2026, 8, 30);

        String emptyResult = ui.formatSchedule(tasks, targetDate);
        assertTrue(emptyResult.contains("No tasks scheduled for Aug 30 2026"));

        tasks.add(new Deadline("return book", "2026-08-30"));
        tasks.add(new Event("camp", "2026-08-29", "2026-08-31"));
        String populatedResult = ui.formatSchedule(tasks, targetDate);

        assertTrue(populatedResult.startsWith("Schedule for Aug 30 2026:"));
        assertTrue(populatedResult.contains("1.[D][ ] return book"));
        assertTrue(populatedResult.contains("2.[E][ ] camp"));
    }

    @Test
    public void formatTaskLifecycleMethods_returnNonEmptyStrings() {
        Todo todo = new Todo("read book");
        assertTrue(!ui.formatTaskAdded(todo, 1).isEmpty());
        assertTrue(!ui.formatTaskMarked(todo).isEmpty());
        assertTrue(!ui.formatTaskUnmarked(todo).isEmpty());
        assertTrue(!ui.formatTaskRemoved(todo, 0).isEmpty());
        assertTrue(!ui.formatGoodbye().isEmpty());
    }

    @Test
    public void showLine_printsDivider() {
        ui.showLine();
        assertTrue(outContent.toString().contains("____________________________________________________________"));
    }

    @Test
    public void showError_printsMessage() {
        ui.showError("An error occurred");
        assertTrue(outContent.toString().contains("An error occurred"));
    }

    @Test
    public void showLoadingError_printsWarning() {
        ui.showLoadingError();
        assertTrue(outContent.toString().contains("Could not load tasks from storage"));
    }

    @Test
    public void showWelcome_printsBannerAndGreeting() {
        ui.showWelcome();
        String output = outContent.toString();
        assertTrue(output.contains("Jiji saved a warm spot for you"));
    }
}
