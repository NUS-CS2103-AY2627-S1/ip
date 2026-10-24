package orbit.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import orbit.task.Task;
import orbit.task.Todo;

/**
 * Checks that varargs-based messages retain their exact text and line endings.
 */
class UiTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final Ui ui = new Ui(InputStream.nullInputStream(),
            new PrintStream(output, true, StandardCharsets.UTF_8));

    @Test
    void showGoodbye_singleLine_preservesExactOutput() {
        ui.showGoodbye();

        assertEquals("Bye. Hope to see you again soon!" + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showMarkedTask_twoLines_preservesSpacingAndUnicode() {
        Todo task = new Todo("read café notes");
        task.markAsDone();

        ui.showMarkedTask(task);

        String expected = "Nice! I've marked this task as done:" + System.lineSeparator()
                + "  [T][X] read café notes" + System.lineSeparator();
        assertEquals(expected, output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showAddedTask_threeLines_preservesTextAndOrder() {
        ui.showAddedTask(new Todo("read book"), 3);

        String expected = "Got it. I've added this task:" + System.lineSeparator()
                + "  [T][ ] read book" + System.lineSeparator()
                + "Now you have 3 tasks in the list." + System.lineSeparator();
        assertEquals(expected, output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showTaskLists_differentHeadingsShareNumberingFormat() {
        List<Task> tasks = List.of(new Todo("first"), new Todo("second"));

        ui.showTaskList(tasks);
        ui.showMatchingTasks(tasks);

        String numberedTasks = "1.[T][ ] first" + System.lineSeparator()
                + "2.[T][ ] second" + System.lineSeparator();
        String expected = "Here are the tasks in your list:" + System.lineSeparator()
                + numberedTasks
                + "Here are the matching tasks in your list:" + System.lineSeparator()
                + numberedTasks;
        assertEquals(expected, output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showSortedTasks_numberedTasks_preservesExactOutput() {
        ui.showSortedTasks(List.of(new Todo("first"), new Todo("second")));

        String expected = "Here are your tasks sorted chronologically:"
                + System.lineSeparator()
                + "1.[T][ ] first" + System.lineSeparator()
                + "2.[T][ ] second" + System.lineSeparator();
        assertEquals(expected, output.toString(StandardCharsets.UTF_8));
    }
}
