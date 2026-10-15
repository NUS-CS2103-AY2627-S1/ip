package pulbot.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pulbot.task.Deadline;
import pulbot.task.Event;
import pulbot.task.Task;
import pulbot.task.TaskList;
import pulbot.task.Todo;

/** Tests console input and user-facing output. */
public class UiTest {
    private InputStream originalIn;
    private PrintStream originalOut;
    private ByteArrayOutputStream output;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        originalIn = System.in;
        originalOut = System.out;
        System.setIn(new ByteArrayInputStream("first line\nsecond line\n"
                .getBytes(StandardCharsets.UTF_8)));
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        ui = new Ui();
    }

    @AfterEach
    public void tearDown() {
        ui.close();
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    public void inputMethods_readAvailableLines() {
        assertTrue(ui.hasNextLine());
        assertEquals("first line", ui.nextLine());
        assertTrue(ui.hasNextLine());
        assertEquals("second line", ui.nextLine());
        assertFalse(ui.hasNextLine());
    }

    @Test
    public void showWelcome_displaysInstructions() {
        ui.showWelcome();

        String displayed = displayedText();
        assertTrue(displayed.contains("Hello! I'm Pulbot."));
        assertTrue(displayed.contains("todo <description>"));
        assertTrue(displayed.contains("deadline <description> /by <when>"));
        assertTrue(displayed.contains("event <description> /from <start> /to <end>"));
        assertTrue(displayed.contains("bye"));
    }

    @Test
    public void statusMessages_displayTaskAndCounts() {
        Task task = new Todo("read book");

        ui.showSeparator();
        ui.showBye();
        ui.showError("bad command");
        ui.showAddedTask(task, 1);
        ui.showTaskCount(2);
        ui.showMarked(task);
        ui.showUnmarked(task);
        ui.showDeleted(task, 0);

        String displayed = displayedText();
        assertTrue(displayed.contains("_".repeat(80)));
        assertTrue(displayed.contains("Bye."));
        assertTrue(displayed.contains("bad command"));
        assertTrue(displayed.contains("I have added this task"));
        assertTrue(displayed.contains("1 task in the list"));
        assertTrue(displayed.contains("2 tasks in the list"));
        assertTrue(displayed.contains("marked this task as done"));
        assertTrue(displayed.contains("unmarked this task"));
        assertTrue(displayed.contains("I have deleted this task"));
        assertTrue(displayed.contains("0 tasks in the list"));
    }

    @Test
    public void showList_emptyAndPopulated_displaysExpectedContent() {
        ui.showList(new TaskList());
        assertTrue(displayedText().contains("Your list is empty"));

        output.reset();
        ui.showList(new TaskList(List.of(new Todo("first"), new Todo("second"))));
        String displayed = displayedText();
        assertTrue(displayed.contains("1.[T][ ] first"));
        assertTrue(displayed.contains("2.[T][ ] second"));
    }

    @Test
    public void showMatchingTasks_matchesCaseInsensitivelyAndReportsNoMatch() {
        TaskList tasks = new TaskList(List.of(
                new Todo("Read book"),
                new Todo("buy milk"),
                new Todo("book flight")));

        ui.showMatchingTasks(tasks, "BOOK");
        String displayed = displayedText();
        assertTrue(displayed.contains("1.[T][ ] Read book"));
        assertTrue(displayed.contains("3.[T][ ] book flight"));
        assertFalse(displayed.contains("buy milk"));

        output.reset();
        ui.showMatchingTasks(tasks, "homework");
        assertTrue(displayedText().contains("No matching tasks found"));
    }

    @Test
    public void showTasksOnDate_displaysDeadlinesAndSpanningEventsOnly() {
        TaskList tasks = new TaskList(List.of(
                new Todo("undated task"),
                new Deadline("submit report", "2/12/2019 1800"),
                new Event("camp", "1/12/2019 0900", "3/12/2019 1700")));

        ui.showTasksOnDate(tasks, LocalDate.of(2019, 12, 2));
        String displayed = displayedText();
        assertTrue(displayed.contains("Tasks on Dec 02 2019"));
        assertTrue(displayed.contains("submit report"));
        assertTrue(displayed.contains("camp"));
        assertFalse(displayed.contains("undated task"));

        output.reset();
        ui.showTasksOnDate(tasks, LocalDate.of(2019, 12, 4));
        assertTrue(displayedText().contains("No deadlines or events on Dec 04 2019"));
    }

    private String displayedText() {
        return output.toString(StandardCharsets.UTF_8);
    }
}
