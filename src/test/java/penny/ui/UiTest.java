package penny.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import penny.task.Task;
import penny.task.Todo;

/**
 * Unit test suite for {@link Ui}.
 * Tests user interface formatting operations for task lists, query results, and feedback messages
 * with randomized thematic phrases.
 */
public class UiTest {

    private Ui ui;
    private Task sampleTodo;

    @BeforeEach
    public void setUp() {
        ui = new Ui();
        sampleTodo = new Todo("read book");
    }

    @Test
    public void showTaskList_emptyList_returnsEmptyMessage() {
        String output = ui.showTaskList(Collections.emptyList());
        assertNotNull(output);
        assertFalse(output.isBlank());
    }

    @Test
    public void showTaskList_nonEmptyList_returnsFormattedNumberedList() {
        List<Task> tasks = List.of(sampleTodo, new Todo("return book"));
        String output = ui.showTaskList(tasks);
        assertTrue(output.contains("1.[T][ ] read book\n2.[T][ ] return book"));
    }

    @Test
    public void showTasksOnDate_emptyList_returnsEmptyMessage() {
        String output = ui.showTasksOnDate(Collections.emptyList(), "Oct 15 2019");
        assertTrue(output.contains("Oct 15 2019"));
    }

    @Test
    public void showTasksOnDate_nonEmptyList_returnsFormattedNumberedList() {
        List<Task> tasks = List.of(sampleTodo);
        String output = ui.showTasksOnDate(tasks, "Oct 15 2019");
        assertTrue(output.contains("Oct 15 2019"));
        assertTrue(output.contains("1.[T][ ] read book"));
    }

    @Test
    public void showMatchingTasks_emptyList_returnsEmptyMessage() {
        String output = ui.showMatchingTasks(Collections.emptyList(), "read");
        assertTrue(output.contains("read"));
    }

    @Test
    public void showMatchingTasks_nonEmptyList_returnsFormattedNumberedList() {
        List<Task> tasks = List.of(sampleTodo);
        String output = ui.showMatchingTasks(tasks, "read");
        assertTrue(output.contains("read"));
        assertTrue(output.contains("1.[T][ ] read book"));
    }

    @Test
    public void showTaskAdded_validTask_returnsConfirmationMessage() {
        String output = ui.showTaskAdded(sampleTodo, 1);
        assertTrue(output.contains("[T][ ] read book"));
        assertTrue(output.contains("1"));
    }

    @Test
    public void showTaskMarked_validTask_returnsConfirmationMessage() {
        sampleTodo.markAsDone();
        String output = ui.showTaskMarked(sampleTodo);
        assertTrue(output.contains("[X] read book"));
    }

    @Test
    public void showTaskUnmarked_validTask_returnsConfirmationMessage() {
        String output = ui.showTaskUnmarked(sampleTodo);
        assertTrue(output.contains("[ ] read book"));
    }

    @Test
    public void showTaskDeleted_validTask_returnsConfirmationMessage() {
        String output = ui.showTaskDeleted(sampleTodo, 0);
        assertTrue(output.contains("[T][ ] read book"));
        assertTrue(output.contains("0"));
    }

    @Test
    public void showWelcome_invoked_returnsNonEmptyThemedGreeting() {
        String output = ui.showWelcome();
        assertNotNull(output);
        assertFalse(output.isBlank());
    }

    @Test
    public void showGoodbye_invoked_returnsNonEmptyThemedGoodbye() {
        String output = ui.showGoodbye();
        assertNotNull(output);
        assertFalse(output.isBlank());
    }

    @Test
    public void showError_validErrorMessage_returnsThemedErrorMessage() {
        String output = ui.showError("Syntax failure");
        assertTrue(output.contains("Syntax failure"));
    }

    @Test
    public void showTaskAdded_fixedRandomIndex_returnsExpectedPhrase() {
        Random fixedRandom = new Random() {
            @Override
            public int nextInt(int bound) {
                return 0;
            }
        };
        Ui deterministicUi = new Ui(fixedRandom);
        String output = deterministicUi.showTaskAdded(sampleTodo, 3);
        assertTrue(output.contains("Mission directive logged into SP//dr!"));
        assertTrue(output.contains("3 active operation(s) ready for deployment!"));
    }
}
