package yapbot.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import yapbot.task.Deadline;
import yapbot.task.Event;
import yapbot.task.Priority;
import yapbot.task.Task;
import yapbot.task.Todo;

public class UiTest {

    private final ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    public void redirectSystemOut() {
        originalOut = System.out;
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void restoreSystemOut() {
        System.setOut(originalOut);
    }

    private String output() {
        return capturedOutput.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void readCommand_returnsTheLineFromInput() {
        Ui ui = new Ui(new ByteArrayInputStream("todo read book\n".getBytes(StandardCharsets.UTF_8)));

        String command = ui.readCommand();

        assertEquals("todo read book", command);
    }

    @Test
    public void showWelcome_printsGreeting() {
        new Ui().showWelcome();

        assertTrue(output().contains("Hey! I'm YapBot"));
        assertTrue(output().contains("What are we tackling today?"));
    }

    @Test
    public void getGreeting_matchesShowWelcomesGreetingWords() {
        Ui ui = new Ui();

        ui.showWelcome();

        assertTrue(output().contains(ui.getGreeting()));
    }

    @Test
    public void showGoodbye_printsGoodbyeMessage() {
        new Ui().showGoodbye();

        assertTrue(output().contains("Nice work today! See you soon!"));
    }

    @Test
    public void showMessage_printsGivenMessage() {
        new Ui().showMessage("custom message");

        assertTrue(output().contains("custom message"));
    }

    @Test
    public void showTaskAdded_printsTaskAndUpdatedCount() {
        Task task = new Todo("read book");

        new Ui().showTaskAdded(task, 3);

        assertTrue(output().contains("read book"));
        assertTrue(output().contains("Now you have 3 tasks in the list."));
    }

    @Test
    public void showTaskAdded_todo_usesAddedToListPhrase() {
        Task task = new Todo("read book");

        new Ui().showTaskAdded(task, 1);

        assertTrue(output().contains("Great, I've added read book to the list"));
    }

    @Test
    public void showTaskAdded_deadline_usesRemindPhraseWithFormattedDate() {
        Task task = new Deadline("return book", LocalDate.of(2019, 10, 15));

        new Ui().showTaskAdded(task, 1);

        assertTrue(output().contains("Remember to do return book by Oct 15 2019"));
    }

    @Test
    public void showTaskAdded_event_usesAddedToSchedulePhrase() {
        Task task = new Event("project meeting", "Mon 2pm", "4pm");

        new Ui().showTaskAdded(task, 1);

        assertTrue(output().contains("Noted! I've added project meeting to your schedule"));
    }

    @Test
    public void showTaskAdded_overdueDeadline_includesPassedWarning() {
        Task task = new Deadline("return book", LocalDate.now().minusDays(1));

        new Ui().showTaskAdded(task, 1);

        assertTrue(output().contains("(heads up, that date's already passed!)"));
    }

    @Test
    public void showTaskAdded_futureDeadline_noPassedWarning() {
        Task task = new Deadline("return book", LocalDate.now().plusDays(1));

        new Ui().showTaskAdded(task, 1);

        assertFalse(output().contains("heads up"));
    }

    @Test
    public void showTaskAdded_overdueEvent_includesOverWarning() {
        Task task = new Event("project meeting", "2019-10-15", LocalDate.now().minusDays(1).toString());

        new Ui().showTaskAdded(task, 1);

        assertTrue(output().contains("(heads up, that's already over!)"));
    }

    @Test
    public void showTaskMarked_printsTask() {
        Task task = new Todo("read book");
        task.markAsDone();

        new Ui().showTaskMarked(task);

        assertTrue(output().contains("[X]read book"));
    }

    @Test
    public void showPriorityChanged_printsTaskWithPriorityTag() {
        Task task = new Todo("read book");
        task.setPriority(Priority.HIGH);

        new Ui().showPriorityChanged(task);

        assertTrue(output().contains("[HIGH]read book"));
    }

    @Test
    public void showTaskUnmarked_printsTask() {
        Task task = new Todo("read book");

        new Ui().showTaskUnmarked(task);

        assertTrue(output().contains("[ ]read book"));
    }

    @Test
    public void showTaskDeleted_printsTaskAndRemainingCount() {
        Task task = new Todo("read book");

        new Ui().showTaskDeleted(task, 1);

        assertTrue(output().contains("read book"));
        assertTrue(output().contains("Now you have 1 tasks in the list."));
    }

    @Test
    public void showTaskDeleted_usesRemovedFromListPhrase() {
        Task task = new Todo("read book");

        new Ui().showTaskDeleted(task, 0);

        assertTrue(output().contains("I've removed read book from the list"));
    }

    @Test
    public void showTaskList_emptyList_printsHeaderOnlyAndNoItems() {
        new Ui().showTaskList(new Task[0], 0);

        assertTrue(output().contains("Here's everything on your list:"));
        assertFalse(output().contains("1."));
    }

    @Test
    public void showTaskList_multipleTasks_printsEachOneNumbered() {
        Task[] tasks = new Task[]{new Todo("read book"), new Todo("write code")};

        new Ui().showTaskList(tasks, 2);

        assertTrue(output().contains("1.[T][ ]read book"));
        assertTrue(output().contains("2.[T][ ]write code"));
    }

    @Test
    public void showMatchingTasks_emptyMatches_printsHeaderOnlyAndNoItems() {
        new Ui().showMatchingTasks(new Task[0]);

        assertTrue(output().contains("Here's what I found for you:"));
        assertFalse(output().contains("1."));
    }

    @Test
    public void showMatchingTasks_someMatches_printsEachOneNumbered() {
        Task[] matches = new Task[]{new Todo("read book"), new Todo("return book")};

        new Ui().showMatchingTasks(matches);

        assertTrue(output().contains("1.[T][ ]read book"));
        assertTrue(output().contains("2.[T][ ]return book"));
    }
}
