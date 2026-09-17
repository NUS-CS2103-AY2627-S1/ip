package megatron.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import megatron.exception.EmptyCommandException;
import megatron.exception.EmptyDescriptionException;
import megatron.exception.InvalidTaskFormatException;
import megatron.exception.StorageException;
import megatron.exception.UnknownCommandException;
import megatron.task.TaskList;
import megatron.task.TaskMatch;
import megatron.task.Todo;

/**
 * Tests input handling and console output by {@link Ui}.
 */
class UiTest {
    @Test
    void constructor_nullScanner_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Ui(null, new PrintStream(new ByteArrayOutputStream())));
    }

    @Test
    void constructor_nullOutput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Ui(new Scanner(""), null));
    }

    @Test
    void input_commands_areReadInOrder() {
        Ui ui = new Ui(new Scanner("todo study\nbye\n"), new PrintStream(new ByteArrayOutputStream()));

        assertTrue(ui.hasNextCommand());
        assertEquals("todo study", ui.readCommand());
        assertTrue(ui.hasNextCommand());
        assertEquals("bye", ui.readCommand());
        assertFalse(ui.hasNextCommand());
    }

    @Test
    void close_scanner_preventsFurtherInput() {
        Ui ui = new Ui(new Scanner("bye\n"), new PrintStream(new ByteArrayOutputStream()));

        ui.close();

        assertThrows(IllegalStateException.class, ui::hasNextCommand);
    }

    @Test
    void showWelcome_printsBannerAndPrompt() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showWelcome();

        String welcome = output.toString(StandardCharsets.UTF_8);
        assertTrue(welcome.startsWith("____________________________________________________________"));
        assertTrue(welcome.contains("Rawr! Megatron Griffin reporting for duty!"));
        assertTrue(welcome.contains("I was built to conquer the universe, but task management will do."));
        assertTrue(welcome.contains("What command shall I execute?"));
        assertTrue(welcome.endsWith("____________________________________________________________"
                + System.lineSeparator()));
    }

    @Test
    void showDivider_printsDivider() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showDivider();

        assertEquals("____________________________________________________________"
                + System.lineSeparator(), output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showGoodbye_printsExitMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showGoodbye();

        assertEquals("Retreat accepted. Try not to create more tasks while I'm gone!"
                + System.lineSeparator()
                + "____________________________________________________________" + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showTasks_emptyList_printsEmptyStateMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showTasks(new TaskList());

        assertEquals("Your task empire is empty. Add a task before it gets awkward."
                + System.lineSeparator(), output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showTasks_populatedList_printsNumberedTasks() {
        Todo firstTask = new Todo("first");
        Todo secondTask = new Todo("second");
        secondTask.markAsDone();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showTasks(new TaskList(List.of(firstTask, secondTask)));

        assertEquals("Behold! The current state of your task empire:" + System.lineSeparator()
                + "1.[T][ ] first" + System.lineSeparator()
                + "2.[T][X] second" + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showMatchingTasks_populatedList_printsHeadingAndOriginalTaskNumbers() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showMatchingTasks(List.of(new TaskMatch(2, new Todo("read book"))));

        assertEquals("Target acquired! Here are the matching tasks:" + System.lineSeparator()
                + "2.[T][ ] read book" + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showMatchingTasks_emptyList_printsNoMatchMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showMatchingTasks(List.of());

        assertEquals("No matching tasks detected. The search has conquered nothing."
                + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showDatetimeInformation_printsSupportedFormats() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showDatetimeInformation();

        assertEquals(expectedDatetimeInformation(), output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showTaskAdded_printsTaskAndCount() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showTaskAdded(new Todo("study"), 3);

        assertEquals("Command accepted! This task has joined my army:" + System.lineSeparator()
                + "[T][ ] study" + System.lineSeparator()
                + "My army now contains 3 tasks." + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showTaskMarked_doneTask_printsDoneMessage() {
        Todo task = new Todo("study");
        task.markAsDone();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showTaskMarked(task, true);

        assertEquals("Victory! This task has fallen before my mighty intellect:"
                + System.lineSeparator()
                + "[T][X] study" + System.lineSeparator(), output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showTaskMarked_notDoneTask_printsNotDoneMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showTaskMarked(new Todo("study"), false);

        assertEquals("Rebellion successful. This task escaped completion:" + System.lineSeparator()
                + "[T][ ] study" + System.lineSeparator(), output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showTaskDeleted_printsTaskAndCount() {
        Todo task = new Todo("study");
        task.markAsDone();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showTaskDeleted(task, 2);

        assertEquals("Target destroyed! This task has been removed:" + System.lineSeparator()
                + "[T][X] study" + System.lineSeparator()
                + "My army now contains 2 tasks." + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showError_printsExceptionMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showError(new EmptyCommandException());

        assertEquals("I need more details. My mind-reading module is still under construction."
                + " Please enter a command." + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showError_unknownCommand_usesMasterPlanMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showError(new UnknownCommandException(List.of("list", "bye")));

        assertEquals("That command is not part of my master plan. Try again."
                + System.lineSeparator(), output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showError_missingDetails_keepsFormatHint() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showError(new InvalidTaskFormatException("todo <description>."));

        assertEquals("I need more details. My mind-reading module is still under construction."
                + " Use: todo <description>." + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showError_emptyDescription_usesMissingDetailsMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showError(new EmptyDescriptionException());

        assertEquals("I need more details. My mind-reading module is still under construction."
                + " A todo description cannot be empty." + System.lineSeparator(),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    void showError_storageFailure_usesOopsMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        ui.showError(new StorageException(new IllegalStateException("storage failure")));

        assertEquals("OOPS! Megatron says: Could not save tasks. Check that the data file is writable."
                + System.lineSeparator(), output.toString(StandardCharsets.UTF_8));
    }

    private static Ui createUi(ByteArrayOutputStream output) {
        return new Ui(new Scanner(""), new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    private static String expectedDatetimeInformation() {
        return String.join(System.lineSeparator(), List.of(
                "Initiating temporal intelligence. Supported formats:",
                "Dates with a year: yyyy-MM-dd, d/M/yyyy",
                "MMM d yyyy, MMMM d yyyy",
                "d MMM yyyy, d MMMM yyyy",
                "Dates without a year: MMM d, MMMM d",
                "d MMM, d MMMM (current year is used)",
                "Times: HHmm, H:mm, h[am|pm], h:mm[am|pm]",
                "Examples: 2145, 21:45, 9pm, 9:45pm",
                "Weekdays: mon/tue/wed/thu/fri/sat/sun",
                "Full names are also accepted, for example monday 6pm.",
                "Missing times default to 0000 (midnight).",
                "A weekday resolves to its next available occurrence.",
                "A time-only event end uses the event start date.",
                "Example: event Exam /from 6 Jul 26 1200 /to 1400",
                "The above sets an event occuring from 6 Jul 26 1200hrs to 6 Jul 26 1400hrs",
                "Output format: dd MMM uu, HHmm'hrs' (example: 24 Aug 26, 2145hrs)"))
                + System.lineSeparator();
    }
}
