package seedu.goat.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void status_toggleAndRepeat_serializesCorrectly() {
        Task task = new Todo("task");
        assertFalse(task.isDone());
        task.markAsDone();
        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("T | 1 | task", task.toFileString());
        task.markAsNotDone();
        task.markAsNotDone();
        assertEquals("[T][ ] task", task.toString());
        task.setDone(true);
        assertEquals("[X]", task.getStatusIcon());
    }

    @Test
    public void matching_turkishLocale_remainsCaseInsensitive() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            Task task = new Todo("FINISH IP");
            assertTrue(task.hasKeyword("finish"));
            assertTrue(task.isDuplicateOf(new Todo("finish ip")));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void isOn_eventBoundaryAndOutside_inclusiveDates() {
        LocalDate start = LocalDate.of(2026, 9, 18);
        Task task = new Event("trip", start, start.plusDays(1));
        assertFalse(task.isOn(start.minusDays(1)));
        assertTrue(task.isOn(start));
        assertTrue(task.isOn(start.plusDays(1)));
        assertFalse(task.isOn(start.plusDays(2)));
        assertFalse(task.isDuplicateOf(new Event("trip", start.minusDays(1), start.plusDays(1))));
    }
}
