package echo.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import echo.task.Task;
import echo.task.TodoTask;

/** Tests user interface message formatting and input reading in {@link Ui}. */
public class UiTest {
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputCapture = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputCapture));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    @Test
    public void readCommand_trimsWhitespacesAndHandlesEof() {
        String input = "   todo read book   \n  \n";
        Ui ui = new Ui(new Scanner(input));

        assertEquals("todo read book", ui.readCommand());
        assertEquals("", ui.readCommand());
        assertNull(ui.readCommand());
        ui.close();
    }

    @Test
    public void getWelcomeMessage_returnsStandardGreeting() {
        Ui ui = new Ui(new Scanner(""));
        String welcome = ui.getWelcomeMessage();

        assertTrue(welcome.contains("SECURE UPLINK ESTABLISHED"));
        assertTrue(welcome.contains("Online and listening"));
    }

    @Test
    public void showWelcome_printsBannerAndReturnsMessage() {
        Ui ui = new Ui(new Scanner(""));
        String message = ui.showWelcome();

        assertTrue(message.contains("Online and listening"));
        String printed = outputCapture.toString();
        assertTrue(printed.contains("Everyday Conversational & Helpful Operator"));
    }

    @Test
    public void showHelp_returnsAndPrintsHelpManual() {
        Ui ui = new Ui(new Scanner(""));
        String help = ui.showHelp();

        assertTrue(help.contains("TACTICAL PROTOCOL DIRECTIVES"));
        assertTrue(help.contains("todo <description>"));
        assertTrue(help.contains("deadline <description>"));
        assertTrue(help.contains("event <description>"));
    }

    @Test
    public void showError_formatsErrorMessage() {
        Ui ui = new Ui(new Scanner(""));
        String error = ui.showError("Target directive not found.");

        assertEquals("Target directive not found.", error);
        assertTrue(outputCapture.toString().contains("Target directive not found."));
    }

    @Test
    public void showLoadingError_returnsStandardStorageFaultMessage() {
        Ui ui = new Ui(new Scanner(""));
        String error = ui.showLoadingError();

        assertTrue(error.contains("STORAGE FAULT DETECTED"));
    }

    @Test
    public void showTaskList_emptyList_returnsEmptyRadarMessage() {
        Ui ui = new Ui(new Scanner(""));
        String message = ui.showTaskList(List.of());

        assertTrue(message.contains("Tactical radar is clear"));
    }

    @Test
    public void showTaskList_populatedList_returnsNumberedManifest() {
        Ui ui = new Ui(new Scanner(""));
        Task first = new TodoTask("first");
        Task second = new TodoTask("second");

        String message = ui.showTaskList(List.of(first, second));

        assertTrue(message.contains("1: [To-Do][ ] first"));
        assertTrue(message.contains("2: [To-Do][ ] second"));
    }

    @Test
    public void showFindResults_emptyList_returnsNotFoundMessage() {
        Ui ui = new Ui(new Scanner(""));
        String message = ui.showFindResults("books", List.of());

        assertTrue(message.contains("No directives found matching keyword: 'books'"));
    }

    @Test
    public void showFindResults_populatedList_returnsMatchingDirectives() {
        Ui ui = new Ui(new Scanner(""));
        Task match = new TodoTask("read books");
        String message = ui.showFindResults("books", List.of(match));

        assertTrue(message.contains("MATCHING DIRECTIVES"));
        assertTrue(message.contains("1: [To-Do][ ] read books"));
    }

    @Test
    public void showSorted_emptyList_returnsEmptyMessage() {
        Ui ui = new Ui(new Scanner(""));
        String message = ui.showSorted(List.of());

        assertTrue(message.contains("Directive manifest is empty! Nothing to sort."));
    }

    @Test
    public void showSorted_populatedList_returnsOrderedDirectives() {
        Ui ui = new Ui(new Scanner(""));
        Task first = new TodoTask("apple");
        Task second = new TodoTask("banana");
        String message = ui.showSorted(List.of(first, second));

        assertTrue(message.contains("TELEMETRY REORDERED"));
        assertTrue(message.contains("1: [To-Do][ ] apple"));
        assertTrue(message.contains("2: [To-Do][ ] banana"));
    }

    @Test
    public void showFarewell_returnsFlavouredMessage() {
        Ui ui = new Ui(new Scanner(""));
        String farewell = ui.showFarewell();

        assertNotNull(farewell);
        assertFalse(farewell.isBlank());
    }

    @Test
    public void showMutationMessages_variousOperations_returnExpectedFormats() {
        Ui ui = new Ui(new Scanner(""));
        Task task = new TodoTask("read manual");

        String added = ui.showAdded(task, 5);
        assertTrue(added.contains("[DIRECTIVE LOGGED]"));
        assertTrue(added.contains("Active directives in backlog: 5"));

        String marked = ui.showMarked(task);
        assertTrue(marked.contains("[OBJECTIVE NEUTRALIZED]"));

        String unmarked = ui.showUnmarked(task);
        assertTrue(unmarked.contains("[DIRECTIVE REOPENED]"));

        String deleted = ui.showDeleted(2, task, 4);
        assertTrue(deleted.contains("[PURGE COMPLETE]"));
        assertTrue(deleted.contains("Purged directive #2"));
        assertTrue(deleted.contains("Remaining active directives: 4"));
    }

    @Test
    public void wrapResponse_longMessageWithMultiLines_wrapsLinesProperly() {
        Ui ui = new Ui(new Scanner(""));
        String veryLongLine = "word ".repeat(30);
        String multiLine = "Line 1\n\n" + veryLongLine + "\nLine 3";

        String result = ui.showError(multiLine);
        assertEquals(multiLine, result);
        assertTrue(outputCapture.toString().contains("Line 1"));
    }
}
