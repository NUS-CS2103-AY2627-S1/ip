package tasktracker.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import tasktracker.exception.TaskTrackerException;

public class EventTest {

    @Test
    public void toString_validEvent_correctFormat() throws TaskTrackerException {
        TaskDateTime from = new TaskDateTime("2026-11-10 1400");
        TaskDateTime to = new TaskDateTime("2026-11-10 1600");
        Event event = new Event("project presentation", from, to);

        assertEquals("[E][ ] project presentation (from: Nov 10 2026, 2:00 PM to: Nov 10 2026, 4:00 PM)",
                event.toString());
    }

    @Test
    public void toFileFormat_markedEvent_correctFileString() throws TaskTrackerException {
        TaskDateTime from = new TaskDateTime("2026-11-10 1400");
        TaskDateTime to = new TaskDateTime("2026-11-10 1600");
        Event event = new Event("project presentation", from, to);
        event.markAsDone();

        assertEquals("E | 1 | project presentation | 2026-11-10 1400 | 2026-11-10 1600",
                event.toFileFormat());
    }
}
