package tasktracker.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import tasktracker.exception.TaskTrackerException;

public class DeadlineTest {

    @Test
    public void toString_validDeadline_correctFormat() throws TaskTrackerException {
        TaskDateTime by = new TaskDateTime("2026-10-15 2359");
        Deadline deadline = new Deadline("submit report", by);

        assertEquals("[D][ ] submit report (by: Oct 15 2026, 11:59 PM)", deadline.toString());
    }

    @Test
    public void toFileFormat_markedDeadline_correctFileString() throws TaskTrackerException {
        TaskDateTime by = new TaskDateTime("2026-10-15 2359");
        Deadline deadline = new Deadline("submit report", by);
        deadline.markAsDone();

        assertEquals("D | 1 | submit report | 2026-10-15 2359", deadline.toFileFormat());
    }
}
