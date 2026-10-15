package rocky.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import rocky.TaskList;
import rocky.parser.Parser;
import rocky.task.Todo;

/** Tests console input handling and all user-facing output provided by {@link Ui}. */
class UiTest {
    private static final String DIVIDER = "____________________________________________________________";
    private InputStream originalInput;
    private PrintStream originalOutput;
    private ByteArrayOutputStream output;

    /** Redirects standard streams so each test can inspect console behavior. */
    @BeforeEach
    void setUp() {
        originalInput = System.in;
        originalOutput = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    /** Restores standard streams after each test. */
    @AfterEach
    void tearDown() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    /** Verifies that the UI reads each available command from standard input. */
    @Test
    void hasNextLineAndReadLine_availableInput_returnsCommandsInOrder() {
        Ui ui = createUi("list\nbye\n");

        assertTrue(ui.hasNextLine());
        assertEquals("list", ui.readLine());
        assertTrue(ui.hasNextLine());
        assertEquals("bye", ui.readLine());
        assertFalse(ui.hasNextLine());
    }

    /** Verifies that welcome and goodbye messages retain the expected console framing. */
    @Test
    void showWelcomeAndBye_newUi_printsGreetingBannerAndFarewell() {
        Ui ui = createUi("");

        ui.showWelcome();
        ui.showBye();

        String printed = capturedOutput();
        assertTrue(printed.startsWith(DIVIDER));
        assertTrue(printed.contains("|_| \\_\\___/ \\___|_|\\_\\__, |"));
        assertTrue(printed.contains("Hello! I Rocky."));
        assertTrue(printed.contains("Amaze, what a special human being! What rocky do for you?"));
        assertTrue(printed.endsWith("Bye. We meet again soon!" + System.lineSeparator() + DIVIDER
                + System.lineSeparator()));
    }

    /** Verifies that task-list, sorted-list, and matching-list output displays tasks correctly. */
    @Test
    void showTaskCollections_emptyAndPopulatedLists_printsExpectedMessages() {
        Ui ui = createUi("");
        TaskList emptyTasks = new TaskList();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        TaskList tasks = new TaskList(List.of(todo));

        ui.showTasks(emptyTasks);
        ui.showSortedTasks(tasks);
        ui.showMatchingTasks(List.of(todo));
        ui.showMatchingTasks(List.of());

        assertEquals(lines(
                "Rocky remember you have these tasks",
                DIVIDER,
                "Rocky don't see anything!",
                DIVIDER,
                "Rocky help you sort the task from earliest to latest!",
                "Rocky remember you have these tasks",
                DIVIDER,
                "1. [T][X] read book",
                DIVIDER,
                DIVIDER,
                "Here are the matching tasks in your list:",
                "1. [T][X] read book",
                DIVIDER,
                DIVIDER,
                "Here are the matching tasks in your list:",
                "Rocky don't see any matching tasks!",
                DIVIDER), capturedOutput());
    }

    /** Verifies that task status and deletion feedback include the supplied task. */
    @Test
    void showTaskFeedback_addStatusAndDelete_printsExpectedMessages() {
        Ui ui = createUi("");
        Todo todo = new Todo("read book");

        ui.showTaskAdded(todo, 1);
        todo.markAsDone();
        ui.showTaskStatus(todo, true);
        todo.markAsNotDone();
        ui.showTaskStatus(todo, false);
        ui.showTaskDeleted(todo);

        String printed = capturedOutput();
        assertTrue(printed.contains("Amaze! Rocky add this to task...:"));
        assertTrue(printed.contains("Rocky see 1 tasks in the list."));
        assertTrue(printed.contains("Nice! Rocky marked this task as done:"));
        assertTrue(printed.contains("Oh No! Rocky marked this task as not done yet:"));
        assertTrue(printed.contains("Rocky will remove that annoying task for you!:"));
        assertEquals(8, printed.split(DIVIDER, -1).length - 1);
    }

    /** Verifies that task-number and parser errors use their respective display formats. */
    @Test
    void showErrors_invalidNumbersMissingTasksAndParserErrors_printsExpectedMessages() {
        Ui ui = createUi("");
        Parser parser = new Parser();

        ui.showInvalidTaskNumber("mark");
        ui.showTaskNotFound();
        ui.showError(parser.parse("find"));
        ui.showError(parser.parse("todo"));

        assertEquals(lines(
                "Please provide a task number, for example: mark 2",
                "Rocky cannot find that task number.",
                "Use: find KEYWORD",
                DIVIDER,
                "Curious? Rocky don't see description for the task...",
                DIVIDER), capturedOutput());
    }

    /** Creates a UI whose scanner reads the supplied test input. */
    private Ui createUi(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        return new Ui();
    }

    /** Returns the captured standard-output text. */
    private String capturedOutput() {
        return output.toString(StandardCharsets.UTF_8);
    }

    /** Joins output lines and includes the newline printed after the final line. */
    private String lines(String... lines) {
        return String.join(System.lineSeparator(), lines) + System.lineSeparator();
    }
}
