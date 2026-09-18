package student.project.bob.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import student.project.bob.exception.BobException;
import student.project.bob.model.Task;
import student.project.bob.model.Todo;

/**
 * Tests console input and output behavior.
 */
public class UiTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOutput;
    private Ui ui;

    /**
     * Redirects standard input and output before each UI test.
     */
    @BeforeEach
    public void setUp() {
        originalOutput = System.out;
        System.setIn(new ByteArrayInputStream(new byte[0]));
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        ui = new Ui();
    }

    /**
     * Restores the process-wide output stream after each UI test.
     */
    @AfterEach
    public void tearDown() {
        System.setOut(originalOutput);
    }

    /**
     * Verifies that commands are trimmed and input availability is detected correctly.
     */
    @Test
    public void inputMethods_commandsTrimInputAndDetectEnd() {
        System.setIn(new ByteArrayInputStream("  todo read book  \nbye\n".getBytes(StandardCharsets.UTF_8)));
        ui = new Ui();

        assertTrue(ui.hasNextCommand());
        assertEquals("todo read book", ui.readCommand());
        assertTrue(ui.hasNextCommand());
        assertEquals("bye", ui.readCommand());
        assertFalse(ui.hasNextCommand());
    }

    /**
     * Verifies the welcome, goodbye, and separator messages.
     */
    @Test
    public void showWelcomeGoodbyeAndSeparator_printExpectedMessages() {
        ui.showWelcome();
        ui.showGoodbye();
        ui.showSeparator();

        assertEquals(
                normalize("____________________________________________________________\n"
                        + " ____        _     \n"
                        + "| __ )  ___ | |__  \n"
                        + "|  _ \\ / _ \\| '_ \\ \n"
                        + "| |_) | (_) | |_) |\n"
                        + "|____/ \\___/|_.__/ \n"
                        + "\n"
                        + "Hello! I'm Bob.\n"
                        + "What can I do for you?\n"
                        + "____________________________________________________________\n"
                        + "Bye. Hope to see you again soon!\n"
                        + "____________________________________________________________\n"
                        + "____________________________________________________________\n"),
                outputText());
    }

    /**
     * Verifies that task lists display an empty or populated list with one-based numbering.
     */
    @Test
    public void showTaskList_emptyAndPopulatedLists_printExpectedLists() {
        ui.showTaskList(List.of());
        assertEquals(
                normalize("Here are the tasks in your list:\n\n"
                        + "____________________________________________________________\n"),
                outputText());

        output.reset();
        ui.showTaskList(List.of(new Todo("first"), new Todo("second")));

        assertEquals(
                normalize("Here are the tasks in your list:\n\n"
                        + "1.[T][ ] first\n"
                        + "2.[T][ ] second\n"
                        + "____________________________________________________________\n"),
                outputText());
    }

    /**
     * Verifies upcoming-task output includes the requested day count and task numbering.
     */
    @Test
    public void showUpcomingTasks_tasks_printExpectedDateRange() {
        ui.showUpcomingTasks(List.of(new Todo("read book")), 14);

        assertEquals(
                normalize("Here are the upcoming tasks in the next 14 days:\n\n"
                        + "1.[T][ ] read book\n"
                        + "____________________________________________________________\n"),
                outputText());
    }

    /**
     * Verifies date-specific task output formats the requested date.
     */
    @Test
    public void showTasksOnDate_tasks_printExpectedDate() {
        ui.showTasksOnDate(List.of(new Todo("read book")), LocalDate.of(2019, 10, 15));

        assertEquals(
                normalize("Here are the tasks on Tue, Oct 15 2019:\n\n"
                        + "1.[T][ ] read book\n"
                        + "____________________________________________________________\n"),
                outputText());
    }

    /**
     * Verifies overdue-task output displays the supplied tasks and separator.
     */
    @Test
    public void showOverdueTasks_tasks_printExpectedList() {
        ui.showOverdueTasks(List.of(new Todo("old task")));

        assertEquals(
                normalize("Here are your overdue tasks:\n\n"
                        + "1.[T][ ] old task\n"
                        + "____________________________________________________________\n"),
                outputText());
    }

    /**
     * Verifies matching-task output uses the same numbering and separator as other task queries.
     */
    @Test
    public void showMatchingTasks_tasks_printExpectedList() {
        ui.showMatchingTasks(List.of(new Todo("read book"), new Todo("return book")));

        assertEquals(
                normalize("Here are the matching tasks in your list:\n\n"
                        + "1.[T][ ] read book\n"
                        + "2.[T][ ] return book\n"
                        + "____________________________________________________________\n"),
                outputText());
    }

    /**
     * Verifies task confirmation messages for adding, marking, unmarking, and deleting.
     */
    @Test
    public void showTaskConfirmations_tasks_printExpectedMessages() {
        Task task = new Todo("read book");
        ui.showAddedTask(task, 1);
        ui.showMarkedTask(task);
        ui.showUnmarkedTask(task);
        ui.showDeletedTask(task, 0);

        assertEquals(
                normalize("Got it. I've added this task:\n\n"
                        + "[T][ ] read book\n"
                        + "Now you have 1 tasks in the list.\n"
                        + "____________________________________________________________\n"
                        + "Nice! I've marked this task as done:\n"
                        + "  [X] read book\n"
                        + "____________________________________________________________\n"
                        + "OK, I've marked this task as not done yet:\n"
                        + "  [ ] read book\n"
                        + "Noted. I've removed this task:\n"
                        + "    [T][ ] read book\n"
                        + "Now you have 0 tasks in the list.\n"
                        + "____________________________________________________________\n"),
                outputText());
    }

    /**
     * Verifies that undo output includes its message and the standard separator.
     */
    @Test
    public void showUndoResult_message_printsMessageAndSeparator() {
        ui.showUndoResult("Undid the last command:\n    [T][ ] read book");

        assertEquals(
                normalize("Undid the last command:\n"
                        + "    [T][ ] read book\n"
                        + "____________________________________________________________\n"),
                outputText());
    }

    /**
     * Verifies input and storage errors use the common error prefix and separator.
     */
    @Test
    public void showErrors_messages_printExpectedErrorOutput() {
        ui.showError(new BobException("invalid command"));
        ui.showStorageError("storage unavailable");

        assertEquals(
                normalize("Oops! invalid command\n"
                        + "____________________________________________________________\n"
                        + "Oops! storage unavailable\n"
                        + "____________________________________________________________\n"),
                outputText());
    }

    private String outputText() {
        return normalize(output.toString(StandardCharsets.UTF_8));
    }

    private String normalize(String text) {
        return text.replace("\r\n", "\n").replace('\r', '\n');
    }
}
