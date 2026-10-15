package duncan;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import duncan.task.Deadline;
import duncan.task.Event;
import duncan.task.Task;
import duncan.task.Todo;

public class UiTest {
    private static final String NEWLINE = System.lineSeparator();

    @Test
    public void flushOutput_nothingShown_emptyStringReturned() {
        assertEquals("", new Ui().flushOutput());
    }

    @Test
    public void flushOutput_severalMessages_linesReturnedInOrder() {
        Ui ui = new Ui();

        ui.showTaskAdded(new Todo("read book"), 1);
        ui.showGoodbye();

        assertEquals("Got it. I've added this task:" + NEWLINE
                + "  [T][ ] read book" + NEWLINE
                + "Now you have 1 tasks in the list." + NEWLINE
                + "Bye. Hope to see you again soon!" + NEWLINE,
                ui.flushOutput());
    }

    @Test
    public void showMatchingTasks_matchesFromLaterInList_originalNumbersShown() {
        Ui ui = new Ui();
        Map<Integer, Task> matches = new LinkedHashMap<>();
        matches.put(1, new Todo("read book"));
        matches.put(4, new Deadline("return book", LocalDate.of(2019, 12, 2)));

        ui.showMatchingTasks(matches);

        assertEquals("Here are the matching tasks in your list:" + NEWLINE
                + "2.[T][ ] read book" + NEWLINE
                + "5.[D][ ] return book (by: Dec 2 2019)" + NEWLINE,
                ui.flushOutput());
    }

    @Test
    public void showTaskRescheduled_deadline_oldAndNewTaskShown() {
        Ui ui = new Ui();

        ui.showTaskRescheduled("[D][ ] return book (by: Dec 2 2019)",
                new Deadline("return book", LocalDate.of(2019, 12, 9)));

        assertEquals("OK, I've rescheduled this task:" + NEWLINE
                + "  from: [D][ ] return book (by: Dec 2 2019)" + NEWLINE
                + "  to:   [D][ ] return book (by: Dec 9 2019)" + NEWLINE,
                ui.flushOutput());
    }

    @Test
    public void showTaskSnoozed_event_daysAndOldAndNewTaskShown() {
        Ui ui = new Ui();

        ui.showTaskSnoozed("[E][ ] project fair (from: Dec 1 2019 to: Dec 2 2019)",
                new Event("project fair", LocalDate.of(2019, 12, 8), LocalDate.of(2019, 12, 9)), 7);

        assertEquals("OK, I've snoozed this task by 7 days:" + NEWLINE
                + "  from: [E][ ] project fair (from: Dec 1 2019 to: Dec 2 2019)" + NEWLINE
                + "  to:   [E][ ] project fair (from: Dec 8 2019 to: Dec 9 2019)" + NEWLINE,
                ui.flushOutput());
    }

    @Test
    public void flushOutput_calledTwice_bufferClearedAfterFirstCall() {
        Ui ui = new Ui();
        ui.showError("HEY! this task number is bad");

        assertEquals("HEY! this task number is bad" + NEWLINE, ui.flushOutput());
        assertEquals("", ui.flushOutput());
    }

    @Test
    public void flushOutput_warningShown_sameTextAsAnError() {
        Ui warningUi = new Ui();
        Ui errorUi = new Ui();

        warningUi.showWarning("WARNING: that change was made but NOT saved: disk full");
        errorUi.showError("WARNING: that change was made but NOT saved: disk full");

        assertEquals(errorUi.flushOutput(), warningUi.flushOutput());
    }

    @Test
    public void flushLines_mixedMessages_eachLineKeepsItsSeverity() {
        Ui ui = new Ui();

        ui.showTaskAdded(new Todo("read book"), 1);
        ui.showWarning("WARNING: not saved");
        ui.showError("HEY! bad");

        assertEquals(List.of(
                new Ui.MessageLine("Got it. I've added this task:", Ui.Severity.NORMAL),
                new Ui.MessageLine("  [T][ ] read book", Ui.Severity.NORMAL),
                new Ui.MessageLine("Now you have 1 tasks in the list.", Ui.Severity.NORMAL),
                new Ui.MessageLine("WARNING: not saved", Ui.Severity.WARNING),
                new Ui.MessageLine("HEY! bad", Ui.Severity.ERROR)),
                ui.flushLines());
    }

    @Test
    public void flushLines_calledTwice_bufferClearedAfterFirstCall() {
        Ui ui = new Ui();
        ui.showError("HEY! bad");

        ui.flushLines();

        assertEquals(List.of(), ui.flushLines());
        assertEquals("", ui.flushOutput());
    }

    @Test
    public void getHighestSeverity_noLines_normal() {
        assertEquals(Ui.Severity.NORMAL, Ui.getHighestSeverity(List.of()));
    }

    @Test
    public void getHighestSeverity_normalAndWarning_warning() {
        List<Ui.MessageLine> lines = List.of(
                new Ui.MessageLine("Got it.", Ui.Severity.NORMAL),
                new Ui.MessageLine("WARNING: not saved", Ui.Severity.WARNING));

        assertEquals(Ui.Severity.WARNING, Ui.getHighestSeverity(lines));
    }

    @Test
    public void getHighestSeverity_warningAndError_errorWhicheverComesFirst() {
        Ui.MessageLine warning = new Ui.MessageLine("WARNING: not saved", Ui.Severity.WARNING);
        Ui.MessageLine error = new Ui.MessageLine("HEY! bad", Ui.Severity.ERROR);

        assertEquals(Ui.Severity.ERROR, Ui.getHighestSeverity(List.of(warning, error)));
        assertEquals(Ui.Severity.ERROR, Ui.getHighestSeverity(List.of(error, warning)));
    }

    @Test
    public void getBanner_anyUi_eightLinesWithoutTrailingLineBreak() {
        String banner = new Ui().getBanner();

        assertEquals(8, banner.split("\n", -1).length);
        assertEquals("/", banner.substring(1, 2));
    }
}
