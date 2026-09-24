package murphy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests duplicate lookup without changing the task list's ordering behavior. */
class TaskListTest {
    @Test
    void findDuplicate_ignoresCaseAndCompletionStatus() {
        Todo existing = new Todo("Buy milk");
        existing.markAsDone();
        TaskList tasks = new TaskList();
        tasks.add(existing);

        Task duplicate = tasks.findDuplicate(new Todo(" buy milk "));

        assertEquals(existing, duplicate);
    }

    @Test
    void findDuplicate_considersTypeAndTaskDetails() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit report", LocalDate.of(2026, 9, 10)));

        assertNull(tasks.findDuplicate(new Todo("submit report")));
        assertNull(tasks.findDuplicate(new Deadline("submit report", LocalDate.of(2026, 9, 11))));
    }

    @Test
    void findDuplicate_comparesEventFields() {
        TaskList tasks = new TaskList();
        tasks.add(new Event("Team meeting", "2pm", "4pm"));

        assertEquals("[E][ ] Team meeting (from: 2pm to: 4pm)",
                tasks.findDuplicate(new Event("team meeting", "2PM", "4PM")).toString());
        assertNull(tasks.findDuplicate(new Event("team meeting", "2pm", "5pm")));
    }
}
