package bobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Tests console input handling and all UI message formats. */
public class UiTest {
    private final InputStream originalInput = System.in;

    /** Restores standard input after a command-reading test. */
    @AfterEach
    public void restoreStandardInput() {
        System.setIn(originalInput);
    }

    /** Verifies command input is trimmed and end of input returns null. */
    @Test
    public void readCommand_whitespaceAndEndOfInput_trimmedCommandThenNullReturned() {
        System.setIn(new ByteArrayInputStream("  todo read book  \n".getBytes(StandardCharsets.UTF_8)));
        Ui ui = new Ui(new PrintStream(new ByteArrayOutputStream()));

        assertEquals("todo read book", ui.readCommand());
        assertNull(ui.readCommand());
    }

    /** Verifies the public constructor can create a standard console UI. */
    @Test
    public void constructor_defaultInputAndOutput_uiCreated() {
        Ui ui = new Ui();

        assertEquals(Ui.class, ui.getClass());
    }

    /** Verifies the complete welcome banner and separators. */
    @Test
    public void showWelcome_defaultMessage_completeBannerReturned() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showWelcome();

        String expectedOutput = "____________________________________________________________" + System.lineSeparator()
                + " ____   ____  ____  ____  __   __\n"
                + "| __ ) / __ \\| __ )| __ ) \\ \\ / /\n"
                + "|  _ \\| |  | |  _ \\|  _ \\  \\ V /\n"
                + "| |_) | |__| | |_) | |_) |   | |\n"
                + "|____/ \\____/|____/|____/    |_|\n"
                + Ui.WELCOME_MESSAGE + System.lineSeparator()
                + "____________________________________________________________" + System.lineSeparator();
        assertEquals(expectedOutput, output.toString(StandardCharsets.UTF_8));
    }

    /** Verifies list and search output for populated and empty results. */
    @Test
    public void showLists_populatedAndEmptyResults_numberedAndNoMatchTextReturned() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);
        Task firstTask = new ToDo("read book");
        Task secondTask = new ToDo("write code");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        ui.showTaskList(tasks);
        ui.showMatchingTasks(List.of(secondTask));
        ui.showMatchingTasks(List.of());

        String expectedOutput = "Ta-da! Here's your task parade:" + System.lineSeparator()
                + "1.[T][ ] read book" + System.lineSeparator()
                + "2.[T][ ] write code" + System.lineSeparator()
                + "Detective Bobby found these matching tasks:" + System.lineSeparator()
                + "1.[T][ ] write code" + System.lineSeparator()
                + "Detective Bobby found these matching tasks:" + System.lineSeparator()
                + "I checked under the couch. No matching tasks found!" + System.lineSeparator();
        assertEquals(expectedOutput, output.toString(StandardCharsets.UTF_8));
    }

    /** Verifies every task mutation message and the generic error output. */
    @Test
    public void showMutationMessages_taskChanges_expectedTextReturned() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);
        Task task = new ToDo("read book");

        ui.showError("Example error");
        ui.showTaskAdded(task, 1);
        task.markAsDone();
        ui.showTaskMarkedDone(task);
        task.unmarkAsDone();
        ui.showTaskMarkedNotDone(task);
        task.setPriority(Priority.HIGH);
        ui.showTaskPriorityUpdated(task);
        ui.showTaskDeleted(task, 0);

        String expectedOutput = "Example error" + System.lineSeparator()
                + "Boop! I've tucked this task into the list:" + System.lineSeparator()
                + "[T][ ] read book" + System.lineSeparator()
                + "Bobby's task count is now 1." + System.lineSeparator()
                + "Woohoo! This task is officially done:" + System.lineSeparator()
                + "  [T][X] read book" + System.lineSeparator()
                + "Plot twist! This task is back in action:" + System.lineSeparator()
                + "  [T][ ] read book" + System.lineSeparator()
                + "Beep boop! I've updated this task's priority:" + System.lineSeparator()
                + "  [T][ ][P: HIGH] read book" + System.lineSeparator()
                + "Poof! This task has left the building:" + System.lineSeparator()
                + "[T][ ][P: HIGH] read book" + System.lineSeparator()
                + "Bobby's task count is now 0." + System.lineSeparator();
        assertEquals(expectedOutput, output.toString(StandardCharsets.UTF_8));
    }

    /** Verifies the farewell includes its trailing separator. */
    @Test
    public void showGoodbye_defaultMessage_messageAndSeparatorReturned() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showGoodbye();

        assertEquals("Toodles! Bobby is off to recharge the silly batteries." + System.lineSeparator()
                + "____________________________________________________________" + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    /** Creates a UI whose output can be inspected by a test. */
    private Ui createUi(ByteArrayOutputStream output) {
        return new Ui(new PrintStream(output, true, StandardCharsets.UTF_8));
    }
}
