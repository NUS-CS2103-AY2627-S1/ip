package chatty.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import chatty.task.RecurrenceUnit;
import chatty.task.RecurringTask;
import chatty.task.Todo;

/**
 * Tests deterministic console and GUI response formatting.
 */
public class UiTest {
    /** Verifies task collection headings, numbering, and empty-list formatting. */
    @Test
    public void formatTaskCollections_tasksAndEmptyList_formattedCorrectly() {
        Ui ui = new Ui();
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("write notes");

        assertEquals(" Flight plan status:\n 1.[T][ ] read book\n 2.[T][ ] write notes",
                ui.formatTaskList(List.of(firstTask, secondTask)));
        assertEquals(" Radar found these matching tasks:\n 2.[T][ ] write notes",
                ui.formatMatchingTasks(List.of(secondTask), List.of(firstTask, secondTask)));
        assertEquals(" Radar found these matching tasks:",
                ui.formatMatchingTasks(List.of(), List.of(firstTask, secondTask)));
        assertEquals(" Flight plan status:", ui.formatTaskList(List.of()));
    }

    /** Verifies confirmations and errors use Chatty's expected voice and task details. */
    @Test
    public void formatResponses_taskOperations_chattyMessagesReturned() {
        Ui ui = new Ui();
        Todo task = new Todo("read book");
        RecurringTask recurringTask = new RecurringTask("team meeting",
                LocalDate.of(2026, 9, 21), 1, RecurrenceUnit.WEEK);

        assertEquals(" OOPS!!! Invalid command.", ui.formatError("OOPS!!! Invalid command."));
        assertEquals(" Chatty signing off. Keep your next milestone in sight!", ui.formatExit());
        assertEquals(" Task added to the flight plan:\n   [T][ ] read book\n"
                + " 1 task(s) now on board.", ui.formatTaskAdded(task, 1));
        task.markAsDone();
        assertEquals(" Milestone cleared:\n   [T][X] read book", ui.formatTaskMarked(task));
        task.markAsNotDone();
        assertEquals(" Task returned to active duty:\n   [T][ ] read book",
                ui.formatTaskUnmarked(task));
        assertEquals(" Task removed from the flight plan:\n   [T][ ] read book\n"
                + " 0 task(s) remain on board.", ui.formatTaskDeleted(task, 0));
        assertEquals(" Recurring milestone cleared. Next occurrence locked in:\n"
                + "   [R][ ] team meeting (on: Sep 21 2026, every: 1 week)",
                ui.formatRecurringTaskAdvanced(recurringTask));
    }
}
