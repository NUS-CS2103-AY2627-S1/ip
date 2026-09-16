package panda.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TaskFormattingTest {

    @Test
    void deadline_toString_displaysTypeAndDeadline() {
        Deadline deadline = new Deadline("submit report", "2/12/2019 1800");

        assertEquals("[D][ ] submit report (by: Dec 02 2019 6:00 PM)", deadline.toString());
    }

    @Test
    void deadline_toFileString_serializesInStorageFormat() {
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2019, 12, 2, 18, 0));

        assertEquals("D | 0 | submit report | 2019-12-02T18:00:00", deadline.toFileString());
    }

    @Test
    void event_toString_displaysFromAndToTimes() {
        Event event = new Event("team meeting",
                LocalDateTime.of(2019, 10, 16, 14, 0),
                LocalDateTime.of(2019, 10, 16, 16, 0));

        assertEquals("[E][ ] team meeting (from: Oct 16 2019 2:00 PM to: Oct 16 2019 4:00 PM)",
                event.toString());
    }

    @Test
    void event_toFileString_serializesStartAndEndTimes() {
        Event event = new Event("team meeting",
                LocalDateTime.of(2019, 10, 16, 14, 0),
                LocalDateTime.of(2019, 10, 16, 16, 0));

        assertEquals("E | 0 | team meeting | 2019-10-16T14:00:00 | 2019-10-16T16:00:00",
                event.toFileString());
    }

    @Test
    void task_markAsDone_updatesStatusAndDisplayString() {
        Task task = new Todo("read book");

        task.markAsDone();

        assertEquals("[T][X] read book", task.toString());
        assertEquals("T | 1 | read book", task.toFileString());
    }
}
