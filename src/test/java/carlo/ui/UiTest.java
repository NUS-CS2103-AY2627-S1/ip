package carlo.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import carlo.task.Task;
import carlo.task.Todo;

/**
 * Tests for the console-output methods of {@link Ui}.
 *
 * <p>Each test redirects {@link System#out} into an in-memory buffer so the
 * printed text can be inspected, then restores the original stream
 * afterwards.
 */
class UiTest {
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputBuffer;
    private Ui ui;

    @BeforeEach
    void setUp() {
        outputBuffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputBuffer));
        ui = new Ui();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private String capturedOutput() {
        return outputBuffer.toString();
    }

    @Test
    void showTaskAdded_printsTaskAndUpdatedCount() {
        Todo todo = new Todo("read book");
        ui.showTaskAdded(todo, 3);
        assertTrue(capturedOutput().contains("read book"));
        assertTrue(capturedOutput().contains("3"));
    }

    @Test
    void showTaskMarked_printsTask() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        ui.showTaskMarked(todo);
        assertTrue(capturedOutput().contains("[T][X] read book"));
    }

    @Test
    void showTaskUnmarked_printsTask() {
        Todo todo = new Todo("read book");
        ui.showTaskUnmarked(todo);
        assertTrue(capturedOutput().contains("[T][ ] read book"));
    }

    @Test
    void showTaskDeleted_printsTaskAndRemainingCount() {
        Todo todo = new Todo("read book");
        ui.showTaskDeleted(todo, 1);
        assertTrue(capturedOutput().contains("read book"));
        assertTrue(capturedOutput().contains("1"));
    }

    @Test
    void showTaskList_printsEveryTaskWithOneBasedNumbering() {
        List<Task> tasks = List.of(new Todo("read book"), new Todo("buy milk"));
        ui.showTaskList(tasks);
        String output = capturedOutput();
        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("2.[T][ ] buy milk"));
    }

    @Test
    void showMatchingTasks_someMatches_printsOnlyMatchingTasksRenumbered() {
        List<Task> tasks = List.of(new Todo("read book"), new Todo("buy milk"));
        boolean[] matches = {true, false};
        ui.showMatchingTasks(tasks, matches);
        String output = capturedOutput();
        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(!output.contains("buy milk"));
    }

    @Test
    void showMatchingTasks_noMatches_printsNotFoundMessage() {
        List<Task> tasks = List.of(new Todo("buy milk"));
        boolean[] matches = {false};
        ui.showMatchingTasks(tasks, matches);
        assertTrue(capturedOutput().contains("couldn't find any matching tasks"));
    }

    @Test
    void showTasksOnDate_hasMatch_printsMatchingTask() {
        List<Task> tasks = List.of(new Todo("read book"));
        boolean[] matches = {true};
        ui.showTasksOnDate(LocalDate.of(2019, 12, 2), tasks, matches);
        assertTrue(capturedOutput().contains("read book"));
    }

    @Test
    void showTasksOnDate_noMatch_printsFreeDayMessage() {
        List<Task> tasks = List.of(new Todo("read book"));
        boolean[] matches = {false};
        ui.showTasksOnDate(LocalDate.of(2019, 12, 2), tasks, matches);
        assertTrue(capturedOutput().contains("Free day"));
    }

    @Test
    void showGoodbye_printsFarewellMessage() {
        ui.showGoodbye();
        assertTrue(capturedOutput().contains("Byeeee"));
    }

    @Test
    void showError_printsMessageAndHelpText() {
        ui.showError("something went wrong");
        String output = capturedOutput();
        assertTrue(output.contains("something went wrong"));
        assertTrue(output.contains("Say 'bye'") || output.contains("possible list items"));
    }
}
