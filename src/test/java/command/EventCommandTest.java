package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import exception.CbtException;
import task.TaskList;

/** Tests parsing and creation of event tasks. */
public class EventCommandTest {
    /** Verifies that valid event details add an event and report a state change. */
    @Test
    public void execute_validDetails_eventAdded() throws CbtException {
        TaskList tasks = new TaskList();

        CommandResult result = new EventCommand(
                "meeting /from 2/12/2019 0900 /to 2/12/2019 1000").execute(tasks);

        assertEquals(1, tasks.getSize());
        assertEquals("EVENT | 0 | meeting | 02/12/2019 0900 | 02/12/2019 1000",
                tasks.getTask(0).toFileFormat());
        assertTrue(result.isTaskListChanged());
    }

    /** Verifies that missing event markers or times are rejected. */
    @Test
    public void execute_missingMarkersOrTimes_exceptionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(CbtException.class, () -> new EventCommand("meeting").execute(tasks));
        assertThrows(CbtException.class, () -> new EventCommand("meeting /from 2/12/2019 /to ").execute(tasks));
        assertThrows(CbtException.class, () -> new EventCommand(
                "meeting /from 2/12/2019 /from 3/12/2019 /to 4/12/2019").execute(tasks));
    }

    /** Verifies that a reversed event range is rejected without changing the list. */
    @Test
    public void execute_startAfterEnd_exceptionThrownAndListUnchanged() {
        TaskList tasks = new TaskList();

        assertThrows(CbtException.class, () -> new EventCommand(
                "meeting /from 3/12/2019 1000 /to 2/12/2019 1000").execute(tasks));
        assertThrows(CbtException.class, () -> new EventCommand(
                "meeting /from 2/12/2019 1000 /to 2/12/2019 1000").execute(tasks));
        assertEquals(0, tasks.getSize());
    }
}
