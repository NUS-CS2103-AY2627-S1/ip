package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/** Tests task status changes and user-facing task formatting. */
class TaskTest {
    @Test
    void status_newTaskIsNotDone_markAndUnmarkUpdateStatus() {
        Task task = new Task("read book");

        assertEquals(" ", task.getStatusIcon());
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void toString_todoIncludesDescriptionAndStatus() {
        Task task = new Task("read book");

        assertEquals("[T][ ] read book", task.toString());
        task.markAsDone();
        assertEquals("[T][X] read book", task.toString());
    }

    @Test
    void toString_deadlineIncludesFormattedDateTime() {
        Task task = new Task("return book", LocalDateTime.of(2019, 12, 2, 18, 0));

        assertEquals("[D][ ] return book (by: Dec 2 2019 6:00 PM)", task.toString());
    }

    @Test
    void toString_eventIncludesStartAndEndDetails() {
        Task task = new Task(TaskType.EVENT, "project meeting", null,
                "Mon 2pm", "4pm");

        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", task.toString());
    }

    @Test
    void byDeadline_sortsParsedDeadlinesChronologically() {
        Task later = new Task("later", LocalDateTime.of(2026, 9, 20, 9, 0));
        Task earlier = new Task("earlier", LocalDateTime.of(2026, 9, 10, 9, 0));
        Task todo = new Task("no deadline");
        ArrayList<Task> tasks = new ArrayList<>(java.util.List.of(later, todo, earlier));

        tasks.sort(Task.byDeadline());

        assertEquals("earlier", tasks.get(0).description);
        assertEquals("later", tasks.get(1).description);
        assertEquals("no deadline", tasks.get(2).description);
    }
}
