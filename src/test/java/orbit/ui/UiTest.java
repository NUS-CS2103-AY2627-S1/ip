package orbit.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import orbit.task.Deadline;
import orbit.task.Task;
import orbit.task.Todo;

/**
 * Tests the user-facing messages produced by {@link Ui}.
 */
class UiTest {
    private final Ui ui = new Ui();

    /**
     * Verifies Orbit's personality in its welcome and goodbye messages.
     */
    @Test
    void greetingAndGoodbye_returnsOrbitMissionControlMessages() {
        assertEquals("Orbit online.\nMission control is ready. What shall we accomplish?",
                ui.getWelcomeMessage());
        assertEquals("Orbit signing off. Clear skies!", ui.getGoodbyeMessage());
    }

    /**
     * Verifies that task listings use one-based numbering and preserve task order.
     */
    @Test
    void getTaskListMessage_multipleTasks_returnsNumberedFlightPlan() {
        List<Task> tasks = List.of(
                new Todo("calibrate telescope"),
                new Deadline("transmit report", LocalDate.parse("2026-10-09")));

        assertEquals("Here is your flight plan:\n"
                        + "1.[T][ ] calibrate telescope\n"
                        + "2.[D][ ] transmit report (by: Oct 9 2026)",
                ui.getTaskListMessage(tasks));
    }

    /**
     * Verifies correct singular and plural mission counts in confirmation messages.
     */
    @Test
    void getTaskAddedAndDeletedMessages_differentCounts_usesCorrectGrammar() {
        Task task = new Todo("calibrate telescope");

        assertEquals("Mission logged:\n  [T][ ] calibrate telescope\n"
                        + "Flight plan now contains 1 mission.",
                ui.getTaskAddedMessage(task, 1));
        assertEquals("Mission scrubbed from the flight plan:\n  [T][ ] calibrate telescope\n"
                        + "Flight plan now contains 0 missions.",
                ui.getTaskDeletedMessage(task, 0));
    }

    /**
     * Verifies that empty query results provide a clear response rather than an empty heading.
     */
    @Test
    void emptyTaskViews_returnsClearEmptyStateMessages() {
        LocalDate date = LocalDate.parse("2026-10-09");

        assertEquals("Flight plan is clear. No missions are currently logged.",
                ui.getTaskListMessage(List.of()));
        assertEquals("Scanner found no matching missions.",
                ui.getMatchingTasksMessage(List.of()));
        assertEquals("No missions are scheduled for Oct 9 2026.",
                ui.getScheduleMessage(date, List.of()));
    }
}
