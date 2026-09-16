package aider.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import aider.AiderException;

/** Tests task-list operations and date filtering. */
class TaskListTest {
    @Test
    void markAndUnmark_updateTaskStatus() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        tasks.mark("mark 1");
        assertEquals("[T][X] read book", tasks.get(0).toString());

        tasks.unmark("unmark 1");
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    void occurringOn_returnsDeadlinesAndOverlappingEvents() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("submit report", dateTime(2026, 9, 1, 9, 0)));
        tasks.add(new Event("conference", dateTime(2026, 8, 31, 23, 0),
                dateTime(2026, 9, 1, 1, 0)));

        assertEquals(2, tasks.occurringOn(LocalDate.of(2026, 9, 1)).size());
    }

    @Test
    void find_returnsCaseInsensitiveDescriptionMatches() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("Book dentist appointment"));
        tasks.add(new Todo("buy milk"));

        assertEquals(2, tasks.find("find book").size());
    }

    @Test
    void scheduledOn_returnsCaseInsensitiveScheduleMatches() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("submit report", dateTime(2026, 9, 1, 9, 0), "Friday"));
        tasks.add(new Event("conference", dateTime(2026, 9, 1, 10, 0),
                dateTime(2026, 9, 1, 12, 0), "Friday 10am", "Friday 12pm"));

        assertEquals(2, tasks.scheduledOn("frIdAy").size());
        assertEquals(0, tasks.scheduledOn("Monday").size());
    }

    @Test
    void add_rejectsDuplicateTask() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertThrows(AiderException.class, () -> tasks.add(new Todo("read book")));
    }

    @Test
    void mark_rejectsMissingAndOutOfRangeTaskNumbers() throws Exception {
        TaskList tasks = new TaskList();

        assertThrows(AiderException.class, () -> tasks.mark("mark"));
        assertThrows(AiderException.class, () -> tasks.mark("mark 1"));
    }

    private static java.time.LocalDateTime dateTime(int year, int month, int day, int hour,
            int minute) {
        return java.time.LocalDateTime.of(year, month, day, hour, minute);
    }
}
