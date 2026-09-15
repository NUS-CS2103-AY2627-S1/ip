package duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import duke.task.TaskList;
import duke.ui.CapturingUi;

public class EventCommandTest {

    @Test
    public void execute_validEvent_addsTask() {
        TaskList tasks = new TaskList();
        CapturingUi ui = new CapturingUi();

        new EventCommand("project meeting", "2026-08-25", "2026-08-26").execute(tasks, ui);

        assertNull(ui.getLastErrorMessage());
        assertEquals(1, tasks.size());
        assertEquals("E | 0 | project meeting | 2026-08-25 | 2026-08-26", tasks.getLast().toDataString());
        assertEquals(tasks.getLast(), ui.getLastAddedTask());
        assertEquals(1, ui.getLastAddedTaskCount());
    }

    @Test
    public void execute_validEventWithWhitespace_addsTask() {
        TaskList tasks = new TaskList();
        CapturingUi ui = new CapturingUi();

        new EventCommand("  project meeting  ", "  2026-08-25 ", " 2026-08-26  ").execute(tasks, ui);

        assertNull(ui.getLastErrorMessage());
        assertEquals(1, tasks.size());
        assertEquals("E | 0 | project meeting | 2026-08-25 | 2026-08-26", tasks.getLast().toDataString());
    }

    @Test
    public void execute_emptyDescription_showsError() {
        TaskList tasks = new TaskList();
        CapturingUi ui = new CapturingUi();

        new EventCommand("   ", "2026-08-25", "2026-08-26").execute(tasks, ui);

        assertEquals(0, tasks.size());
        assertEquals("""
                The Event description and dates cannot be empty!
                Use ' /from ' and ' /to ' to indicate start and end dates!
                """, ui.getLastErrorMessage());
        assertNull(ui.getLastAddedTask());
    }

    @Test
    public void execute_emptyDates_showsError() {
        TaskList tasks = new TaskList();
        CapturingUi ui = new CapturingUi();

        new EventCommand("project meeting", "   ", " ").execute(tasks, ui);

        assertEquals(0, tasks.size());
        assertEquals("""
                The Event description and dates cannot be empty!
                Use ' /from ' and ' /to ' to indicate start and end dates!
                """, ui.getLastErrorMessage());
    }

    @Test
    public void execute_invalidFormat_showsError() {
        TaskList tasks = new TaskList();
        CapturingUi ui = new CapturingUi();

        new EventCommand("project meeting", "2026/08/25", "2026-08-26").execute(tasks, ui);

        assertEquals(0, tasks.size());
        assertEquals("Please provide valid start and end dates in the format YYYY-MM-DD.", ui.getLastErrorMessage());
    }

    @Test
    public void execute_nonPaddedDate_showsError() {
        TaskList tasks = new TaskList();
        CapturingUi ui = new CapturingUi();

        new EventCommand("project meeting", "2026-8-5", "2026-08-26").execute(tasks, ui);

        assertEquals(0, tasks.size());
        assertEquals("Please provide valid start and end dates in the format YYYY-MM-DD.", ui.getLastErrorMessage());
    }

    @Test
    public void execute_startAfterEnd_showsError() {
        TaskList tasks = new TaskList();
        CapturingUi ui = new CapturingUi();

        new EventCommand("project meeting", "2026-08-27", "2026-08-26").execute(tasks, ui);

        assertEquals(0, tasks.size());
        assertEquals("The event start date must be before the end date.", ui.getLastErrorMessage());
    }

    @Test
    public void execute_startSameAsEnd_showsError() {
        TaskList tasks = new TaskList();
        CapturingUi ui = new CapturingUi();

        new EventCommand("project meeting", "2026-08-26", "2026-08-26").execute(tasks, ui);

        assertEquals(0, tasks.size());
        assertEquals("The event start date must be before the end date.", ui.getLastErrorMessage());
    }

    @Test
    public void execute_nonExistentDate_showsError() {
        TaskList tasks = new TaskList();
        CapturingUi ui = new CapturingUi();

        new EventCommand("project meeting", "2026-02-30", "2026-08-26").execute(tasks, ui);

        assertEquals(0, tasks.size());
        assertEquals("Please provide valid start and end dates in the format YYYY-MM-DD.", ui.getLastErrorMessage());
    }
}
