package duck.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import duck.task.Task;
import duck.task.Todo;

/**
 * Tests Duck's user-facing personality messages.
 */
public class UiTest {
    private ByteArrayOutputStream outputBuffer;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        this.outputBuffer = new ByteArrayOutputStream();
        this.ui = new Ui(
                new ByteArrayInputStream(new byte[0]),
                new PrintStream(this.outputBuffer, true, StandardCharsets.UTF_8));
    }

    @Test
    public void showTaskList_emptyList_describesClearPond() {
        this.ui.showTaskList(List.of());

        assertEquals("The pond is clear—there are no tasks yet.", getOutput());
    }

    @Test
    public void showTaskList_nonEmptyList_numbersTasksInOrder() {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");
        secondTask.markAsDone();

        this.ui.showTaskList(List.of(firstTask, secondTask));

        assertEquals("Here are the tasks in your pond:\n"
                + "1.[T][ ] first\n"
                + "2.[T][X] second", getOutput());
    }

    @Test
    public void showMatchingTasks_emptyList_describesNoMatches() {
        this.ui.showMatchingTasks(List.of(new Todo("non-match")), List.of());

        assertEquals("No matching tasks surfaced in the pond.", getOutput());
    }

    @Test
    public void showMatchingTasks_nonEmptyList_preservesFullListNumbers() {
        Task firstMatch = new Todo("first match");
        Task nonMatch = new Todo("non-match");
        Task secondMatch = new Todo("second match");
        this.ui.showMatchingTasks(
                List.of(firstMatch, nonMatch, secondMatch),
                List.of(firstMatch, secondMatch));

        assertEquals("Here are the matching tasks in your pond:\n"
                + "1.[T][ ] first match\n"
                + "3.[T][ ] second match", getOutput());
    }

    @Test
    public void showTaskMarked_completedTask_describesCompletion() {
        Task task = new Todo("read book");
        task.markAsDone();

        this.ui.showTaskMarked(task);

        assertEquals("✓ Nicely done! I've marked this task as complete:\n"
                + "  [T][X] read book", getOutput());
    }

    @Test
    public void showTaskUnmarked_activeTask_describesReactivation() {
        Task task = new Todo("read book");

        this.ui.showTaskUnmarked(task);

        assertEquals("✓ No problem—this task is active again:\n"
                + "  [T][ ] read book", getOutput());
    }

    @Test
    public void showTaskAdded_oneTask_usesSingularTaskCount() {
        Task task = new Todo("read book");

        this.ui.showTaskAdded(task, 1);

        assertEquals("✓ Got it—this task is now under my wing:\n"
                + "[T][ ] read book\n"
                + "You now have 1 task in your pond.", getOutput());
    }

    @Test
    public void showTaskAdded_multipleTasks_usesPluralTaskCount() {
        Task task = new Todo("read book");

        this.ui.showTaskAdded(task, 2);

        assertEquals("✓ Got it—this task is now under my wing:\n"
                + "[T][ ] read book\n"
                + "You now have 2 tasks in your pond.", getOutput());
    }

    @Test
    public void showTaskDeleted_multipleTasks_usesPluralTaskCount() {
        Task task = new Todo("read book");

        this.ui.showTaskDeleted(task, 2);

        assertEquals("✓ Removed! That task has left the pond:\n"
                + "  [T][ ] read book\n"
                + "You now have 2 tasks in your pond.", getOutput());
    }

    @Test
    public void showTaskDeleted_oneTask_usesSingularTaskCount() {
        Task task = new Todo("read book");

        this.ui.showTaskDeleted(task, 1);

        assertEquals("✓ Removed! That task has left the pond:\n"
                + "  [T][ ] read book\n"
                + "You now have 1 task in your pond.", getOutput());
    }

    @Test
    public void showTaskDeleted_noTasks_usesPluralTaskCount() {
        Task task = new Todo("read book");

        this.ui.showTaskDeleted(task, 0);

        assertEquals("✓ Removed! That task has left the pond:\n"
                + "  [T][ ] read book\n"
                + "You now have 0 tasks in your pond.", getOutput());
    }

    @Test
    public void showError_message_addsDuckErrorPrefix() {
        this.ui.showError("Something went wrong.");

        assertEquals("Quack? Something went wrong.", getOutput());
    }

    @Test
    public void showHelp_noArguments_displaysEveryCommand() {
        this.ui.showHelp();

        assertEquals("Here are the commands I can help with:\n"
                + "  help\n"
                + "  todo DESCRIPTION\n"
                + "  deadline DESCRIPTION /by DATE\n"
                + "  event DESCRIPTION /from START /to END\n"
                + "  list\n"
                + "  find KEYWORD\n"
                + "  mark NUMBER\n"
                + "  unmark NUMBER\n"
                + "  delete NUMBER\n"
                + "  bye", getOutput());
    }

    /** Returns captured output without its final platform line separator. */
    private String getOutput() {
        return this.outputBuffer.toString(StandardCharsets.UTF_8).stripTrailing();
    }
}
