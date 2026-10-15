package nutrisoy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/** Verifies model identity, display formats, tag behavior, and ordered list operations. */
public class TaskModelTest {
    @Test
    public void task_completionChanges_displayAndStorageAgree() {
        Task task = new Task("read");
        assertEquals("read", task.getDescription());
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read", task.toString());
        assertEquals("0 | read", task.toFileFormat());
        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] read", task.toString());
        assertEquals("1 | read", task.toFileFormat());
        task.unmarkAsDone();
        assertFalse(task.isDone());
    }

    @Test
    public void datedTasks_displayAndStorage_includeDatesAndTags() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.ENGLISH);
            Deadline deadline = new Deadline("report", LocalDate.of(2028, 2, 29));
            deadline.addTag("work");
            deadline.markAsDone();
            assertEquals("[D][X] report #work (by: Feb 29 2028)", deadline.toString());
            assertEquals("D | 1 | report | 2028-02-29 | work", deadline.toFileFormat());
            Event event = new Event("trip", LocalDate.of(2026, 12, 31), LocalDate.of(2027, 1, 1));
            assertEquals("[E][ ] trip (from: Dec 31 2026 to: Jan 01 2027)", event.toString());
            assertEquals("E | 0 | trip | 2026-12-31 | 2027-01-01", event.toFileFormat());
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void tags_caseVariants_removeAndReaddPreservesOrder() {
        Task task = new Todo("read");
        task.addTag("Work");
        task.addTag("work");
        task.addTag("school");
        task.removeTag("absent");
        assertTrue(task.hasTag("WORK"));
        assertEquals("T | 0 | read | Work,school", task.toFileFormat());
        task.removeTag("WORK");
        assertFalse(task.hasTag("work"));
        task.addTag("work");
        assertEquals("T | 0 | read | school,work", task.toFileFormat());
        task.removeTag("school");
        task.removeTag("work");
        assertEquals("[T][ ] read", task.toString());
        assertEquals("T | 0 | read", task.toFileFormat());
    }

    @Test
    public void tagNames_supportedAndReservedCharacters_validated() {
        for (String tag : List.of("a", "1", "School", "project-2_notes")) {
            assertTrue(Task.isValidTagName(tag), tag);
        }
        for (String tag : List.of("", " ", "a b", "#work", "a,b", "a|b", "-work", "_work", "a\nb")) {
            assertFalse(Task.isValidTagName(tag), tag);
        }
        assertFalse(Task.isValidTagName(null));
    }

    @Test
    public void identity_typeDescriptionAndDates_determineDuplicates() {
        LocalDate first = LocalDate.of(2026, 9, 20);
        LocalDate second = first.plusDays(1);
        Task todo = new Todo("Read");
        Task completed = new Todo("read");
        completed.markAsDone();
        completed.addTag("work");
        assertTrue(todo.hasSameDetails(completed));
        assertFalse(todo.hasSameDetails(null));
        assertFalse(todo.hasSameDetails(new Todo("write")));
        assertFalse(todo.hasSameDetails(new Deadline("Read", first)));
        Deadline deadline = new Deadline("Read", first);
        assertTrue(deadline.hasSameDetails(new Deadline("read", first)));
        assertFalse(deadline.hasSameDetails(new Deadline("Read", second)));
        assertFalse(deadline.hasSameDetails(todo));
        Event event = new Event("Read", first, second);
        assertTrue(event.hasSameDetails(new Event("read", first, second)));
        assertFalse(event.hasSameDetails(new Event("Read", first.minusDays(1), second)));
        assertFalse(event.hasSameDetails(new Event("Read", first, second.plusDays(1))));
        assertFalse(event.hasSameDetails(deadline));
    }

    @Test
    public void taskList_initialTasksAndRemoval_preservesIdentityAndOrder() {
        Task first = new Todo("first");
        Task middle = new Todo("middle");
        Task last = new Todo("last");
        TaskList tasks = new TaskList(new ArrayList<>(List.of(first, middle, last)));
        assertSame(first, tasks.get(0));
        assertTrue(tasks.containsSameTask(new Todo("FIRST")));
        assertFalse(tasks.containsSameTask(new Todo("missing")));
        assertSame(middle, tasks.remove(1));
        assertEquals(List.of(first, last), tasks.getTasks());
        assertFalse(tasks.isEmpty());
        tasks.remove(1);
        tasks.remove(0);
        assertTrue(tasks.isEmpty());
        assertFalse(tasks.containsSameTask(first));
    }
}
