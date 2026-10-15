package ff15;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import ff15.contact.Contact;
import ff15.task.Deadline;
import ff15.task.Event;
import ff15.task.Task;
import ff15.task.TaskTime;
import ff15.task.Todo;

/**
 * Tests everything {@link Ui} prints and reads. Two things are checked for each
 * message: what the console sees, captured from standard output, and what the
 * window sees, taken from the transcript -- the same words, framed differently.
 */
public class UiTest {

    private PrintStream realOut;
    private InputStream realIn;
    private ByteArrayOutputStream printed;
    private Ui ui;

    @BeforeEach
    public void captureTheConsole() {
        realOut = System.out;
        realIn = System.in;
        printed = new ByteArrayOutputStream();
        System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
        ui = new Ui();
    }

    @AfterEach
    public void restoreTheConsole() {
        System.setOut(realOut);
        System.setIn(realIn);
    }

    private String console() {
        return printed.toString(StandardCharsets.UTF_8);
    }

    // --- the transcript, which the window reads ----------------------------------

    @Test
    public void showMessage_severalLines_printsEachIndentedAndRecordsEach() {
        ui.showMessage("one", "two");

        assertTrue(console().contains("     one" + System.lineSeparator()), console());
        assertTrue(console().contains("     two" + System.lineSeparator()), console());
        assertEquals("one" + System.lineSeparator() + "two", ui.drainTranscript());
    }

    @Test
    public void drainTranscript_afterDraining_isEmptyUntilSomethingElseIsShown() {
        ui.showMessage("first");
        ui.drainTranscript();
        assertEquals("", ui.drainTranscript());
        ui.showMessage("second");
        assertEquals("second", ui.drainTranscript());
    }

    @Test
    public void drainTranscript_keepsTheWordsButNotTheConsoleFraming() {
        ui.startBlock();
        ui.showMessage("hello");
        ui.endBlock();

        assertTrue(console().contains("____"), "the console gets divider lines");
        assertEquals("hello", ui.drainTranscript(), "the transcript does not");
    }

    @Test
    public void showMessage_noArguments_trips() {
        assertThrows(AssertionError.class, () -> ui.showMessage());
    }

    // --- one method per kind of reply ---------------------------------------------

    @Test
    public void showError_prefixesTheLine() {
        ui.showError("that went wrong");
        assertEquals("No. GOD. NO. that went wrong", ui.drainTranscript());
    }

    @Test
    public void showWelcome_printsTheBannerToTheConsoleButNotTheTranscript() {
        ui.showWelcome();

        assertTrue(console().contains("|  ___|"), "the ASCII banner goes to the console");
        String transcript = ui.drainTranscript();
        assertFalse(transcript.contains("|  ___|"), transcript);
        assertTrue(transcript.contains("Assistant TO the Regional Manager"), transcript);
    }

    @Test
    public void showFarewell_saysGoodbye() {
        ui.showFarewell();
        assertEquals("See ya tomorrow, boss.", ui.drainTranscript());
    }

    @Test
    public void showLoadingError_reportsTheReasonAndTheEmptyStart() {
        ui.showLoadingError("disk on fire");
        String shown = ui.drainTranscript();
        assertTrue(shown.startsWith("No. GOD. NO. Couldn't read your saved tasks: disk on fire"), shown);
        assertTrue(shown.contains("empty list"), shown);
    }

    @Test
    public void showContactLoadingError_reportsTheReasonAndTheEmptyStart() {
        ui.showContactLoadingError("disk on fire");
        String shown = ui.drainTranscript();
        assertTrue(shown.contains("Couldn't read your saved contacts: disk on fire"), shown);
        assertTrue(shown.contains("empty contact list"), shown);
    }

    @Test
    public void showSkippedLines_nothingSkipped_printsNothing() {
        ui.showSkippedLines("data/ff15.txt", List.of());
        assertEquals("", ui.drainTranscript());
        assertEquals("", console());
    }

    @Test
    public void showSkippedLines_oneLine_usesTheSingular() {
        ui.showSkippedLines("data/ff15.txt", List.of("line 2: broken"));
        String shown = ui.drainTranscript();
        assertTrue(shown.contains("1 line in data/ff15.txt, so I skipped it:"), shown);
        assertTrue(shown.contains("  line 2: broken"), shown);
        assertTrue(shown.contains("The rest loaded fine"), shown);
    }

    @Test
    public void showSkippedLines_severalLines_usesThePluralAndListsEach() {
        ui.showSkippedLines("data/ff15.txt", List.of("line 2: broken", "line 5: also broken"));
        String shown = ui.drainTranscript();
        assertTrue(shown.contains("2 lines in data/ff15.txt, so I skipped them:"), shown);
        assertTrue(shown.contains("line 2: broken") && shown.contains("line 5: also broken"), shown);
    }

    @Test
    public void showTaskList_numbersFromOneUnderTheHeader() {
        ui.showTaskList("Header:", List.of(new Todo("a"), new Todo("b")));
        String[] lines = ui.drainTranscript().split(System.lineSeparator());
        assertEquals("Header:", lines[0]);
        assertEquals("1.[T][ ] a", lines[1]);
        assertEquals("2.[T][ ] b", lines[2]);
    }

    @Test
    public void showTaskList_empty_printsOnlyTheHeader() {
        ui.showTaskList("Header:", List.of());
        assertEquals("Header:", ui.drainTranscript());
    }

    @Test
    public void showTask_indentsTheTaskUnderTheHeader() {
        ui.showTask("Header:", new Todo("a"));
        assertEquals("Header:" + System.lineSeparator() + "  [T][ ] a", ui.drainTranscript());
    }

    @Test
    public void showTaskAdded_todo_saysTheThingHeAlwaysSays() {
        ui.showTaskAdded(new Todo("a"), 1);
        String shown = ui.drainTranscript();
        assertTrue(shown.startsWith("That's what she said."), shown);
        assertTrue(shown.contains("Now you have 1 tasks"), shown);
    }

    @Test
    public void showTaskAdded_event_asksIfHeIsInvited() throws FF15Exception {
        Task event = new Event("party", TaskTime.parse("2019-12-05"), TaskTime.parse("2019-12-06"));
        ui.showTaskAdded(event, 2);
        assertTrue(ui.drainTranscript().startsWith("Am I invited?"));
    }

    @Test
    public void showTaskAdded_deadline_isPlainAcknowledgement() throws FF15Exception {
        ui.showTaskAdded(new Deadline("report", TaskTime.parse("2019-12-05")), 3);
        assertTrue(ui.drainTranscript().startsWith("Got it. Added:"));
    }

    @Test
    public void showTaskRemoved_reportsTheTaskAndTheCountLeft() {
        ui.showTaskRemoved(new Todo("a"), 0);
        String shown = ui.drainTranscript();
        assertTrue(shown.contains("Removed:"), shown);
        assertTrue(shown.contains("[T][ ] a"), shown);
        assertTrue(shown.contains("Now you have 0 tasks"), shown);
    }

    @Test
    public void showContactList_numbersFromOneUnderTheHeader() {
        ui.showContactList("Header:", List.of(new Contact("John", "", ""), new Contact("Mary", "1", "")));
        String[] lines = ui.drainTranscript().split(System.lineSeparator());
        assertEquals("Header:", lines[0]);
        assertEquals("1.John", lines[1]);
        assertEquals("2.Mary (phone: 1)", lines[2]);
    }

    @Test
    public void showContactAdded_reportsTheContactAndTheCount() {
        ui.showContactAdded(new Contact("John", "", ""), 1);
        String shown = ui.drainTranscript();
        assertTrue(shown.contains("Added:") && shown.contains("  John"), shown);
        assertTrue(shown.contains("1 contacts"), shown);
    }

    @Test
    public void showContactRemoved_reportsTheContactAndTheCount() {
        ui.showContactRemoved(new Contact("John", "", ""), 0);
        String shown = ui.drainTranscript();
        assertTrue(shown.contains("Removed:") && shown.contains("  John"), shown);
        assertTrue(shown.contains("0 contacts"), shown);
    }

    // --- framing and input, which only the console has ------------------------------

    @Test
    public void startBlock_endBlock_frameWithDividersAndABlankLine() {
        ui.startBlock();
        ui.showMessage("x");
        ui.endBlock();

        String[] lines = console().split("\\r?\\n", -1);
        assertTrue(lines[0].contains("____"), lines[0]);
        assertEquals("     x", lines[1]);
        assertTrue(lines[2].contains("____"), lines[2]);
        assertEquals("", lines[3], "a blank line separates blocks");
    }

    @Test
    public void endFinalBlock_closesWithoutTheBlankLine() {
        ui.endFinalBlock();
        String out = console();
        assertTrue(out.contains("____"), out);
        assertFalse(out.endsWith(System.lineSeparator() + System.lineSeparator()), "no trailing blank line");
    }

    @Test
    public void readCommand_returnsTheNextLineTyped() {
        System.setIn(new ByteArrayInputStream("todo a\nlist\n".getBytes(StandardCharsets.UTF_8)));
        Ui reading = new Ui();
        assertEquals("todo a", reading.readCommand());
        assertEquals("list", reading.readCommand());
    }
}
