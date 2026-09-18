package myriad.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import myriad.task.Task;
import myriad.task.TaskStats;
import myriad.task.ToDo;

/**
 * Tests for {@link Ui}, which since the GUI arrived has two jobs at once:
 * printing to the console exactly as it always did, and recording the same
 * messages so a GUI can read a whole reply back as one String. The console
 * transcript is pinned down by a scripted checker elsewhere, so what is
 * tested here is the recording — and that the divider framing the checker
 * relies on is still printed when, and only when, the Ui echoes.
 */
public class UiTest {

    private static final String DIVIDER = "____________________________________________________________";

    /** What a recorded reply puts between its lines, on every platform. */
    private static final String NEWLINE = "\n";

    /** What println puts at the end of a console line, which is platform-specific. */
    private static final String CONSOLE_NEWLINE = System.lineSeparator();

    /** A Ui that only records, as a GUI session uses. */
    private static Ui guiUi() {
        return new Ui(false);
    }

    /** Builds a list of not-done ToDos with the given descriptions. */
    private static List<Task> toDos(String... descriptions) {
        return Arrays.stream(descriptions).map(description -> (Task) new ToDo(description)).toList();
    }

    /**
     * Runs the given action with standard output captured, and returns
     * whatever it printed.
     */
    private static String capturePrinted(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(captured, true));
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return captured.toString();
    }

    // ---------------------------------------------------------------
    // The recorded reply
    // ---------------------------------------------------------------

    @Test
    public void getResponse_nothingShown_emptyStringReturned() {
        assertEquals("", guiUi().getResponse());
    }

    @Test
    public void getResponse_oneMessage_messageReturnedWithoutDividers() {
        Ui ui = guiUi();
        ui.showFarewell();
        assertEquals("Putting the brushes away. Come paint again soon!", ui.getResponse());
    }

    @Test
    public void getResponse_twoMessages_joinedByOneLineSeparator() {
        // A command that shows two things in a row must read as one reply,
        // not as two run together.
        Ui ui = guiUi();
        ui.showError("first");
        ui.showError("second");
        assertEquals("Smudge! first" + NEWLINE + "Smudge! second", ui.getResponse());
    }

    @Test
    public void getResponse_afterStartResponse_earlierMessagesDiscarded() {
        // Without this the GUI would repeat every earlier reply in every bubble.
        Ui ui = guiUi();
        ui.showError("stale");
        ui.startResponse();
        ui.showError("fresh");
        assertEquals("Smudge! fresh", ui.getResponse());
    }

    @Test
    public void getResponse_calledTwice_replyNotConsumed() {
        // Reading the reply must not clear it; only startResponse does that.
        Ui ui = guiUi();
        ui.showError("kept");
        assertEquals(ui.getResponse(), ui.getResponse());
    }

    // ---------------------------------------------------------------
    // What each message looks like
    // ---------------------------------------------------------------

    @Test
    public void showGreeting_recordedReply_bannerExcluded() {
        // The ASCII banner only lines up in a monospaced font, so it is
        // console-only; a GUI gets the words alone.
        Ui ui = guiUi();
        ui.showGreeting();
        assertEquals("Hello! I'm Myriad, your task painter." + NEWLINE
                + "What shall we paint on today's canvas?", ui.getResponse());
    }

    @Test
    public void showList_emptyList_headerOnly() {
        Ui ui = guiUi();
        ui.showList(List.of());
        assertEquals("Here's your canvas so far:", ui.getResponse());
    }

    @Test
    public void showList_severalTasks_numberedFromOne() {
        Ui ui = guiUi();
        ui.showList(toDos("read book", "return book"));
        assertEquals("Here's your canvas so far:" + NEWLINE
                + "1.[T][ ] read book" + NEWLINE
                + "2.[T][ ] return book", ui.getResponse());
    }

    @Test
    public void showMatchingTasks_noMatches_keywordNamed() {
        Ui ui = guiUi();
        ui.showMatchingTasks(List.of(), "book");
        assertEquals("No strokes match \"book\".", ui.getResponse());
    }

    @Test
    public void showAddedTask_anyTask_countIncluded() {
        Ui ui = guiUi();
        ui.showAddedTask(new ToDo("read book"), 3);
        assertEquals("A fresh stroke on the canvas:" + NEWLINE
                + "[T][ ] read book" + NEWLINE
                + "Your canvas now holds 3 tasks.", ui.getResponse());
    }

    @Test
    public void showDeleted_oneTaskLeft_singularCountShown() {
        // The count line says "1 task", not "1 tasks".
        Ui ui = guiUi();
        ui.showDeleted(new ToDo("read book"), 1);
        assertEquals("Painted over. I've removed this task:" + NEWLINE
                + "  [T][ ] read book" + NEWLINE
                + "Your canvas now holds 1 task.", ui.getResponse());
    }

    @Test
    public void showDeleted_noTasksLeft_pluralCountShown() {
        // Zero takes the plural, like every count other than one.
        Ui ui = guiUi();
        ui.showDeleted(new ToDo("read book"), 0);
        assertTrue(ui.getResponse().endsWith("Your canvas now holds 0 tasks."), ui.getResponse());
    }

    @Test
    public void showLoadWarning_twoSkippedLines_bothListed() {
        Ui ui = guiUi();
        ui.showLoadWarning(List.of("line 2: bad", "line 5: worse"));
        assertEquals("Warning: 2 line(s) in your saved data could not be loaded and were skipped:" + NEWLINE
                + "  - line 2: bad" + NEWLINE
                + "  - line 5: worse", ui.getResponse());
    }

    @Test
    public void showStats_nonDefaultWindow_windowTakenFromStats() {
        // The "due soon" day count belongs to StatsCommand; Ui must print
        // whatever window it is given rather than a hard-coded 7.
        Ui ui = guiUi();
        TaskStats stats = new TaskStats(toDos("read book", "walk dog"), List.of(), List.of(),
                3, List.of(), toDos("read book"));
        ui.showStats(stats);
        assertEquals("Stepping back to admire your canvas:" + NEWLINE
                + "Total tasks: 2 (ToDo: 2, Deadline: 0, Event: 0)" + NEWLINE
                + "Due in the next 3 days: none" + NEWLINE
                + "Oldest not done:" + NEWLINE
                + "1.[T][ ] read book", ui.getResponse());
    }

    // ---------------------------------------------------------------
    // Console echoing
    // ---------------------------------------------------------------

    @Test
    public void showError_echoingToConsole_framedByDividers() {
        // The scripted transcript checker asserts on these dividers, so the
        // framing has to survive any change to how messages are recorded.
        String printed = capturePrinted(() -> new Ui(true).showError("boom"));
        assertEquals(DIVIDER + CONSOLE_NEWLINE + "Smudge! boom" + CONSOLE_NEWLINE + DIVIDER + CONSOLE_NEWLINE,
                printed);
    }

    @Test
    public void showError_notEchoingToConsole_nothingPrinted() {
        String printed = capturePrinted(() -> guiUi().showError("boom"));
        assertEquals("", printed);
    }

    @Test
    public void showGreeting_echoingToConsole_bannerPrinted() {
        // The banner is left out of the recorded reply but must still reach
        // the console, where it does line up.
        String printed = capturePrinted(() -> new Ui(true).showGreeting());
        assertTrue(printed.contains("Hello! I'm Myriad, your task painter."));
        assertTrue(printed.contains("╚═╝"), "console output should include the ASCII banner");
    }
}
