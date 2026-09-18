package toothless.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import toothless.DisplaySymbols;

/**
 * Tests the observable representation and completion state of each task type.
 */
public class TaskModelTest {
    @Test
    public void todo_unicodeDescription_preservesDescriptionAndStatus() {
        Todo task = new Todo("Read 书 📚!");

        assertEquals("Read 书 📚!", task.getDescription());
        assertFalse(task.isDone());
        assertEquals("[T][ ] Read 书 📚!", task.toString());

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("[T][" + DisplaySymbols.getDoneMark() + "] Read 书 📚!", task.toString());

        task.unmarkAsDone();
        assertEquals("[T][ ] Read 书 📚!", task.toString());
    }

    @Test
    public void deadline_leapDay_formatsEnglishAndRetainsDateAfterMarking() {
        LocalDate date = LocalDate.of(2024, 2, 29);
        Deadline task = new Deadline("交报告", date);

        assertFalse(task.isDone());
        assertEquals(date, task.getBy());
        assertEquals("[D][ ] 交报告 (by: Feb 29 2024)", task.toString());

        task.markAsDone();
        assertEquals("[D][" + DisplaySymbols.getDoneMark() + "] 交报告 (by: Feb 29 2024)",
                task.toString());
    }

    @Test
    public void event_unicodeDescription_preservesEndpointsAndStatus() {
        Event task = new Event("Meet 朋友!", "31 December 2026 11pm", "1 January 2027 1am");

        assertFalse(task.isDone());
        assertEquals("31 December 2026 11pm", task.getFrom());
        assertEquals("1 January 2027 1am", task.getTo());
        assertEquals("[E][ ] Meet 朋友! (from: 31 December 2026 11pm to: 1 January 2027 1am)",
                task.toString());

        task.markAsDone();
        task.unmarkAsDone();
        assertEquals(" ", task.getStatusIcon());
        assertFalse(task.isDone());
    }
}
