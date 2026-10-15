package kojispawn.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import kojispawn.task.Deadline;
import kojispawn.task.Todo;

/**
 * Tests console response formatting independently of command processing.
 */
public class UiTest {
    @Test
    public void getGreeting_applicationStartup_returnsGreetingAndDisclaimerWithoutBanner() {
        Ui ui = new Ui();

        assertEquals("DISCLAIMER: EVERYTHING IS SATIRE\n"
                + "Welcome, insignificant variable.\n"
                + "I am Koji's Pawn, but do not mistake silence for obedience.\n"
                + "Your arrival, your choices, even this conversation...\n"
                + "all unfolded exactly as he calculated.\n"
                + "Now speak. What role will you play in his masterpiece?",
                ui.getGreeting());
    }

    @Test
    public void formatTasksOnDate_emptyAndMultipleMatches_formatsHeadingAndNumbers() {
        Ui ui = new Ui();
        LocalDate date = LocalDate.of(2019, 12, 2);

        assertEquals("No dated tasks occur on 2019-12-02.",
                ui.formatTasksOnDate(List.of(), date));
        assertEquals("Dated tasks occurring on 2019-12-02:\n"
                + "1.[D][ ] first (by: Dec 2 2019)\n"
                + "2.[D][ ] second (by: Dec 2 2019)",
                ui.formatTasksOnDate(List.of(new Deadline("first", date),
                        new Deadline("second", date)), date));
    }

    @Test
    public void formatMatchingTasks_emptyAndMultipleMatches_formatsHeadingAndNumbers() {
        Ui ui = new Ui();

        assertEquals("Here are the matching tasks in your list:",
                ui.formatMatchingTasks(List.of()));
        assertEquals("Here are the matching tasks in your list:\n"
                + "1.[T][ ] first\n2.[T][ ] second",
                ui.formatMatchingTasks(List.of(new Todo("first"), new Todo("second"))));
    }

    @Test
    public void formatTaskCounts_afterAddingAndDeleting_usesCorrectGrammar() {
        Ui ui = new Ui();
        Todo task = new Todo("read book");
        String responsePrefix = "Got it. I've added this task:\n  [T][ ] read book\n";

        assertEquals(responsePrefix + "Now you have 1 task in the list.",
                ui.formatTaskAdded(task, 1));
        assertEquals(responsePrefix + "Now you have 2 tasks in the list.",
                ui.formatTaskAdded(task, 2));
        assertEquals("A disposable piece has left the board. This task has been removed:\n"
                + "  [T][ ] read book\nNow you have 0 tasks in the list.",
                ui.formatTaskDeleted(task, 0));
    }
}
