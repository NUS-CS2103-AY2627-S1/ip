package ubis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Tests the creation and parsing of tasks using variable argument segments.
 */
public class TaskTest {

    @Test
    public void initialise_todoSegments_returnsTodo() {
        Task task = Task.initialise("T", "0", "read book");
        assertNotNull(task);
        assertEquals("{T}{0}{read book}", task.stringify());
    }

    @Test
    public void initialise_deadlineSegments_returnsDeadline() {
        Task task = Task.initialise("D", "1", "submit assignment", "2026-09-01");
        assertNotNull(task);
        assertEquals("{D}{1}{submit assignment}{2026-09-01}", task.stringify());
    }

    @Test
    public void initialise_eventSegments_returnsEvent() {
        Task task = Task.initialise("E", "0", "hackathon", "2026-09-01", "2026-09-03");
        assertNotNull(task);
        assertEquals("{E}{0}{hackathon}{2026-09-01}{2026-09-03}", task.stringify());
    }

    @Test
    public void initialise_insufficientSegments_returnsNull() {
        assertNull(Task.initialise("T", "0"));
        assertNull(Task.initialise("D", "0", "deadline without date"));
        assertNull(Task.initialise("E", "0", "event without end date", "2026-09-01"));
    }

    @Test
    public void initialise_invalidSymbol_returnsNull() {
        assertNull(Task.initialise("X", "0", "unknown task"));
    }

    @Test
    public void initialise_invalidStatus_returnsNull() {
        assertNull(Task.initialise("T", "done", "read book"));
        assertNull(Task.initialise("T", "2", "read book"));
    }

    @Test
    public void initialise_blankName_returnsNull() {
        assertNull(Task.initialise("T", "0", "   "));
    }

    @Test
    public void initialise_unexpectedExtraFields_returnsNull() {
        assertNull(Task.initialise("T", "0", "read book", "unexpected"));
        assertNull(Task.initialise("D", "0", "submit", "2026-09-01", "unexpected"));
        assertNull(Task.initialise("E", "0", "trip", "2026-09-01", "2026-09-03", "unexpected"));
    }

    @Test
    public void initialise_nullArray_returnsNull() {
        assertNull(Task.initialise((String[]) null));
    }

    @Test
    public void initialise_invalidStoredDates_returnsNull() {
        assertNull(Task.initialise("D", "0", "report", "bad-date"));
        assertNull(Task.initialise("E", "0", "trip", "2026-09-03", "2026-09-01"));
        assertNull(Task.initialise("E", "0", "trip", "2026-09-01", "bad-date"));
    }

    @Test
    public void initialise_eachCompletionState_restoresDisplayAndStorage() {
        for (String status : new String[] {"0", "1"}) {
            String marker = status.equals("1") ? "X" : " ";
            Task todo = Task.initialise("T", status, "read book");
            Task deadline = Task.initialise("D", status, "report", "2026-09-30");
            Task event = Task.initialise("E", status, "trip", "2026-09-01", "2026-09-03");
            assertEquals("[T][" + marker + "] read book", todo.toString());
            assertEquals("[D][" + marker + "] report (by: Sep 30 2026)", deadline.toString());
            assertEquals("[E][" + marker + "] trip (from: Sep 1 2026 to: Sep 3 2026)", event.toString());
            assertEquals("{T}{" + status + "}{read book}", todo.stringify());
            assertEquals("{D}{" + status + "}{report}{2026-09-30}", deadline.stringify());
            assertEquals("{E}{" + status + "}{trip}{2026-09-01}{2026-09-03}", event.stringify());
        }
    }
}
